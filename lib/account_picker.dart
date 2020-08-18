import 'dart:async';

import 'package:flutter/services.dart';

class EmailResult {
  /// The email. Example `foo@example.com`
  final String email;

  /// Email type is the account type to which this email was registered on the device
  /// for example it could  be `google` if this was a google account
  final String type;

  EmailResult._(this.email, this.type);
}

class AccountPicker {
  static const MethodChannel _channel = const MethodChannel('account_picker');

  ///
  /// Show the email hint if available, it returns EmailResult
  /// If not available, it will never callback
  ///
  static Future<EmailResult> emailHint() async {
    final List<dynamic> emailResult = await _channel.invokeMethod('requestEmailHint');
    if (emailResult != null) {
      return EmailResult._(emailResult[0], emailResult[1]);
    }
    return null;
  }

  ///
  /// Show the phone hint if available, it returns String
  /// If not available, it will never callback
  ///
  static Future<String> phoneHint() async {
    final String phone = await _channel.invokeMethod('requestPhoneHint');
    return phone;
  }
}
