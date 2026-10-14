import 'dart:async';
import 'dart:convert';

import 'package:http/http.dart' as http;

import '../models/auth_tokens.dart';

/// Exception thrown when the auth API returns an error or is unreachable.
class ApiException implements Exception {
  final String message;
  final int? statusCode;

  const ApiException(this.message, {this.statusCode});

  @override
  String toString() => statusCode == null
      ? 'ApiException: $message'
      : 'ApiException($statusCode): $message';
}

/// HTTP client for the authentication endpoints.
class AuthApiService {
  static const String defaultBaseUrl = 'http://localhost:8080/api/auth';
  static const Duration _timeout = Duration(seconds: 15);

  final String baseUrl;
  final http.Client _client;

  AuthApiService({
    this.baseUrl = defaultBaseUrl,
    http.Client? client,
  }) : _client = client ?? http.Client();

  Future<AuthTokens> register({
    required String email,
    required String password,
    String? firstName,
    String? lastName,
  }) {
    final Map<String, dynamic> body = <String, dynamic>{
      'email': email,
      'password': password,
      if (firstName != null && firstName.isNotEmpty) 'firstName': firstName,
      if (lastName != null && lastName.isNotEmpty) 'lastName': lastName,
    };
    return _post('/register', body);
  }

  Future<AuthTokens> login({
    required String email,
    required String password,
  }) {
    return _post('/login', <String, dynamic>{
      'email': email,
      'password': password,
    });
  }

  void dispose() => _client.close();

  Future<AuthTokens> _post(String path, Map<String, dynamic> body) async {
    final Uri uri = Uri.parse('$baseUrl$path');

    http.Response response;
    try {
      response = await _client
          .post(
            uri,
            headers: const <String, String>{
              'Content-Type': 'application/json',
              'Accept': 'application/json',
            },
            body: jsonEncode(body),
          )
          .timeout(_timeout);
    } on TimeoutException {
      throw const ApiException('Request timed out. Please try again.');
    } on http.ClientException catch (e) {
      throw ApiException('Network error: ${e.message}');
    } catch (e) {
      throw ApiException('Unable to reach the server: $e');
    }

    return _handleResponse(response);
  }

  AuthTokens _handleResponse(http.Response response) {
    final int status = response.statusCode;

    if (status == 200 || status == 201) {
      try {
        final dynamic decoded = jsonDecode(utf8.decode(response.bodyBytes));
        if (decoded is! Map<String, dynamic>) {
          throw const FormatException('Unexpected response format');
        }
        return AuthTokens.fromJson(decoded);
      } on FormatException catch (e) {
        throw ApiException('Invalid server response: ${e.message}',
            statusCode: status);
      }
    }

    final String serverMessage = _extractMessage(response);

    switch (status) {
      case 400:
        throw ApiException(
          serverMessage.isNotEmpty
              ? serverMessage
              : 'Invalid request. Please check your input.',
          statusCode: status,
        );
      case 401:
        throw ApiException(
          serverMessage.isNotEmpty
              ? serverMessage
              : 'Invalid email or password.',
          statusCode: status,
        );
      case 409:
        throw ApiException(
          serverMessage.isNotEmpty
              ? serverMessage
              : 'An account with this email already exists.',
          statusCode: status,
        );
      default:
        throw ApiException(
          serverMessage.isNotEmpty
              ? serverMessage
              : 'Unexpected server error (HTTP $status).',
          statusCode: status,
        );
    }
  }

  String _extractMessage(http.Response response) {
    try {
      final dynamic decoded = jsonDecode(utf8.decode(response.bodyBytes));
      if (decoded is Map<String, dynamic>) {
        final dynamic msg =
            decoded['message'] ?? decoded['error'] ?? decoded['detail'];
        if (msg is String) return msg;
      } else if (decoded is String) {
        return decoded;
      }
    } catch (_) {
      // Body was not JSON; fall through to raw body.
    }
    final String raw = response.body.trim();
    return raw.length > 200 ? raw.substring(0, 200) : raw;
  }
}