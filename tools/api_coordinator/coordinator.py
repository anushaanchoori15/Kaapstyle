r"""
KAAPSTYLE Claude API Coordinator — Main CLI Entrypoint
Strictly complies with E:/KAAPSTYLE/AGENTS.md.
Provides: doctor, account-check, plan, route, execute (dry-run default), status, pause, resume, report, smoke-test.
Dry-run is the DEFAULT. Live API calls require explicit --live flag.
"""

import sys
import os
import re
import json
import time
import argparse
import shutil
import subprocess
import urllib.request
from pathlib import Path
from datetime import datetime, timezone

from config import (
    WORKSPACE_ROOT,
    REPORTS_DIR,
    LEDGER_FILE,
    EVIDENCE_DIR,
    WORKER_CONFIG,
    SUPPORTED_MODELS,
    RETIRED_MODELS,
    get_worker_status,
    get_raw_key,
    get_fingerprint,
    SAFETY_RESERVE_USD,
    DEFAULT_TOTAL_POOL_CAP_USD,
    DEFAULT_TASK_MAX_USD,
)
from ledger import (
    ensure_directories,
    add_reservation,
    remove_reservation,
    cleanup_stale_reservations,
    append_ledger_record,
    get_summary,
    get_ledger_records,
)
from budget import check_preflight_budget, estimate_request_cost
from client import call_claude_api, call_claude_api_mock
from envelope import validate_job_envelope
from patcher import safe_apply_file_write, sanitize_and_validate_path, SecurityViolation

PAUSE_LOCK_FILE = REPORTS_DIR / "pause.lock"


def parse_and_apply_files(text: str, allowed_prefixes: list) -> list:
    """Parses file delimiters and safely writes approved files within allowed prefixes."""
    applied = []
    parts = re.split(r"\*\*\*FILE:\s*([^\*\r\n]+)\*\*\*", text)
    for i in range(1, len(parts), 2):
        path_str = parts[i].strip().replace("\\", "/")
        raw_content = parts[i+1]
        if "***END_FILE***" not in raw_content:
            applied.append({
                "path": path_str,
                "success": False,
                "message": "REJECTED: File content is truncated (missing ***END_FILE*** delimiter). Token limit exceeded."
            })
            continue
        clean_content = raw_content.split("***END_FILE***")[0].strip()
        if clean_content.startswith("```"):
            lines = clean_content.splitlines()
            if len(lines) >= 2 and lines[0].startswith("```"):
                if lines[-1].strip() == "```":
                    clean_content = "\n".join(lines[1:-1])
                else:
                    clean_content = "\n".join(lines[1:])
        ok, msg = safe_apply_file_write(path_str, clean_content, allowed_prefixes)
        applied.append({"path": path_str, "success": ok, "message": msg})

    return applied


def cmd_doctor():
    """Runs zero-spend environment and dependency diagnostics."""
    ensure_directories()
    print("=" * 65)
    print(" KAAPSTYLE API COORDINATOR — DOCTOR PREFLIGHT")
    print("=" * 65)

    print(f"\n[+] Python Runtime: {sys.version.split()[0]} ({sys.executable})")

    tools = {
        "Flutter": "flutter",
        "Dart": "dart",
        "Java (JDK)": "java",
        "Maven": "mvn",
        "Gradle": "gradle",
        "MySQL": "mysql",
        "Git": "git",
    }
    print("\n[+] Developer Toolchain Detection:")
    for name, cmd in tools.items():
        loc = shutil.which(cmd)
        if loc:
            print(f"  - {name:<12}: FOUND ({loc})")
        else:
            print(f"  - {name:<12}: NOT IN PATH")

    print("\n[+] Worker API Keys & Dedicated Routing:")
    status = get_worker_status()
    all_configured = True
    for alias, info in status.items():
        state = "CONFIGURED" if info["configured"] else "UNSET"
        ded = "DEDICATED" if info["dedicated"] else "FALLBACK/SHARED"
        if not info["configured"]:
            all_configured = False
        print(f"  - {alias} | {info['role']:<32} | State: {state:<10} | Mode: {ded:<15} | Fingerprint: {info['fingerprint']}")

    print("\n[+] Supported Claude Models & Pricing:")
    for m_id, m_info in SUPPORTED_MODELS.items():
        print(f"  - {m_id:<30} (${m_info['input']:.2f}/M in, ${m_info['output']:.2f}/M out)")

    print("\n[+] Directories & Storage:")
    print(f"  - Workspace: {WORKSPACE_ROOT} (OK)")
    print(f"  - Reports:   {REPORTS_DIR} (OK)")
    print(f"  - Ledger:    {LEDGER_FILE} (Exists: {LEDGER_FILE.exists()})")

    routing_state = "CONFIGURED (Keys Present)" if all_configured else "API ROUTING NOT CONFIGURED"
    print(f"\n--> Overall Status: {routing_state}")
    print("=" * 65)


