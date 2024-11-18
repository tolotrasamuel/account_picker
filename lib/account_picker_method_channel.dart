import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'account_picker_platform_interface.dart';

/// An implementation of [AccountPickerPlatform] that uses method channels.
class MethodChannelAccountPicker extends AccountPickerPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('account_picker');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
