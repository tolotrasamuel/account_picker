import 'dart:async';

import 'package:account_picker/account_picker.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  const MethodChannel channel = MethodChannel('account_picker');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      if (methodCall.method == 'requestEmailHint') {
        return ['foo@example.com', 'google'];
      } else {
        if (methodCall.method == 'requestPhoneHint') {
          return '+1234567890';
        }
      }
      return null;
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('emailHint', () async {
    final EmailResult emailResult =
        await (AccountPicker.emailHint() as FutureOr<EmailResult>);
    expect(emailResult.email, 'foo@example.com');
    expect(emailResult.type, 'google');
  });
  test('phoneHint', () async {
    final String? phoneNumber = await AccountPicker.phoneHint();
    expect(phoneNumber, '+1234567890');
  });
}
