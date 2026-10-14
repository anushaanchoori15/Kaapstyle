r"""
KAAPSTYLE Claude API Coordinator — Anthropic Messages Client
Direct HTTPS client using Python standard library (urllib.request).
Zero third-party pip dependencies required.
Never prints, logs, or stores raw API keys.
Includes mock client for deterministic zero-spend dry runs.
"""

import json
import time
import urllib.request
import urllib.error
from typing import Dict, Any, Optional

from config import is_model_supported

ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages"
ANTHROPIC_VERSION = "2023-06-01"
MAX_RETRIES = 2
BASE_BACKOFF_SECONDS = 2.0


def call_claude_api_mock(
    model: str,
    system_prompt: str,
    user_prompt: str,
    max_tokens: int = 1024,
) -> Dict[str, Any]:
    """
    Simulates Claude API execution in dry-run mode.
    Guarantees 0 network calls, 0 cost, and deterministic dry-run telemetry.
    """
    # Estimate tokens based on prompt length
    est_input = max(10, len(system_prompt.split()) + len(user_prompt.split()) * 2)
    est_output = min(max_tokens, 120)

    return {
        "success": True,
        "is_dry_run": True,
        "status_code": 200,
        "request_id": f"dryrun_mock_req_{int(time.time() * 1000)}",
        "text": f"/* [DRY-RUN SIMULATION] Model '{model}' validated. No changes applied. */",
        "thinking": "",
        "stop_reason": "end_turn",
        "is_truncated": False,
        "input_tokens": est_input,
        "output_tokens": est_output,
        "cache_creation_tokens": 0,
        "cache_read_tokens": 0,
        "elapsed_seconds": 0.05,
        "retry_count": 0,
        "raw_response": {"simulation": True, "model": model},
        "error": None,
    }


def call_claude_api(
    raw_key: str,
    model: str,
    system_prompt: str,
    user_prompt: str,
    max_tokens: int = 1024,
    temperature: float = 0.0,
) -> Dict[str, Any]:
    """
    Sends a direct HTTPS POST request to the Anthropic Claude API.
    Handles retry with bounded exponential backoff for 429 and 529 status codes.
    Returns structured metrics, token counts, request IDs, and response text.
    """
    # Validate model ID first
    supported, model_err = is_model_supported(model)
    if not supported:
        return {
            "success": False,
            "status_code": 400,
            "request_id": "",
            "text": "",
            "thinking": "",
            "stop_reason": "model_error",
            "is_truncated": False,
            "input_tokens": 0,
            "output_tokens": 0,
            "cache_creation_tokens": 0,
            "cache_read_tokens": 0,
            "elapsed_seconds": 0.0,
            "retry_count": 0,
            "raw_response": {},
            "error": model_err,
        }

    payload = {
        "model": model,
        "max_tokens": max_tokens,
        "system": system_prompt,
        "messages": [
            {"role": "user", "content": user_prompt}
        ],
    }
    if temperature is not None and temperature > 0:
        payload["temperature"] = temperature

    data = json.dumps(payload).encode("utf-8")
    headers = {
        "Content-Type": "application/json",
        "X-Api-Key": raw_key,
        "Anthropic-Version": ANTHROPIC_VERSION,
        "User-Agent": "KAAPSTYLE-API-Coordinator/1.0",
    }

    start_time = time.time()
    last_error = None

    for attempt in range(MAX_RETRIES + 1):
        req = urllib.request.Request(
            ANTHROPIC_API_URL, data=data, headers=headers, method="POST"
        )
        try:
            with urllib.request.urlopen(req, timeout=60) as resp:
                elapsed = time.time() - start_time
                status_code = resp.status
                resp_headers = dict(resp.headers)
                request_id = resp_headers.get("request-id", "")
                body = json.loads(resp.read().decode("utf-8"))

                # Extract generated text and thinking
                content_blocks = body.get("content", [])
                text_parts = [
                    b["text"] for b in content_blocks if b.get("type") == "text"
                ]
                text = "\n".join(text_parts)
                thinking_parts = [
                    b.get("thinking", "") for b in content_blocks if b.get("type") == "thinking"
                ]
                thinking_text = "\n".join(thinking_parts)

                stop_reason = body.get("stop_reason", "")
                is_truncated = (stop_reason == "max_tokens")

                usage = body.get("usage", {})
                input_tokens = usage.get("input_tokens", 0)
                output_tokens = usage.get("output_tokens", 0)
                cache_creation = usage.get("cache_creation_input_tokens", 0)
                cache_read = usage.get("cache_read_input_tokens", 0)

                return {
                    "success": True,
                    "is_dry_run": False,
                    "status_code": status_code,
                    "request_id": request_id or body.get("id", ""),
                    "text": text,
                    "thinking": thinking_text,
                    "stop_reason": stop_reason,
                    "is_truncated": is_truncated,
                    "input_tokens": input_tokens,
                    "output_tokens": output_tokens,
                    "cache_creation_tokens": cache_creation,
                    "cache_read_tokens": cache_read,
                    "elapsed_seconds": round(elapsed, 3),
                    "retry_count": attempt,
                    "raw_response": body,
                    "error": None,
                }

        except urllib.error.HTTPError as e:
            elapsed = time.time() - start_time
            status_code = e.code
            err_body = ""
            try:
                err_body = e.read().decode("utf-8")
            except Exception:
                pass

            last_error = f"HTTP {status_code}: {err_body}"

            # Retry on rate limit (429) or temporary server overload (529)
            if status_code in (429, 529) and attempt < MAX_RETRIES:
                sleep_time = BASE_BACKOFF_SECONDS * (2 ** attempt)
                time.sleep(sleep_time)
                continue

            return {
                "success": False,
                "is_dry_run": False,
                "status_code": status_code,
                "request_id": "",
                "text": "",
                "input_tokens": 0,
                "output_tokens": 0,
                "cache_creation_tokens": 0,
                "cache_read_tokens": 0,
                "elapsed_seconds": round(elapsed, 3),
                "retry_count": attempt,
                "raw_response": {},
                "error": last_error,
            }

        except Exception as e:
            elapsed = time.time() - start_time
            last_error = str(e)
            if attempt < MAX_RETRIES:
                time.sleep(BASE_BACKOFF_SECONDS)
                continue

            return {
                "success": False,
                "is_dry_run": False,
                "status_code": 0,
                "request_id": "",
                "text": "",
                "input_tokens": 0,
                "output_tokens": 0,
                "cache_creation_tokens": 0,
                "cache_read_tokens": 0,
                "elapsed_seconds": round(elapsed, 3),
                "retry_count": attempt,
                "raw_response": {},
                "error": last_error,
            }

    return {
        "success": False,
        "is_dry_run": False,
        "status_code": 0,
        "request_id": "",
        "text": "",
        "input_tokens": 0,
        "output_tokens": 0,
        "cache_creation_tokens": 0,
        "cache_read_tokens": 0,
        "elapsed_seconds": round(time.time() - start_time, 3),
        "retry_count": MAX_RETRIES,
        "raw_response": {},
        "error": last_error or "Unknown failure",
    }