def cmd_status():
    """Displays spend summary by key alias, pool balance, and budget warnings."""
    ensure_directories()
    summary = get_summary()
    status = get_worker_status()

    is_paused = PAUSE_LOCK_FILE.exists()
    all_configured = all(info["configured"] for info in status.values())
    routing_label = "BLOCKED (Paused)" if is_paused else ("CONFIGURED & VERIFIED" if all_configured else "NOT CONFIGURED")

    print("=" * 65)
    print(" KAAPSTYLE | Status Report")
    print("=" * 65)
    print(f"API routing: {routing_label}")
    print(f"Total Provider Requests: {summary['entry_count']}")
    print(f"Active In-Flight Reservations: ${summary['active_reserved_usd']:.4f}")
    print(f"Total Estimated Spend: ${summary['total_spent_usd']:.4f}")
    print(f"Remaining Budget: ${summary['remaining_usd']:.4f} (Safety Reserve: ${SAFETY_RESERVE_USD:.2f})")
    print("\nSpend By Key Alias:")
    for alias, spent in summary["spend_by_key_usd"].items():
        fp = status.get(alias, {}).get("fingerprint", "UNSET")
        print(f"  - {alias} ({fp}): ${spent:.4f}")

    print(f"\nTokens Consumed: Input: {summary['total_tokens']['input']} | Output: {summary['total_tokens']['output']}")
    print("Verified provider credit pool(s): UNVERIFIED (Awaiting Console confirmation)")
    print("=" * 65)


def cmd_account_check():
    """Checks key metadata, credit pools, and expiry."""
    ensure_directories()
    status = get_worker_status()
    print("=" * 65)
    print(" KAAPSTYLE — Account & Credit Pool Check")
    print("=" * 65)

    fps = set()
    for alias, info in status.items():
        fps.add(info["fingerprint"])
        print(f"\nKey Alias: {alias}")
        print(f"  - Assigned Role: {info['role']}")
        print(f"  - Environment Variable: {info['env_var']}")
        print(f"  - Configured: {info['configured']}")
        print(f"  - Dedicated Env Var: {info['dedicated']}")
        print(f"  - Fingerprint: {info['fingerprint']}")
        print(f"  - Target Model: {info['model']}")

    print("\n[!] Billing Pool Verification Analysis:")
    if len(fps) == 1 and "UNSET" not in fps:
        print("  - ALL 3 ALIASES CURRENTLY RESOLVE TO A SINGLE UNDERLYING KEY.")
        print("  - They share ONE billing balance and rate-limit pool.")
        print("  - Baseline allocation: $60 Worker A / $60 Worker B / $60 Worker C / $20 Protected Reserve.")
    elif len(fps) > 1 and "UNSET" not in fps:
        print("  - MULTIPLE DISTINCT KEYS DETECTED.")
        print("  - Distinct fingerprints detected. Verify organization IDs via Anthropic Console.")
    else:
        print("  - ONE OR MORE KEYS ARE UNSET. Set KAAPSTYLE_CLAUDE_KEY_A, B, C.")

    print("=" * 65)


