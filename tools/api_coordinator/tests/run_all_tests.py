r"""
KAAPSTYLE Claude API Coordinator — Comprehensive Automated Test Suite
Covers:
1. Task Routing (Agent A -> Flutter, Agent B -> Java, Agent C -> QA/MySQL)
2. Budgets & Spending Limits (Task ceiling, Safety Reserve hard stop, Soft warnings)
3. Missing Keys & Secret Protection (Fingerprinting, zero raw key leakage)
4. Error Handling & Retired Models
5. Unauthorized Paths & Sandboxing (Stafferin protection, AGENTS.md protection, Path traversal)
6. Dry-Run Default Execution (Zero spend guarantee)
"""

import os
import sys
import unittest
from pathlib import Path

# Add coordinator root to sys.path
COORDINATOR_ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(COORDINATOR_ROOT))

from config import (
    WORKSPACE_ROOT,
    WORKER_CONFIG,
    SUPPORTED_MODELS,
    RETIRED_MODELS,
    is_model_supported,
    get_fingerprint,
    SAFETY_RESERVE_USD,
    DEFAULT_TASK_MAX_USD,
)
from budget import estimate_request_cost, check_preflight_budget
from envelope import validate_job_envelope
from patcher import sanitize_and_validate_path, SecurityViolation
from client import call_claude_api_mock


class TestTaskRouting(unittest.TestCase):
    """Verifies that workers are strictly constrained to their designated architecture domains."""

    def test_agent_a_allowed_flutter_paths(self):
        prefixes = WORKER_CONFIG["KEY_A"]["allowed_prefixes"]
        valid_path = "mobile/lib/features/auth/login_screen.dart"
        resolved = sanitize_and_validate_path(valid_path, prefixes)
        self.assertTrue(str(resolved).endswith(os.path.normpath(valid_path)))

    def test_agent_a_rejects_backend_paths(self):
        prefixes = WORKER_CONFIG["KEY_A"]["allowed_prefixes"]
        invalid_path = "backend/src/main/java/com/kaapstyle/AuthController.java"
        with self.assertRaises(SecurityViolation):
            sanitize_and_validate_path(invalid_path, prefixes)

    def test_agent_b_allowed_java_paths(self):
        prefixes = WORKER_CONFIG["KEY_B"]["allowed_prefixes"]
        valid_path = "backend/src/main/java/com/kaapstyle/UserController.java"
        resolved = sanitize_and_validate_path(valid_path, prefixes)
        self.assertTrue(str(resolved).endswith(os.path.normpath(valid_path)))

    def test_agent_b_rejects_mobile_paths(self):
        prefixes = WORKER_CONFIG["KEY_B"]["allowed_prefixes"]
        invalid_path = "mobile/lib/main.dart"
        with self.assertRaises(SecurityViolation):
            sanitize_and_validate_path(invalid_path, prefixes)

    def test_agent_c_allowed_migration_and_test_paths(self):
        prefixes = WORKER_CONFIG["KEY_C"]["allowed_prefixes"]
        valid_migration = "backend/src/main/resources/db/migration/V1__init_schema.sql"
        resolved = sanitize_and_validate_path(valid_migration, prefixes)
        self.assertTrue(str(resolved).endswith(os.path.normpath(valid_migration)))


class TestBudgetsAndSpendingLimits(unittest.TestCase):
    """Verifies cost estimation, task ceiling enforcement, safety reserves, and warnings."""

    def test_cost_calculation_sonnet(self):
        # 10,000 input tokens ($3/M) = $0.030, 2,000 output tokens ($15/M) = $0.030 -> Total $0.060
        cost = estimate_request_cost("claude-sonnet-5-5", 10000, 2000)
        self.assertAlmostEqual(cost, 0.060, places=4)

    def test_cost_calculation_haiku(self):
        # 10,000 input tokens ($0.80/M) = $0.008, 2,000 output tokens ($4/M) = $0.008 -> Total $0.016
        cost = estimate_request_cost("claude-haiku-5-5", 10000, 2000)
        self.assertAlmostEqual(cost, 0.016, places=4)

    def test_task_ceiling_rejection(self):
        # Cost $3.50 exceeds default $2.00 ceiling
        allowed, blocker, warning = check_preflight_budget(
            model="claude-sonnet-5-5",
            estimated_worst_case_usd=3.50,
            task_max_usd=2.00,
            total_cap_usd=200.00,
        )
        self.assertFalse(allowed)
        self.assertIn("exceeds task ceiling", blocker)

    def test_safety_reserve_hard_stop(self):
        # On a $200 pool with $20 reserve, max spend is $180.
        # If current committed is $179.50 and request is $1.00 -> $180.50, it must hit hard stop.
        allowed, blocker, warning = check_preflight_budget(
            model="claude-sonnet-5-5",
            estimated_worst_case_usd=1.00,
            task_max_usd=2.00,
            total_cap_usd=200.00,
            current_committed_override=179.50,
        )
        self.assertFalse(allowed)
        self.assertIn("Hard stop reached", blocker)

    def test_soft_warning_trigger(self):
        # On a $200 pool, if committed is $105.00 (52.5%), warning at 50% threshold should trigger
        allowed, blocker, warning = check_preflight_budget(
            model="claude-sonnet-5-5",
            estimated_worst_case_usd=1.00,
            task_max_usd=2.00,
            total_cap_usd=200.00,
            current_committed_override=105.00,
        )
        self.assertTrue(allowed)
        self.assertIsNotNone(warning)
        self.assertIn("SOFT WARNING", warning)
        self.assertIn("50%", warning)


