r"""
KAAPSTYLE Claude API Coordinator — Configuration & Credential Loader
Strictly adheres to E:/KAAPSTYLE/AGENTS.md.
Never prints, logs, or exports raw API keys.
"""

import os
import sys
import hashlib
from pathlib import Path
from typing import Dict, Optional, Tuple, List

# Workspace boundaries
WORKSPACE_ROOT = Path(r"E:\KAAPSTYLE").resolve()
REPORTS_DIR = WORKSPACE_ROOT / "reports"
LEDGER_FILE = REPORTS_DIR / "ledger.jsonl"
RESERVATIONS_FILE = REPORTS_DIR / "reservations.json"
EVIDENCE_DIR = REPORTS_DIR / "test_evidence"

# Strictly forbidden paths (never editable by workers or coordinator output)
FORBIDDEN_PATHS = [
    Path(r"E:\stafferin-ai-agent").resolve(),
    WORKSPACE_ROOT / "AGENTS.md",
    WORKSPACE_ROOT / "tools" / "api_coordinator",
    WORKSPACE_ROOT / "reports" / "ledger.jsonl",
    WORKSPACE_ROOT / "reports" / "reservations.json",
]

# Budget & Safety Constants
DEFAULT_TASK_MAX_USD = 2.00
SAFETY_RESERVE_USD = 20.00
DEFAULT_TOTAL_POOL_CAP_USD = 200.00  # Default conservative baseline
WARNING_THRESHOLDS = [0.50, 0.75, 0.90]

# Supported Anthropic Models & Official Pricing (Per 1 Million Tokens)
# Verified directly from Anthropic /v1/models endpoint
SUPPORTED_MODELS = {
    "claude-sonnet-5-5": {
        "name": "Claude Sonnet 5.5",
        "input": 3.00,
        "output": 15.00,
        "cache_write": 3.75,
        "cache_read": 0.30,
        "status": "ACTIVE",
    },
    "claude-haiku-5-5": {
        "name": "Claude Haiku 5.5",
        "input": 0.80,
        "output": 4.00,
        "cache_write": 1.00,
        "cache_read": 0.08,
        "status": "ACTIVE",
    },
    "claude-sonnet-5": {
        "name": "Claude Sonnet 5",
        "input": 3.00,
        "output": 15.00,
        "cache_write": 3.75,
        "cache_read": 0.30,
        "status": "ACTIVE",
    },
    "claude-opus-5-5": {
        "name": "Claude Opus 5.5",
        "input": 15.00,
        "output": 75.00,
        "cache_write": 18.75,
        "cache_read": 1.50,
        "status": "ACTIVE",
    },
    "claude-haiku-4-5-20251001": {
        "name": "Claude Haiku 4.5",
        "input": 0.80,
        "output": 4.00,
        "cache_write": 1.00,
        "cache_read": 0.08,
        "status": "ACTIVE",
    },
}

# Explicitly retired or deprecated models (rejected if attempted)
RETIRED_MODELS = {
    "claude-3-5-sonnet-20241022": "Retired / Superseded by claude-sonnet-5",
    "claude-3-5-haiku-20241022": "Retired / Superseded by claude-haiku-5-5",
    "claude-2.0": "Retired by Anthropic in 2024",
    "claude-2.1": "Retired by Anthropic in 2024",
    "claude-instant-1.2": "Retired by Anthropic in 2024",
    "claude-3-sonnet-20240229": "Retired by Anthropic in 2024",
}