def cmd_plan(envelope_path: str):
    """Parses a job envelope, validates hierarchy, checks paths, and estimates cost (Zero Spend)."""
    ensure_directories()
    p = Path(envelope_path)
    if not p.exists():
        print(f"[ERROR] Envelope file not found: {envelope_path}")
        return False

    with open(p, "r", encoding="utf-8") as f:
        try:
            envelope = json.load(f)
        except Exception as e:
            print(f"[ERROR] Invalid JSON envelope: {e}")
            return False

    valid, errors = validate_job_envelope(envelope)
    if not valid:
        print("[FAIL] Envelope validation failed:")
        for err in errors:
            print(f"  - {err}")
        return False

    key_alias = envelope["key_alias"]
    model = envelope["model"]
    worker_role = envelope["worker_role"]
    max_usd = envelope["max_task_usd"]

    worst_case_cost = estimate_request_cost(model, 3000, 2000)
    allowed, blocker, warning = check_preflight_budget(
        model=model,
        estimated_worst_case_usd=worst_case_cost,
        task_max_usd=max_usd,
    )

    print("=" * 65)
    print(f" KAAPSTYLE TASK PLAN: {envelope['job_id']}")
    print("=" * 65)
    print(f"Hierarchy: {envelope['cr_id']} -> {envelope['uc_id']} -> {envelope['task_id']} -> {envelope['subtask_ids']}")
    print(f"Worker Role: {worker_role} (Key Alias: {key_alias})")
    print(f"Model: {model}")
    print(f"Allowed Paths: {envelope['allowed_paths']}")
    print(f"Estimated Worst-Case Tokens: ~5,000 (Cost: ${worst_case_cost:.4f} USD)")
    print(f"Task Budget Ceiling: ${max_usd:.2f} USD")
    print(f"Preflight Check: {'PASSED' if allowed else 'BLOCKED'}")
    if blocker:
        print(f"  [BLOCKER] {blocker}")
    if warning:
        print(f"  [WARNING] {warning}")
    print("=" * 65)
    return allowed


def cmd_route(envelope_path: str):
    return cmd_plan(envelope_path)


