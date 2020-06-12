import 'dart:async';

import 'package:flutter/services.dart';
class EmailResult {
  final String email;
  final String type;

  EmailResult._(this.email, this.type);
}
class AccountPicker {
  static const MethodChannel _channel =
      const MethodChannel('account_picker');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
  static Future<EmailResult> emailHint() async {
    final List<dynamic> emailResult = await _channel.invokeMethod('requestEmailHint');
    if(emailResult!=null){
      return EmailResult._(emailResult[0], emailResult[1]);
    }
    return null;
  }

  static Future<String> phoneHint() async {
    final String phone = await _channel.invokeMethod('requestPhoneHint');
    return phone;
  }

}
