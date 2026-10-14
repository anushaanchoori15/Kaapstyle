r"""
KAAPSTYLE Claude API Coordinator — Budget Guard & Spending Verifier
Enforces conservative limits, pre-flight atomic checks, soft warnings, and hard stops.
"""

from typing import Tuple, Optional
from config import (
    SUPPORTED_MODELS,
    DEFAULT_TASK_MAX_USD,
    SAFETY_RESERVE_USD,
    DEFAULT_TOTAL_POOL_CAP_USD,
    WARNING_THRESHOLDS,
    is_model_supported,
)
from ledger import get_summary


def estimate_request_cost(
    model: str,
    input_tokens: int,
    output_tokens: int,
    cache_write_tokens: int = 0,
    cache_read_tokens: int = 0,
) -> float:
    """Calculates estimated cost in USD using official model pricing per 1M tokens."""
    pricing = SUPPORTED_MODELS.get(model, SUPPORTED_MODELS["claude-sonnet-5-5"])
    cost = (
        (input_tokens / 1_000_000.0) * pricing["input"]
        + (output_tokens / 1_000_000.0) * pricing["output"]
        + (cache_write_tokens / 1_000_000.0) * pricing.get("cache_write", 0.0)
        + (cache_read_tokens / 1_000_000.0) * pricing.get("cache_read", 0.0)
    )
    return round(cost, 6)


def check_preflight_budget(
    model: str,
    estimated_worst_case_usd: float,
    task_max_usd: float = DEFAULT_TASK_MAX_USD,
    total_cap_usd: float = DEFAULT_TOTAL_POOL_CAP_USD,
    safety_reserve_usd: float = SAFETY_RESERVE_USD,
    current_committed_override: Optional[float] = None,
) -> Tuple[bool, Optional[str], Optional[str]]:
    """
    Performs pre-flight checks:
    1. Validates that the requested model is active and supported.
    2. Checks if worst-case request cost exceeds task ceiling.
    3. Checks if total spend + reservations + request exceeds hard stop (Total Cap - $20 reserve).
    4. Checks soft warning thresholds (50%, 75%, 90%).

    Returns:
        (allowed: bool, blocker_reason: Optional[str], warning_msg: Optional[str])
    """
    # 0. Model validity check
    supported, model_err = is_model_supported(model)
    if not supported:
        return False, model_err, None

    # 1. Task ceiling check
    if estimated_worst_case_usd > task_max_usd:
        return (
            False,
            f"Estimated cost (${estimated_worst_case_usd:.4f}) exceeds task ceiling (${task_max_usd:.2f}). Requires owner budget increase.",
            None,
        )

    # 2. Hard stop check against pool and safety reserve
    if current_committed_override is not None:
        current_committed = current_committed_override
    else:
        summary = get_summary(total_cap_usd)
        current_committed = summary["total_spent_usd"] + summary["active_reserved_usd"]

    hard_stop_limit = total_cap_usd - safety_reserve_usd

    if (current_committed + estimated_worst_case_usd) > hard_stop_limit:
        return (
            False,
            f"Hard stop reached! Current committed (${current_committed:.2f}) + request (${estimated_worst_case_usd:.4f}) "
            f"exceeds safety threshold (${hard_stop_limit:.2f}, preserving ${safety_reserve_usd:.2f} safety buffer).",
            None,
        )

    # 3. Soft warnings
    projected_spend = current_committed + estimated_worst_case_usd
    ratio = projected_spend / total_cap_usd
    warning_msg = None
    for thresh in reversed(WARNING_THRESHOLDS):
        if ratio >= thresh:
            warning_msg = f"SOFT WARNING: Budget utilization at {ratio * 100:.1f}% (Threshold: {thresh * 100:.0f}%)."
            break

    return True, None, warning_msg