def cmd_execute(envelope_path: str, live: bool = False):
    """
    Executes a task envelope.
    DEFAULT IS DRY-RUN (live=False). Live API calls require explicit --live flag.
    """
    ensure_directories()

    if PAUSE_LOCK_FILE.exists():
        print("[ERROR] Coordinator is PAUSED. Run 'resume' first.")
        return False

    p = Path(envelope_path)
    if not p.exists():
        print(f"[ERROR] Envelope file not found: {envelope_path}")
        return False

    with open(p, "r", encoding="utf-8") as f:
        envelope = json.load(f)

    valid, errors = validate_job_envelope(envelope)
    if not valid:
        print(f"[ERROR] Invalid envelope: {errors}")
        return False

    job_id = envelope["job_id"]
    key_alias = envelope["key_alias"]
    model = envelope["model"]
    max_usd = envelope["max_task_usd"]
    allowed_paths = envelope["allowed_paths"]
    allowed_prefixes = WORKER_CONFIG[key_alias]["allowed_prefixes"]

    est_worst_case = estimate_request_cost(model, 4000, 3000)
    allowed, blocker, warning = check_preflight_budget(
        model=model,
        estimated_worst_case_usd=est_worst_case,
        task_max_usd=max_usd,
    )
    if not allowed:
        print(f"[BLOCKED] Preflight check failed: {blocker}")
        return False

    raw_key = get_raw_key(key_alias)
    if not raw_key and live:
        print(f"[ERROR] Missing API key for {key_alias}. Cannot perform live call.")
        return False

    mode_label = "LIVE API CALL" if live else "DRY-RUN SIMULATION (0 cost, no network)"
    print(f"\n[*] Executing {job_id} [{mode_label}] via {key_alias} ({model})...")

    res_id = add_reservation(job_id, key_alias, est_worst_case)

    try:
        system_prompt = (
            f"You are {WORKER_CONFIG[key_alias]['role_name']}. "
            f"Adhere strictly to KAAPSTYLE architecture.\n"
            f"Allowed target paths: {allowed_paths}\n\n"
            f"Format every file you produce EXACTLY with these delimiters:\n"
            f"***FILE: <path_inside_allowed_paths>***\n"
            f"<complete file content>\n"
            f"***END_FILE***\n\n"
            f"Do not write any files outside {allowed_paths}."
        )
        user_prompt = (
            f"Task Request: {envelope['owner_request']}\n"
            f"Hierarchy: {envelope['cr_id']} -> {envelope['uc_id']} -> {envelope['task_id']} ({envelope['subtask_ids']})\n"
            f"Allowed Target Files:\n" + "\n".join(f"- {f}" for f in allowed_paths)
        )

        if live:
            resp = call_claude_api(
                raw_key=raw_key,
                model=model,
                system_prompt=system_prompt,
                user_prompt=user_prompt,
                max_tokens=5000,
            )
        else:
            resp = call_claude_api_mock(
                model=model,
                system_prompt=system_prompt,
                user_prompt=user_prompt,
                max_tokens=3500,
            )

        remove_reservation(res_id)

        if not resp["success"]:
            print(f"[FAIL] Execution failed: {resp['error']}")
            return False

        cost = estimate_request_cost(model, resp["input_tokens"], resp["output_tokens"])

        # Write to ledger
        record = {
            "utc_timestamp": datetime.now(timezone.utc).isoformat(),
            "provider": "anthropic",
            "is_dry_run": resp.get("is_dry_run", True),
            "key_alias": key_alias,
            "key_fingerprint": get_fingerprint(raw_key),
            "job_id": job_id,
            "cr_id": envelope["cr_id"],
            "uc_id": envelope["uc_id"],
            "task_id": envelope["task_id"],
            "subtask_id": envelope["subtask_ids"][0] if envelope["subtask_ids"] else "ST00",
            "request_id": resp["request_id"],
            "status": "SUCCESS",
            "model": model,
            "input_tokens": resp["input_tokens"],
            "output_tokens": resp["output_tokens"],
            "cache_creation_input_tokens": resp.get("cache_creation_tokens", 0),
            "cache_read_input_tokens": resp.get("cache_read_tokens", 0),
            "estimated_cost_usd": cost if live else 0.0,
            "reconciled_cost_usd": None,
            "elapsed_seconds": resp["elapsed_seconds"],
        }
        append_ledger_record(record)

        # Parse and write files if live
        applied_files = []
        if live:
            applied_files = parse_and_apply_files(resp["text"], allowed_prefixes)

        # Write evidence log
        evidence_file = EVIDENCE_DIR / f"{job_id}.log"
        with open(evidence_file, "w", encoding="utf-8") as ef:
            ef.write(f"JOB ID: {job_id}\n")
            ef.write(f"MODE: {mode_label}\n")
            ef.write(f"REQUEST ID: {resp['request_id']}\n")
            ef.write(f"MODEL: {model}\n")
            ef.write(f"STOP REASON: {resp.get('stop_reason', 'unknown')}\n")
            ef.write(f"IS TRUNCATED: {resp.get('is_truncated', False)}\n")
            ef.write(f"COST USD: {cost if live else 0.0:.6f}\n")
            ef.write(f"APPLIED FILES:\n")
            for af in applied_files:
                ef.write(f"  - {af['path']}: {af['message']}\n")
            ef.write(f"\nRAW OUTPUT:\n{resp['text']}\n")

        print(f"[SUCCESS] {job_id} completed successfully.")
        print(f"  - Mode: {mode_label}")
        print(f"  - Request ID: {resp['request_id']}")
        print(f"  - Tokens: Input {resp['input_tokens']}, Output {resp['output_tokens']}")
        print(f"  - Billed Cost: ${cost if live else 0.0:.6f} USD")
        if resp.get("is_truncated"):
            print("  [WARNING] Output was truncated by token ceiling (stop_reason: max_tokens)! Any incomplete files were safely rejected.")
        print(f"  - Files Created/Updated: {len(applied_files)}")
        for af in applied_files:
            status_tag = "[OK]" if af["success"] else "[FAILED]"
            print(f"    {status_tag} {af['path']}")
        print(f"  - Evidence written: {evidence_file}")
        return True

    except Exception as e:
        remove_reservation(res_id)
        print(f"[ERROR] Exception during execution: {e}")
        return False


