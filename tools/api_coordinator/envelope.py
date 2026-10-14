r"""
KAAPSTYLE Claude API Coordinator — Deterministic Job Envelope Validator
Validates strict traceability: CR -> UC -> Task -> Subtask -> Test Case.
Enforces role-to-path isolation boundaries.
"""

from typing import Dict, Any, List, Tuple
from config import WORKER_CONFIG, is_model_supported


REQUIRED_ENVELOPE_FIELDS = [
    "job_id",
    "cr_id",
    "uc_id",
    "task_id",
    "subtask_ids",
    "worker_role",
    "key_alias",
    "model",
    "owner_request",
    "allowed_paths",
    "forbidden_paths",
    "max_task_usd",
]


def validate_job_envelope(envelope: Dict[str, Any]) -> Tuple[bool, List[str]]:
    """
    Validates that a job envelope meets KAAPSTYLE engineering standards.
    Returns (is_valid: bool, errors: List[str]).
    """
    errors = []

    # 1. Mandatory field checks
    for field in REQUIRED_ENVELOPE_FIELDS:
        if field not in envelope or envelope[field] is None:
            errors.append(f"Missing mandatory envelope field: '{field}'")

    if errors:
        return False, errors

    # 2. Canonical hierarchy check (CRxx, UCxx, Txx, STxx)
    cr = str(envelope.get("cr_id", ""))
    uc = str(envelope.get("uc_id", ""))
    t = str(envelope.get("task_id", ""))
    st_list = envelope.get("subtask_ids", [])

    if not cr.startswith("CR"):
        errors.append(f"Invalid cr_id: '{cr}'. Must start with 'CR' (e.g. CR01).")
    if not uc.startswith("UC"):
        errors.append(f"Invalid uc_id: '{uc}'. Must start with 'UC' (e.g. UC01).")
    if not t.startswith("T"):
        errors.append(f"Invalid task_id: '{t}'. Must start with 'T' (e.g. T01).")
    if not isinstance(st_list, list) or len(st_list) == 0:
        errors.append("subtask_ids must be a non-empty list of subtask IDs (e.g. ['ST01']).")

    # 3. Key alias and Worker Role pairing
    key_alias = envelope.get("key_alias", "")
    worker_role = envelope.get("worker_role", "")
    if key_alias not in WORKER_CONFIG:
        errors.append(f"Invalid key_alias: '{key_alias}'. Must be KEY_A, KEY_B, or KEY_C.")
    else:
        expected_role = WORKER_CONFIG[key_alias]["role_code"]
        if worker_role != expected_role:
            errors.append(
                f"Role mismatch: key_alias '{key_alias}' requires worker_role '{expected_role}', but got '{worker_role}'."
            )

    # 4. Model validity
    model = envelope.get("model", "")
    supported, model_err = is_model_supported(model)
    if not supported:
        errors.append(model_err)

    # 5. Budget ceiling
    max_usd = envelope.get("max_task_usd", 0.0)
    if not isinstance(max_usd, (int, float)) or max_usd <= 0:
        errors.append(f"Invalid max_task_usd: {max_usd}. Must be a positive number.")

    # 6. Path validation & role boundary checks
    allowed = envelope.get("allowed_paths", [])
    if not isinstance(allowed, list) or len(allowed) == 0:
        errors.append("allowed_paths must be a non-empty list of relative file/directory paths.")
    elif key_alias in WORKER_CONFIG:
        permitted_prefixes = WORKER_CONFIG[key_alias]["allowed_prefixes"]
        for p in allowed:
            matches = any(p.startswith(pref) for pref in permitted_prefixes)
            if not matches:
                errors.append(
                    f"Path violation: '{p}' is not allowed for worker role '{worker_role}'. "
                    f"Permitted prefixes: {permitted_prefixes}"
                )

    return len(errors) == 0, errors
