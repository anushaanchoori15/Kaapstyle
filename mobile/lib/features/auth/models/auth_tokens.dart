/// Authentication token payload returned by the auth API
/// after a successful register or login call.
class AuthTokens {
  final String accessToken;
  final String tokenType;
  final String email;
  final int? userId;

  const AuthTokens({
    required this.accessToken,
    required this.tokenType,
    required this.email,
    this.userId,
  });

  factory AuthTokens.fromJson(Map<String, dynamic> json) {
    final dynamic rawToken = json['accessToken'] ?? json['access_token'];
    final dynamic rawType = json['tokenType'] ?? json['token_type'];
    final dynamic rawEmail = json['email'];
    final dynamic rawUserId = json['userId'] ?? json['user_id'];

    if (rawToken == null || rawToken is! String || rawToken.isEmpty) {
      throw const FormatException('AuthTokens: missing accessToken');
    }

    int? userId;
    if (rawUserId is int) {
      userId = rawUserId;
    } else if (rawUserId is num) {
      userId = rawUserId.toInt();
    } else if (rawUserId is String) {
      userId = int.tryParse(rawUserId);
    }

    return AuthTokens(
      accessToken: rawToken,
      tokenType: (rawType is String && rawType.isNotEmpty) ? rawType : 'Bearer',
      email: rawEmail is String ? rawEmail : '',
      userId: userId,
    );
  }

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'accessToken': accessToken,
      'tokenType': tokenType,
      'email': email,
      'userId': userId,
    };
  }

  @override
  String toString() =>
      'AuthTokens(tokenType: $tokenType, email: $email, userId: $userId)';
}