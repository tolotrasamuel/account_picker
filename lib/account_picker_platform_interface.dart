import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'account_picker_method_channel.dart';

abstract class AccountPickerPlatform extends PlatformInterface {
  /// Constructs a AccountPickerPlatform.
  AccountPickerPlatform() : super(token: _token);

  static final Object _token = Object();

  static AccountPickerPlatform _instance = MethodChannelAccountPicker();

  /// The default instance of [AccountPickerPlatform] to use.
  ///
  /// Defaults to [MethodChannelAccountPicker].
  static AccountPickerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AccountPickerPlatform] when
  /// they register themselves.
  static set instance(AccountPickerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