def cmd_report():
    records = get_ledger_records()
    print("=" * 65)
    print(" KAAPSTYLE — Usage Ledger Report")
    print("=" * 65)
    if not records:
        print("Ledger is empty. No requests recorded.")
        print("=" * 65)
        return

    print(f"{'Timestamp (UTC)':<22} | {'Job ID':<24} | {'Mode':<8} | {'Tokens (I/O)':<14} | {'Cost USD':<10}")
    print("-" * 88)
    for r in records[-15:]:
        mode = "DRY-RUN" if r.get("is_dry_run", True) else "LIVE"
        tokens = f"{r.get('input_tokens',0)}/{r.get('output_tokens',0)}"
        cost = f"${r.get('estimated_cost_usd',0.0):.4f}"
        ts = r.get("utc_timestamp", "")[:19]
        print(f"{ts:<22} | {r.get('job_id',''):<24} | {mode:<8} | {tokens:<14} | {cost:<10}")
    print("=" * 65)


def cmd_pause():
    ensure_directories()
    PAUSE_LOCK_FILE.touch()
    print("[!] Coordinator is now PAUSED. Outgoing API calls blocked.")


def cmd_resume():
    if PAUSE_LOCK_FILE.exists():
        PAUSE_LOCK_FILE.unlink()
        print("[+] Coordinator RESUMED.")
    else:
        print("[+] Coordinator is already active.")


def cmd_smoke_test(key_alias: str = "KEY_A", model: str = "claude-haiku-5-5", live: bool = False):
    ensure_directories()
    if PAUSE_LOCK_FILE.exists():
        print("[ERROR] Coordinator is paused.")
        return

    raw_key = get_raw_key(key_alias)
    if not raw_key and live:
        print(f"[ERROR] API key for {key_alias} is unset.")
        return

    mode_label = "LIVE" if live else "DRY-RUN"
    print(f"[*] Running smoke test [{mode_label}] via {key_alias} ({model})...")

    if live:
        resp = call_claude_api(
            raw_key=raw_key,
            model=model,
            system_prompt="You are the KAAPSTYLE coordinator test worker.",
            user_prompt="Reply with exactly: PING_OK",
            max_tokens=5,
        )
    else:
        resp = call_claude_api_mock(
            model=model,
            system_prompt="You are the KAAPSTYLE coordinator test worker.",
            user_prompt="Reply with exactly: PING_OK",
            max_tokens=5,
        )

    if resp["success"]:
        cost = estimate_request_cost(model, resp["input_tokens"], resp["output_tokens"]) if live else 0.0
        record = {
            "utc_timestamp": datetime.now(timezone.utc).isoformat(),
            "provider": "anthropic",
            "is_dry_run": not live,
            "key_alias": key_alias,
            "key_fingerprint": get_fingerprint(raw_key),
            "job_id": "SMOKE-TEST",
            "cr_id": "CR00",
            "uc_id": "UC00",
            "task_id": "T00",
            "subtask_id": "ST00",
            "request_id": resp["request_id"],
            "status": "SUCCESS",
            "model": model,
            "input_tokens": resp["input_tokens"],
            "output_tokens": resp["output_tokens"],
            "cache_creation_input_tokens": 0,
            "cache_read_input_tokens": 0,
            "estimated_cost_usd": cost,
            "reconciled_cost_usd": None,
            "elapsed_seconds": resp["elapsed_seconds"],
        }
        append_ledger_record(record)
        print(f"[SUCCESS] Smoke test passed [{mode_label}]. Request ID: {resp['request_id']} | Cost: ${cost:.6f} USD")
    else:
        print(f"[FAIL] Smoke test failed: {resp['error']}")


def cmd_clean_reservations(max_age_seconds: int = 600):
    """Purges orphaned budget reservations older than max_age_seconds."""
    ensure_directories()
    count = cleanup_stale_reservations(max_age_seconds)
    print(f"[+] Stale reservation cleanup completed: {count} orphaned lock(s) released.")


