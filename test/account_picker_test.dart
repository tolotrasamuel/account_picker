import 'package:flutter_test/flutter_test.dart';
import 'package:account_picker/account_picker.dart';
import 'package:account_picker/account_picker_platform_interface.dart';
import 'package:account_picker/account_picker_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockAccountPickerPlatform
    with MockPlatformInterfaceMixin
    implements AccountPickerPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final AccountPickerPlatform initialPlatform = AccountPickerPlatform.instance;

  test('$MethodChannelAccountPicker is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelAccountPicker>());
  });

  test('getPlatformVersion', () async {
    AccountPicker accountPickerPlugin = AccountPicker();
    MockAccountPickerPlatform fakePlatform = MockAccountPickerPlatform();
    AccountPickerPlatform.instance = fakePlatform;

    expect(await accountPickerPlugin.getPlatformVersion(), '42');
  });
}