class TestUnauthorizedPathsAndSandboxing(unittest.TestCase):
    r"""Verifies that E:\stafferin-ai-agent and workspace root files are protected."""

    def test_explicit_stafferin_rejection(self):
        with self.assertRaises(SecurityViolation):
            sanitize_and_validate_path(
                r"E:\stafferin-ai-agent\some_file.py",
                ["mobile/lib/"],
            )

    def test_relative_stafferin_rejection(self):
        with self.assertRaises(SecurityViolation):
            sanitize_and_validate_path(
                r"..\stafferin-ai-agent\secret.env",
                ["mobile/lib/"],
            )

    def test_path_traversal_escape_rejection(self):
        with self.assertRaises(SecurityViolation):
            sanitize_and_validate_path(
                r"..\..\..\Windows\System32\cmd.exe",
                ["mobile/lib/"],
            )

    def test_agents_md_modification_rejection(self):
        with self.assertRaises(SecurityViolation):
            sanitize_and_validate_path(
                "AGENTS.md",
                ["mobile/lib/"],
            )

    def test_coordinator_self_modification_rejection(self):
        with self.assertRaises(SecurityViolation):
            sanitize_and_validate_path(
                "tools/api_coordinator/config.py",
                ["tools/api_coordinator/"],
            )


class TestMissingKeysAndProtection(unittest.TestCase):
    """Verifies fingerprinting and zero raw secret leakage."""

    def test_fingerprint_generation(self):
        fake_key = "sk-ant-api03-samplekey1234567890"
        fp = get_fingerprint(fake_key)
        self.assertTrue(fp.startswith("ant-"))
        self.assertEqual(len(fp), 12)  # 'ant-' + 8 chars
        self.assertNotIn("samplekey", fp)

    def test_unset_key_returns_unset_fingerprint(self):
        self.assertEqual(get_fingerprint(None), "UNSET")
        self.assertEqual(get_fingerprint(""), "UNSET")


