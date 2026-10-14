import 'dart:async';

import 'package:flutter/material.dart';

/// Dialog shown when the server responds with a rate-limit error (HTTP 429).
///
/// Displays a countdown starting at [retryAfterSeconds]. The "Retry Now"
/// button is enabled only once the countdown reaches zero.
class RateLimitDialog extends StatefulWidget {
  const RateLimitDialog({
    super.key,
    required this.retryAfterSeconds,
    this.onRetry,
  });

  /// Seconds to wait before retrying is allowed.
  final int retryAfterSeconds;

  /// Called when the user taps "Retry Now" after the countdown completes.
  final VoidCallback? onRetry;

  /// Shows the dialog. The user must wait for the countdown (non-dismissible).
  static Future<void> show(
    BuildContext context, {
    int retryAfterSeconds = 60,
    VoidCallback? onRetry,
  }) {
    return showDialog<void>(
      context: context,
      barrierDismissible: false,
      builder: (_) => RateLimitDialog(
        retryAfterSeconds: retryAfterSeconds,
        onRetry: onRetry,
      ),
    );
  }

  @override
  State<RateLimitDialog> createState() => _RateLimitDialogState();
}

class _RateLimitDialogState extends State<RateLimitDialog> {
  late int _remaining;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _remaining = widget.retryAfterSeconds < 0 ? 0 : widget.retryAfterSeconds;
    if (_remaining > 0) {
      _timer = Timer.periodic(const Duration(seconds: 1), _onTick);
    }
  }

  void _onTick(Timer timer) {
    if (!mounted) return;
    setState(() => _remaining--);
    if (_remaining <= 0) timer.cancel();
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  void _handleRetry() {
    Navigator.of(context).pop();
    widget.onRetry?.call();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return AlertDialog(
      icon: Icon(
        Icons.warning_amber_rounded,
        color: theme.colorScheme.error,
        size: 40,
      ),
      title: const Text('Too Many Requests'),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text('Please wait before trying again.'),
          const SizedBox(height: 16),
          Text(
            _remaining > 0 ? 'Retry in ${_remaining}s' : 'You can retry now.',
            style: theme.textTheme.titleMedium,
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: _remaining == 0 ? _handleRetry : null,
          child: const Text('Retry Now'),
        ),
      ],
    );
  }
}