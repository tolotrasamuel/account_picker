package com.tolotra.account_picker;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.content.ContentValues.TAG;

/**
 * AccountPickerPlugin
 */
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
    private AccountPicker accountPicker;

    @Nullable
    private PluginRegistry.Registrar registrar;


    public static void registerWith(Registrar registrar) {
        Log.d(TAG, "Account Picker Register wiith");
        AccountPickerPlugin instance = new AccountPickerPlugin();
        instance.registrar = registrar;
        instance.accountPicker = new AccountPicker(registrar);
        instance.accountPicker.setActivity(registrar.activity());
        instance.setup();
        instance.initInstance(registrar.messenger());
    }

    private void initInstance(BinaryMessenger binaryMessenger) {
        methodCallHandler = new MethodCallHandlerImpl(accountPicker);
        methodCallHandler.startListening(binaryMessenger);
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        accountPicker = new AccountPicker(binding.getApplicationContext(), /* activity= */ null);
        this.initInstance(binding.getBinaryMessenger());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        if (methodCallHandler != null) {
            methodCallHandler.stopListening();
            methodCallHandler = null;
        }
        accountPicker = null;
    }

    private void attachToActivity(ActivityPluginBinding binding) {
        activityBinding = binding;
        try {
            accountPicker.setActivity(binding.getActivity());
            this.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detachActivity() {
        activityBinding.removeActivityResultListener(accountPicker);
        activityBinding = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.attachToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        this.detachActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.detachActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        this.attachToActivity(binding);
    }

    private void setup() {
        if (registrar != null) {
            Log.d(TAG, "Account Picker Register V1");
            // V1 embedding setup for activity listeners.
            registrar.addActivityResultListener(accountPicker);
        } else {
            Log.d(TAG, "Account Picker Register V2");
            // V2 embedding setup for activity listeners.
            activityBinding.addActivityResultListener(accountPicker);
        }
    }
}