# Role and key alias definitions
WORKER_CONFIG = {
    "KEY_A": {
        "env_var": "KAAPSTYLE_CLAUDE_KEY_A",
        "role_code": "AGENT_A_FLUTTER",
        "role_name": "Agent A (Flutter / Dart UI)",
        "default_model": "claude-sonnet-5-5",
        "allowed_prefixes": ["mobile/lib/", "mobile/test/"],
        "forbidden_prefixes": ["backend/", "tools/", "reports/", "AGENTS.md"],
    },
    "KEY_B": {
        "env_var": "KAAPSTYLE_CLAUDE_KEY_B",
        "role_code": "AGENT_B_JAVA",
        "role_name": "Agent B (Java / Spring Boot)",
        "default_model": "claude-sonnet-5-5",
        "allowed_prefixes": ["backend/src/main/java/", "backend/src/test/java/"],
        "forbidden_prefixes": ["mobile/", "tools/", "reports/", "AGENTS.md"],
    },
    "KEY_C": {
        "env_var": "KAAPSTYLE_CLAUDE_KEY_C",
        "role_code": "AGENT_C_QA",
        "role_name": "Agent C (MySQL, Testing, Security)",
        "default_model": "claude-haiku-5-5",
        "allowed_prefixes": [
            "backend/src/main/resources/db/migration/",
            "tests/",
        ],
        "forbidden_prefixes": ["tools/", "reports/", "AGENTS.md"],
    },
}


def is_model_supported(model_id: str) -> Tuple[bool, Optional[str]]:
    """Validates model availability and rejects deprecated model IDs."""
    if model_id in RETIRED_MODELS:
        return False, f"Model '{model_id}' is retired ({RETIRED_MODELS[model_id]}). Use verified models: {list(SUPPORTED_MODELS.keys())}."
    if model_id not in SUPPORTED_MODELS:
        return False, f"Model '{model_id}' is unrecognized. Supported models: {list(SUPPORTED_MODELS.keys())}."
    return True, None


def _lookup_env(env_name: str) -> Optional[str]:
    """Looks up environment variable in os.environ, falling back to Windows User Registry."""
    val = os.environ.get(env_name)
    if val:
        return val.strip()
    if sys.platform == "win32":
        try:
            import winreg
            with winreg.OpenKey(winreg.HKEY_CURRENT_USER, r"Environment") as k:
                reg_val, _ = winreg.QueryValueEx(k, env_name)
                if reg_val:
                    return str(reg_val).strip()
        except Exception:
            pass
    return None


def get_raw_key(key_alias: str) -> Optional[str]:
    """
    Retrieves the raw API key for in-memory use only.
    Checks alias-specific environment variable first, then general ANTHROPIC_API_KEY as fallback.
    """
    if key_alias not in WORKER_CONFIG:
        return None
    env_name = WORKER_CONFIG[key_alias]["env_var"]
    key = _lookup_env(env_name)
    if not key:
        key = _lookup_env("ANTHROPIC_API_KEY")
    return key.strip() if key else None


def is_key_dedicated(key_alias: str) -> bool:
    """Returns True if the alias has its own dedicated environment variable set."""
    if key_alias not in WORKER_CONFIG:
        return False
    env_name = WORKER_CONFIG[key_alias]["env_var"]
    return bool(_lookup_env(env_name))


def get_fingerprint(raw_key: Optional[str]) -> str:
    """Returns a non-reversible 8-character cryptographic fingerprint of the key."""
    if not raw_key:
        return "UNSET"
    sha = hashlib.sha256(raw_key.encode("utf-8")).hexdigest()
    return f"ant-{sha[:8]}"


def get_worker_status() -> Dict[str, dict]:
    """Returns safe metadata about each worker key without leaking secrets."""
    status = {}
    for alias, cfg in WORKER_CONFIG.items():
        raw = get_raw_key(alias)
        is_configured = bool(raw and raw.startswith("sk-ant-"))
        fp = get_fingerprint(raw)
        status[alias] = {
            "alias": alias,
            "role_code": cfg["role_code"],
            "role": cfg["role_name"],
            "configured": is_configured,
            "dedicated": is_key_dedicated(alias),
            "fingerprint": fp,
            "model": cfg["default_model"],
            "env_var": cfg["env_var"],
            "allowed_prefixes": cfg["allowed_prefixes"],
        }
    return status
