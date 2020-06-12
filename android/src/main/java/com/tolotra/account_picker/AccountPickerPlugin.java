package com.tolotra.account_picker;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** AccountPickerPlugin */
public class AccountPickerPlugin implements FlutterPlugin, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private ActivityPluginBinding activityBinding;
  private Activity activity;
  private FlutterPluginBinding pluginBinding;

  @Nullable
  private MethodCallHandlerImpl methodCallHandler;
  private AccountPicker location;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    pluginBinding = binding;

    location = new AccountPicker(binding.getApplicationContext(), /* activity= */ null);
    methodCallHandler = new MethodCallHandlerImpl(location);
    methodCallHandler.startListening(binding.getBinaryMessenger());
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    AccountPicker flutterLocation = new AccountPicker(registrar);
    flutterLocation.setActivity(registrar.activity());

    MethodCallHandlerImpl handler = new MethodCallHandlerImpl(flutterLocation);
    handler.startListening(registrar.messenger());
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

    pluginBinding = null;

    if (methodCallHandler != null) {
      methodCallHandler.stopListening();
      methodCallHandler = null;
    }

    location = null;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    location.setActivity(binding.getActivity());
    activityBinding = binding;
    setup(pluginBinding.getBinaryMessenger(), activityBinding.getActivity(), null);
  }

  @Override
  public void onDetachedFromActivity() {
    tearDown();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  private void setup(final BinaryMessenger messenger, final Activity activity,
                     final PluginRegistry.Registrar registrar) {
    if (registrar != null) {
      // V1 embedding setup for activity listeners.
      registrar.addActivityResultListener(location);
    } else {
      // V2 embedding setup for activity listeners.
      activityBinding.addActivityResultListener(location);
    }
  }

  private void tearDown() {
    activityBinding.removeActivityResultListener(location);
  }
}