def cmd_verify(envelope_path: str) -> bool:
    """Executes required verification test cases for a task and captures immutable evidence."""
    ensure_directories()
    p = Path(envelope_path)
    if not p.exists():
        print(f"[ERROR] Envelope file not found: {envelope_path}")
        return False

    with open(p, "r", encoding="utf-8") as f:
        try:
            envelope = json.load(f)
        except Exception as e:
            print(f"[ERROR] Invalid JSON envelope: {e}")
            return False

    job_id = envelope.get("job_id", "UNKNOWN_JOB")
    cr_id = envelope.get("cr_id", "CR00")
    uc_id = envelope.get("uc_id", "UC00")
    task_id = envelope.get("task_id", "T00")
    subtask_ids = envelope.get("subtask_ids", ["ST00"])
    required_tests = envelope.get("required_tests", [])

    print("=" * 65)
    print(f" KAAPSTYLE VERIFICATION RUNNER — {job_id}")
    print("=" * 65)
    print(f"Hierarchy: {cr_id} -> {uc_id} -> {task_id} -> {subtask_ids}")

    if not required_tests:
        print("[!] No required_tests defined in envelope. Marking verification as NOT RUN.")
        return False

    results = []
    all_passed = True

    for idx, test_cmd in enumerate(required_tests):
        tc_id = f"TC-{cr_id}-{uc_id}-{task_id}-{idx+1:02d}"
        print(f"\n[*] Executing {tc_id}: '{test_cmd}'...")

        cmd_parts = test_cmd.strip().split()
        base_exe = cmd_parts[0] if cmd_parts else ""

        cwd = WORKSPACE_ROOT
        if base_exe in ["flutter", "dart"] or "flutter" in test_cmd or "dart" in test_cmd:
            cwd = WORKSPACE_ROOT / "mobile"
        elif base_exe in ["mvn", "gradle", "./gradlew"] or "mvn" in test_cmd or "gradle" in test_cmd:
            cwd = WORKSPACE_ROOT / "backend"

        if not shutil.which(base_exe):
            status = "BLOCKED"
            out_text = f"Tool '{base_exe}' is not installed or not in PATH."
            all_passed = False
            print(f"  [BLOCKED] {out_text}")
        else:
            start_t = time.time()
            try:
                proc = subprocess.run(
                    test_cmd,
                    cwd=str(cwd),
                    shell=True,
                    capture_output=True,
                    text=True,
                    timeout=180,
                )
                elapsed = round(time.time() - start_t, 3)
                out_text = (proc.stdout + "\n" + proc.stderr).strip()
                if proc.returncode == 0:
                    status = "PASS"
                    print(f"  [PASS] Completed in {elapsed}s")
                else:
                    status = "FAIL"
                    all_passed = False
                    print(f"  [FAIL] Exited with code {proc.returncode} in {elapsed}s\n  {out_text[:300]}")
            except Exception as ex:
                status = "FAIL"
                all_passed = False
                out_text = f"Execution error: {str(ex)}"
                print(f"  [FAIL] {out_text}")

        evidence_entry = {
            "tc_id": tc_id,
            "linked_hierarchy": f"{cr_id} -> {uc_id} -> {task_id} -> {subtask_ids}",
            "type": "AUTOMATED_UNIT_TEST",
            "command": test_cmd,
            "environment": f"Python {sys.version.split()[0]} / Windows",
            "execution_date": datetime.now(timezone.utc).isoformat(),
            "status": status,
            "output_summary": out_text[:500],
        }
        results.append(evidence_entry)

    evidence_path = EVIDENCE_DIR / f"{job_id}_test_report.json"
    with open(evidence_path, "w", encoding="utf-8") as ef:
        json.dump(
            {
                "job_id": job_id,
                "cr_id": cr_id,
                "uc_id": uc_id,
                "task_id": task_id,
                "all_passed": all_passed,
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "results": results,
            },
            ef,
            indent=2,
        )

    print("\n" + "=" * 65)
    print(f" VERIFICATION OUTCOME: {'ALL TESTS PASSED' if all_passed else 'FAILURES / BLOCKERS DETECTED'}")
    print(f" Test Evidence Report: {evidence_path}")
    print("=" * 65)
    return all_passed


