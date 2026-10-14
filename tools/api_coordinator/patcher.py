r"""
KAAPSTYLE Claude API Coordinator — Path Sandboxing & Patch Validator
Enforces zero leakage into E:\stafferin-ai-agent and protects sensitive workspace files.
"""

import os
from pathlib import Path
from typing import List, Tuple
from config import WORKSPACE_ROOT, FORBIDDEN_PATHS


class SecurityViolation(Exception):
    """Raised when an operation attempts to access or modify forbidden directories."""
    pass


def sanitize_and_validate_path(
    target_path_str: str,
    allowed_prefixes: List[str],
) -> Path:
    r"""
    Validates that a path is strictly inside E:\KAAPSTYLE and matches allowed_prefixes.
    Rejects any path targeting E:\stafferin-ai-agent, AGENTS.md, or coordinator tools.
    """
    # 0. Immediate substring check for stafferin in any case
    if "stafferin" in target_path_str.lower():
        raise SecurityViolation(
            f"Security Violation: Target path '{target_path_str}' references forbidden project Stafferin."
        )

    # 1. Resolve path
    p = Path(target_path_str)
    if p.is_absolute():
        resolved = p.resolve()
    else:
        resolved = (WORKSPACE_ROOT / p).resolve()

    # 2. Workspace boundary check
    try:
        rel_to_workspace = resolved.relative_to(WORKSPACE_ROOT)
    except ValueError:
        raise SecurityViolation(
            f"Access Denied: Path '{target_path_str}' resolves outside active workspace {WORKSPACE_ROOT}."
        )

    # 3. Strict Stafferin and protected paths check
    for forbidden in FORBIDDEN_PATHS:
        try:
            resolved.relative_to(forbidden)
            raise SecurityViolation(
                f"Access Denied: Path '{target_path_str}' is inside protected directory '{forbidden}'."
            )
        except ValueError:
            pass

        if resolved == forbidden:
            raise SecurityViolation(
                f"Access Denied: Modification of protected file '{forbidden}' is prohibited."
            )

    # 4. Allowed prefixes check
    norm_rel = rel_to_workspace.as_posix()
    matches_allowed = False
    for prefix in allowed_prefixes:
        clean_prefix = prefix.rstrip("/*").rstrip("/")
        if norm_rel == clean_prefix or norm_rel.startswith(clean_prefix + "/"):
            matches_allowed = True
            break

    if not matches_allowed:
        raise SecurityViolation(
            f"Access Denied: Path '{norm_rel}' is not permitted by worker allowed_prefixes: {allowed_prefixes}"
        )

    return resolved


def safe_apply_file_write(
    target_rel_path: str,
    content: str,
    allowed_prefixes: List[str],
) -> Tuple[bool, str]:
    r"""
    Safely writes content to an approved file inside E:\KAAPSTYLE.
    Creates parent directories if necessary.
    """
    try:
        target_path = sanitize_and_validate_path(target_rel_path, allowed_prefixes)
        target_path.parent.mkdir(parents=True, exist_ok=True)
        with open(target_path, "w", encoding="utf-8") as f:
            f.write(content)
        return True, f"Successfully written: {target_path.relative_to(WORKSPACE_ROOT)}"
    except SecurityViolation as sv:
        return False, str(sv)
    except Exception as e:
        return False, f"File write error: {str(e)}"
