import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:local_auth/local_auth.dart';

/// Outcome of a biometric capability check.
enum BiometricAvailability {
  /// Hardware present and at least one biometric enrolled.
  available,

  /// Device has no supported biometric hardware / secure lock screen.
  unsupported,

  /// Hardware present but the user has not enrolled any biometric.
  notEnrolled,
}

/// Outcome of a biometric authentication attempt.
enum BiometricAuthStatus {
  /// The user authenticated successfully.
  success,

  /// The user cancelled or failed the prompt (may retry).
  failed,

  /// Biometrics cannot be used; caller should fall back to PIN / TOTP entry.
  fallbackRequired,

  /// Too many attempts; biometrics are temporarily/permanently locked.
  lockedOut,
}

/// Immutable result returned from [BiometricService.authenticate].
@immutable
class BiometricAuthResult {
  const BiometricAuthResult(this.status, {this.message});

  final BiometricAuthStatus status;

  /// User-friendly message suitable for display (never a raw exception).
  final String? message;

  bool get isSuccess => status == BiometricAuthStatus.success;
  bool get shouldFallback =>
      status == BiometricAuthStatus.fallbackRequired ||
      status == BiometricAuthStatus.lockedOut;
}

/// Wraps the platform biometric APIs (Face ID / Touch ID / Fingerprint).
///
/// This service holds no UI state. Screens depend on it via constructor
/// injection so it can be replaced with a fake in tests.
class BiometricService {
  BiometricService({LocalAuthentication? localAuth})
      : _localAuth = localAuth ?? LocalAuthentication();

  final LocalAuthentication _localAuth;

  static const String defaultLocalizedReason =
      'Authenticate to verify your identity';

  /// Returns whether biometrics are supported and enrolled on this device.
  Future<BiometricAvailability> checkAvailability() async {
    try {
      final bool deviceSupported = await _localAuth.isDeviceSupported();
      final bool canCheck = await _localAuth.canCheckBiometrics;

      if (!deviceSupported || !canCheck) {
        return BiometricAvailability.unsupported;
      }

      final List<BiometricType> enrolled =
          await _localAuth.getAvailableBiometrics();
      if (enrolled.isEmpty) {
        return BiometricAvailability.notEnrolled;
      }
      return BiometricAvailability.available;
    } on PlatformException catch (e) {
      debugPrint('BiometricService.checkAvailability error: ${e.code}');
      return BiometricAvailability.unsupported;
    } catch (e) {
      debugPrint('BiometricService.checkAvailability unexpected error: $e');
      return BiometricAvailability.unsupported;
    }
  }

  /// Convenience: true only when biometrics can be used right now.
  Future<bool> isAvailable() async =>
      (await checkAvailability()) == BiometricAvailability.available;

  /// Returns the biometric types enrolled on the device (may be empty).
  Future<List<BiometricType>> getEnrolledBiometrics() async {
    try {
      return await _localAuth.getAvailableBiometrics();
    } catch (_) {
      return const <BiometricType>[];
    }
  }

  /// Prompts the user to authenticate.
  ///
  /// [localizedReason] is shown by the OS in the system prompt.
  /// When [biometricOnly] is false the OS may offer the device passcode
  /// as a fallback. Never throws; failures are mapped to a
  /// [BiometricAuthResult] so callers can fall back to PIN entry.
  Future<BiometricAuthResult> authenticate({
    String localizedReason = defaultLocalizedReason,
    bool biometricOnly = false,
  }) async {
    final BiometricAvailability availability = await checkAvailability();
    if (availability == BiometricAvailability.unsupported) {
      return const BiometricAuthResult(
        BiometricAuthStatus.fallbackRequired,
        message: 'Biometric sign-in is not available on this device. '
            'Please enter your code instead.',
      );
    }
    if (availability == BiometricAvailability.notEnrolled) {
      return const BiometricAuthResult(
        BiometricAuthStatus.fallbackRequired,
        message: 'No fingerprint or face is set up on this device. '
            'Please enter your code instead.',
      );
    }

    try {
      final bool ok = await _localAuth.authenticate(
        localizedReason: localizedReason,
        biometricOnly: biometricOnly,
        persistAcrossBackgrounding: true,
      );
      if (ok) {
        return const BiometricAuthResult(BiometricAuthStatus.success);
      }
      return const BiometricAuthResult(
        BiometricAuthStatus.failed,
        message: 'We couldn\'t verify you. Please try again.',
      );
    } on PlatformException catch (e) {
      return _mapPlatformException(e);
    } catch (e) {
      debugPrint('BiometricService.authenticate unexpected error: $e');
      return const BiometricAuthResult(
        BiometricAuthStatus.fallbackRequired,
        message: 'Biometric sign-in is unavailable right now. '
            'Please enter your code instead.',
      );
    }
  }

  BiometricAuthResult _mapPlatformException(PlatformException e) {
    debugPrint('BiometricService.authenticate error: ${e.code}');
    switch (e.code) {
      case 'LockedOut':
        return const BiometricAuthResult(
          BiometricAuthStatus.lockedOut,
          message: 'Too many attempts. Please wait a moment or '
              'enter your code instead.',
        );
      case 'PermanentlyLockedOut':
        return const BiometricAuthResult(
          BiometricAuthStatus.lockedOut,
          message: 'Biometrics are locked. Unlock your device or '
              'enter your code instead.',
        );
      case 'NotEnrolled':
      case 'NotAvailable':
      case 'PasscodeNotSet':
        return const BiometricAuthResult(
          BiometricAuthStatus.fallbackRequired,
          message: 'Biometric sign-in is not set up on this device. '
              'Please enter your code instead.',
        );
      default:
        return const BiometricAuthResult(
          BiometricAuthStatus.failed,
          message: 'Biometric verification failed. Please try again.',
        );
    }
  }

  /// Cancels any in-flight authentication prompt.
  Future<void> cancel() async {
    try {
      await _localAuth.stopAuthentication();
    } catch (_) {
      // Best effort; nothing to surface to the user.
    }
  }
}