def cmd_reconcile():
    """Queries Anthropic official usage/cost endpoint if Admin API key is configured."""
    admin_key = os.environ.get("KAAPSTYLE_ANTHROPIC_ADMIN_KEY")
    print("=" * 65)
    print(" KAAPSTYLE — Provider Credit Reconciliation")
    print("=" * 65)

    if not admin_key:
        print("Reconciliation Status: CREDITS_UNVERIFIED")
        print("Note: Direct Messages API keys cannot query organization billing/credit balance.")
        print("To enable automated reconciliation, configure the KAAPSTYLE_ANTHROPIC_ADMIN_KEY environment variable.")
        print("Or confirm nonsecret balance numbers via Anthropic Console:")
        print("  https://console.anthropic.com/settings/billing")
        print("=" * 65)
        return

    print("[*] Contacting Anthropic Usage API...")
    try:
        req = urllib.request.Request(
            "https://api.anthropic.com/v1/organizations/usage",
            headers={
                "X-Api-Key": admin_key,
                "Anthropic-Version": "2023-06-01",
                "User-Agent": "KAAPSTYLE-API-Coordinator/1.0",
            },
        )
        with urllib.request.urlopen(req, timeout=30) as resp:
            body = json.loads(resp.read().decode("utf-8"))
            print("[+] Official Usage Data Reconciled:")
            print(json.dumps(body, indent=2))
    except Exception as e:
        print(f"[!] Reconcile API query failed: {e}")
    print("=" * 65)


def main():
    parser = argparse.ArgumentParser(description="KAAPSTYLE Claude API Coordinator CLI (Dry-Run by Default)")
    subparsers = parser.add_subparsers(dest="command", help="Coordinator commands")

    subparsers.add_parser("doctor", help="Run environment & dependency checks")
    subparsers.add_parser("status", help="Show budget and spend status")
    subparsers.add_parser("account-check", help="Check credit pool and key configurations")
    subparsers.add_parser("report", help="Show ledger usage report")
    subparsers.add_parser("pause", help="Pause outgoing API requests")
    subparsers.add_parser("resume", help="Resume outgoing API requests")
    subparsers.add_parser("clean-reservations", help="Purge orphaned/stale reservations")
    subparsers.add_parser("reconcile", help="Reconcile usage with Anthropic billing API")

    plan_p = subparsers.add_parser("plan", help="Dry-run task planning and budget estimation")
    plan_p.add_argument("envelope", help="Path to job envelope JSON file")

    route_p = subparsers.add_parser("route", help="Verify task routing and path boundaries")
    route_p.add_argument("envelope", help="Path to job envelope JSON file")

    exec_p = subparsers.add_parser("execute", help="Execute task (Default: DRY-RUN)")
    exec_p.add_argument("envelope", help="Path to job envelope JSON file")
    exec_p.add_argument("--live", action="store_true", default=False, help="Perform LIVE API call (Default is Dry-Run)")

    verify_p = subparsers.add_parser("verify", help="Run automated test verification and generate evidence report")
    verify_p.add_argument("envelope", help="Path to job envelope JSON file")

    smoke_p = subparsers.add_parser("smoke-test", help="Run connectivity probe (Default: DRY-RUN)")
    smoke_p.add_argument("--key", default="KEY_A", help="Key alias to test (default: KEY_A)")
    smoke_p.add_argument("--model", default="claude-haiku-5-5", help="Model to use")
    smoke_p.add_argument("--live", action="store_true", default=False, help="Perform LIVE API call (Default is Dry-Run)")

    args = parser.parse_args()

    if args.command == "doctor":
        cmd_doctor()
    elif args.command == "status":
        cmd_status()
    elif args.command == "account-check":
        cmd_account_check()
    elif args.command == "report":
        cmd_report()
    elif args.command == "pause":
        cmd_pause()
    elif args.command == "resume":
        cmd_resume()
    elif args.command == "clean-reservations":
        cmd_clean_reservations()
    elif args.command == "reconcile":
        cmd_reconcile()
    elif args.command == "plan":
        cmd_plan(args.envelope)
    elif args.command == "route":
        cmd_route(args.envelope)
    elif args.command == "execute":
        cmd_execute(args.envelope, live=args.live)
    elif args.command == "verify":
        cmd_verify(args.envelope)
    elif args.command == "smoke-test":
        cmd_smoke_test(key_alias=args.key, model=args.model, live=args.live)
    else:
        parser.print_help()


if __name__ == "__main__":
    main()