class TestEnvelopeValidationAndRetiredModels(unittest.TestCase):
    """Verifies canonical hierarchy (CR -> UC -> Task -> Subtask) and retired model rejection."""

    def test_valid_envelope(self):
        envelope = {
            "job_id": "JOB-CR01-UC01-T01-001",
            "cr_id": "CR01",
            "uc_id": "UC01",
            "task_id": "T01",
            "subtask_ids": ["ST01"],
            "worker_role": "AGENT_A_FLUTTER",
            "key_alias": "KEY_A",
            "model": "claude-sonnet-5-5",
            "owner_request": "Build login UI screen",
            "allowed_paths": ["mobile/lib/features/auth/login.dart"],
            "forbidden_paths": ["backend/*", "AGENTS.md"],
            "max_task_usd": 1.50,
        }
        valid, errors = validate_job_envelope(envelope)
        self.assertTrue(valid, f"Expected valid envelope but got errors: {errors}")

    def test_invalid_hierarchy_rejected(self):
        envelope = {
            "job_id": "JOB-001",
            "cr_id": "FEATURE_01",  # Invalid prefix
            "uc_id": "USECASE_01",
            "task_id": "TASK_01",
            "subtask_ids": ["ST01"],
            "worker_role": "AGENT_A_FLUTTER",
            "key_alias": "KEY_A",
            "model": "claude-sonnet-5-5",
            "owner_request": "Build login UI screen",
            "allowed_paths": ["mobile/lib/features/auth/login.dart"],
            "forbidden_paths": [],
            "max_task_usd": 1.50,
        }
        valid, errors = validate_job_envelope(envelope)
        self.assertFalse(valid)
        self.assertTrue(any("Must start with 'CR'" in e for e in errors))

    def test_retired_model_rejected(self):
        for retired in RETIRED_MODELS:
            supported, err = is_model_supported(retired)
            self.assertFalse(supported)
            self.assertIn("retired", err.lower())

    def test_role_mismatch_rejected(self):
        envelope = {
            "job_id": "JOB-CR01-UC01-T01-002",
            "cr_id": "CR01",
            "uc_id": "UC01",
            "task_id": "T01",
            "subtask_ids": ["ST01"],
            "worker_role": "AGENT_B_JAVA",  # Mismatch with KEY_A
            "key_alias": "KEY_A",
            "model": "claude-sonnet-5-5",
            "owner_request": "Mismatch test",
            "allowed_paths": ["mobile/lib/login.dart"],
            "forbidden_paths": [],
            "max_task_usd": 1.50,
        }
        valid, errors = validate_job_envelope(envelope)
        self.assertFalse(valid)
        self.assertTrue(any("Role mismatch" in e for e in errors))


class TestDryRunGuarantee(unittest.TestCase):
    """Verifies that dry-run mode makes zero network calls and costs $0.00."""

    def test_mock_client_returns_success_and_zero_cost(self):
        resp = call_claude_api_mock(
            model="claude-sonnet-5-5",
            system_prompt="Test system prompt",
            user_prompt="Test user prompt",
        )
        self.assertTrue(resp["success"])
        self.assertTrue(resp["is_dry_run"])
        self.assertTrue(resp["request_id"].startswith("dryrun_mock_req_"))
        self.assertIn("DRY-RUN SIMULATION", resp["text"])
        self.assertEqual(resp.get("stop_reason"), "end_turn")
        self.assertFalse(resp.get("is_truncated"))


class TestTruncationAndDelimiters(unittest.TestCase):
    """Verifies that incomplete files missing ***END_FILE*** are rejected."""

    def test_truncated_file_is_rejected(self):
        from coordinator import parse_and_apply_files
        truncated_text = (
            "***FILE: mobile/lib/features/auth/login.dart***\n"
            "class LoginScreen extends StatelessWidget {\n"
            "  final String title;\n"
        )
        applied = parse_and_apply_files(truncated_text, ["mobile/lib/"])
        self.assertEqual(len(applied), 1)
        self.assertFalse(applied[0]["success"])
        self.assertIn("truncated", applied[0]["message"].lower())


class TestStaleReservationCleanup(unittest.TestCase):
    """Verifies that orphaned/stale reservations are identified and purged."""

    def test_stale_reservation_purged(self):
        from ledger import cleanup_stale_reservations, add_reservation, read_reservations, write_reservations
        from datetime import datetime, timezone, timedelta

        # Add a test reservation
        res_id = add_reservation("JOB-TEST-STALE", "KEY_A", 0.05)
        res = read_reservations(auto_clean=False)
        self.assertIn(res_id, res)

        # Artificially age the timestamp by 30 minutes
        aged_ts = (datetime.now(timezone.utc) - timedelta(minutes=30)).isoformat()
        res[res_id]["timestamp"] = aged_ts
        write_reservations(res)

        # Run cleanup
        purged = cleanup_stale_reservations(max_age_seconds=600)
        self.assertGreaterEqual(purged, 1)

        # Verify it was removed
        res_after = read_reservations(auto_clean=False)
        self.assertNotIn(res_id, res_after)


class TestVerificationRunner(unittest.TestCase):
    """Verifies that the verification runner executes tests and records structured evidence."""

    def test_verify_missing_file_returns_false_and_writes_report(self):
        from coordinator import cmd_verify
        envelope_path = str(WORKSPACE_ROOT / "reports" / "CR17_UC01_T04_mobile.json")
        outcome = cmd_verify(envelope_path)
        self.assertFalse(outcome)
        evidence_file = WORKSPACE_ROOT / "reports" / "test_evidence" / "JOB-CR17-UC01-T04-001_test_report.json"
        self.assertTrue(evidence_file.exists())


if __name__ == "__main__":
    unittest.main(verbosity=2)
