import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:http/http.dart' as http;

/// Screen that asks the user for the 6-digit MFA code from their
/// authenticator app and verifies it against `/api/auth/mfa/verify`.
class MfaVerifyScreen extends StatefulWidget {
  /// Identifier of the user being verified (numeric IDs are passed as String).
  final String userId;

  /// Invoked after the backend confirms the code is valid.
  final VoidCallback? onVerified;

  /// Optional HTTP client (useful for testing). Defaults to a new client.
  final http.Client? httpClient;

  const MfaVerifyScreen({
    super.key,
    required this.userId,
    this.onVerified,
    this.httpClient,
  });

  @override
  State<MfaVerifyScreen> createState() => _MfaVerifyScreenState();
}

class _MfaVerifyScreenState extends State<MfaVerifyScreen> {
  static const String _apiBaseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://10.0.2.2:8080',
  );
  static const String _verifyPath = '/api/auth/mfa/verify';
  static const int _codeLength = 6;

  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController _codeController = TextEditingController();

  late final http.Client _client;
  late final bool _ownsClient;

  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _ownsClient = widget.httpClient == null;
    _client = widget.httpClient ?? http.Client();
  }

  @override
  void dispose() {
    _codeController.dispose();
    if (_ownsClient) {
      _client.close();
    }
    super.dispose();
  }

  String? _validateCode(String? value) {
    final code = (value ?? '').trim();
    if (code.isEmpty) {
      return 'Please enter the code';
    }
    if (code.length != _codeLength || int.tryParse(code) == null) {
      return 'Code must be $_codeLength digits';
    }
    return null;
  }

  void _showSnackBar(String message) {
    if (!mounted) return;
    ScaffoldMessenger.of(context)
      ..hideCurrentSnackBar()
      ..showSnackBar(SnackBar(content: Text(message)));
  }

  Future<void> _verify() async {
    if (_isLoading) return;
    if (!(_formKey.currentState?.validate() ?? false)) return;

    setState(() => _isLoading = true);

    try {
      final response = await _client
          .post(
            Uri.parse('$_apiBaseUrl$_verifyPath'),
            headers: const {'Content-Type': 'application/json'},
            body: jsonEncode({
              'userId': widget.userId,
              'code': _codeController.text.trim(),
            }),
          )
          .timeout(const Duration(seconds: 20));

      if (!mounted) return;

      if (response.statusCode >= 200 && response.statusCode < 300) {
        if (_isRejectedBody(response.body)) {
          _showSnackBar('Invalid code. Please try again.');
          return;
        }
        widget.onVerified?.call();
      } else if (response.statusCode == 400 ||
          response.statusCode == 401 ||
          response.statusCode == 403) {
        _showSnackBar('Invalid code. Please try again.');
      } else {
        _showSnackBar('Verification failed. Please try again later.');
      }
    } catch (e) {
      debugPrint('MFA verify error: $e');
      _showSnackBar('Network error. Please check your connection.');
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  /// Returns true if a 2xx body explicitly says the code was not valid.
  bool _isRejectedBody(String body) {
    if (body.isEmpty) return false;
    try {
      final decoded = jsonDecode(body);
      if (decoded is Map<String, dynamic>) {
        if (decoded['verified'] == false || decoded['valid'] == false) {
          return true;
        }
        if (decoded['success'] == false) return true;
      }
    } catch (_) {
      // Non-JSON body: treat as success based on status code.
    }
    return false;
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: const Text('Two-Factor Verification')),
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 400),
              child: Form(
                key: _formKey,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Icon(
                      Icons.lock_outline,
                      size: 64,
                      color: theme.colorScheme.primary,
                    ),
                    const SizedBox(height: 24),
                    Text(
                      'Enter the 6-digit code from your authenticator app',
                      textAlign: TextAlign.center,
                      style: theme.textTheme.bodyLarge,
                    ),
                    const SizedBox(height: 24),
                    TextFormField(
                      controller: _codeController,
                      enabled: !_isLoading,
                      keyboardType: TextInputType.number,
                      textAlign: TextAlign.center,
                      maxLength: _codeLength,
                      autofocus: true,
                      inputFormatters: [
                        FilteringTextInputFormatter.digitsOnly,
                      ],
                      style: theme.textTheme.headlineSmall?.copyWith(
                        letterSpacing: 8,
                      ),
                      decoration: const InputDecoration(
                        labelText: 'Verification code',
                        hintText: '000000',
                        border: OutlineInputBorder(),
                        counterText: '',
                      ),
                      validator: _validateCode,
                      onFieldSubmitted: (_) => _verify(),
                    ),
                    const SizedBox(height: 24),
                    SizedBox(
                      height: 48,
                      child: ElevatedButton(
                        onPressed: _isLoading ? null : _verify,
                        child: _isLoading
                            ? const SizedBox(
                                width: 22,
                                height: 22,
                                child: CircularProgressIndicator(
                                  strokeWidth: 2.5,
                                ),
                              )
                            : const Text('Verify Code'),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}