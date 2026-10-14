"""
KAAPSTYLE Claude API Coordinator — Durable Ledger & Credit Tracker
Maintains atomic reservations and immutable JSONL spend audit logs.
"""

import json
import time
from datetime import datetime, timezone
from pathlib import Path
from typing import Dict, List, Optional
from config import (
    LEDGER_FILE,
    RESERVATIONS_FILE,
    REPORTS_DIR,
    EVIDENCE_DIR,
    DEFAULT_TOTAL_POOL_CAP_USD,
)


def ensure_directories():
    """Ensures required reports and evidence directories exist."""
    REPORTS_DIR.mkdir(parents=True, exist_ok=True)
    EVIDENCE_DIR.mkdir(parents=True, exist_ok=True)
    if not LEDGER_FILE.exists():
        LEDGER_FILE.touch()
    if not RESERVATIONS_FILE.exists():
        with open(RESERVATIONS_FILE, "w", encoding="utf-8") as f:
            json.dump({}, f)


def cleanup_stale_reservations(max_age_seconds: int = 600) -> int:
    """
    Identifies and purges reservations older than max_age_seconds (default: 15 min).
    Prevents orphaned locks from aborted/crashed requests from permanently tying up budget.
    """
    ensure_directories()
    try:
        with open(RESERVATIONS_FILE, "r", encoding="utf-8") as f:
            res = json.load(f)
    except Exception:
        return 0

    now = datetime.now(timezone.utc)
    to_delete = []
    for r_id, item in res.items():
        ts_str = item.get("timestamp")
        if not ts_str:
            to_delete.append(r_id)
            continue
        try:
            ts = datetime.fromisoformat(ts_str)
            if (now - ts).total_seconds() > max_age_seconds:
                to_delete.append(r_id)
        except Exception:
            to_delete.append(r_id)

    if to_delete:
        for r_id in to_delete:
            del res[r_id]
        write_reservations(res)

    return len(to_delete)


def read_reservations(auto_clean: bool = True) -> Dict[str, dict]:
    """Reads active in-flight cost reservations, optionally pruning stale locks."""
    ensure_directories()
    if auto_clean:
        cleanup_stale_reservations()
    try:
        with open(RESERVATIONS_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    except Exception:
        return {}


def write_reservations(res: Dict[str, dict]):
    """Writes active in-flight cost reservations atomically."""
    ensure_directories()
    temp_file = RESERVATIONS_FILE.with_suffix(".tmp")
    with open(temp_file, "w", encoding="utf-8") as f:
        json.dump(res, f, indent=2)
    temp_file.replace(RESERVATIONS_FILE)


def add_reservation(job_id: str, key_alias: str, estimated_usd: float) -> str:
    """Creates an atomic budget lock before an API call is dispatched."""
    res = read_reservations(auto_clean=True)
    res_id = f"RES-{job_id}-{int(time.time() * 1000)}"
    res[res_id] = {
        "job_id": job_id,
        "key_alias": key_alias,
        "estimated_usd": round(estimated_usd, 6),
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }
    write_reservations(res)
    return res_id


def remove_reservation(res_id: str):
    """Removes a budget reservation once the API call settles or fails."""
    res = read_reservations(auto_clean=False)
    if res_id in res:
        del res[res_id]
        write_reservations(res)


def get_active_reserved_usd() -> float:
    """Returns the total USD currently locked in active requests."""
    res = read_reservations(auto_clean=True)
    return sum(item.get("estimated_usd", 0.0) for item in res.values())


def append_ledger_record(record: dict):
    """Appends an immutable entry to reports/ledger.jsonl."""
    ensure_directories()
    if "utc_timestamp" not in record:
        record["utc_timestamp"] = datetime.now(timezone.utc).isoformat()
    with open(LEDGER_FILE, "a", encoding="utf-8") as f:
        f.write(json.dumps(record) + "\n")


def get_ledger_records() -> List[dict]:
    """Reads all historical entries from the ledger."""
    ensure_directories()
    records = []
    if not LEDGER_FILE.exists():
        return records
    with open(LEDGER_FILE, "r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if line:
                try:
                    records.append(json.loads(line))
                except json.JSONDecodeError:
                    continue
    return records


def get_summary(total_cap: float = DEFAULT_TOTAL_POOL_CAP_USD) -> dict:
    """Computes spend summary across keys, total spend, reserved funds, and remaining budget."""
    records = get_ledger_records()
    spend_by_key = {"KEY_A": 0.0, "KEY_B": 0.0, "KEY_C": 0.0}
    total_spent = 0.0
    total_tokens = {"input": 0, "output": 0}

    for rec in records:
        cost = rec.get("estimated_cost_usd", 0.0)
        key = rec.get("key_alias", "UNKNOWN")
        spend_by_key[key] = spend_by_key.get(key, 0.0) + cost
        total_spent += cost
        total_tokens["input"] += rec.get("input_tokens", 0)
        total_tokens["output"] += rec.get("output_tokens", 0)

    active_reserved = get_active_reserved_usd()
    remaining = total_cap - (total_spent + active_reserved)

    return {
        "total_cap_usd": round(total_cap, 4),
        "total_spent_usd": round(total_spent, 4),
        "active_reserved_usd": round(active_reserved, 4),
        "remaining_usd": round(remaining, 4),
        "spend_by_key_usd": {k: round(v, 4) for k, v in spend_by_key.items()},
        "total_tokens": total_tokens,
        "entry_count": len(records),
    }
