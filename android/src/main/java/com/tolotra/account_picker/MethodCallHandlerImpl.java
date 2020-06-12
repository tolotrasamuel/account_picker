package com.tolotra.account_picker;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import static android.content.ContentValues.TAG;

final class MethodCallHandlerImpl implements MethodChannel.MethodCallHandler {

    private static final String METHOD_CHANNEL_NAME = "account_picker";

    private final com.tolotra.account_picker.AccountPicker accountPicker;
    MethodCallHandlerImpl(AccountPicker location) {
        this.accountPicker = location;
    }
    @Nullable
    private MethodChannel channel;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } if (call.method.equals("requestEmailHint")) {
            accountPicker.requestEmailHint(result);
        } else if(call.method.equals("requestPhoneHint")){
            accountPicker.requestPhoneHint(result);
        }
//        else {
//            result.notImplemented();
//        }
    }
    /**
     * Registers this instance as a method call handler on the given
     * {@code messenger}.
     */
    void startListening(BinaryMessenger messenger) {
        if (channel != null) {
            Log.wtf(TAG, "Setting a method call handler before the last was disposed.");
            stopListening();
        }

        channel = new MethodChannel(messenger, METHOD_CHANNEL_NAME);
        channel.setMethodCallHandler(this);
    }
    /**
     * Clears this instance from listening to method calls.
     */
    void stopListening() {
        if (channel == null) {
            Log.d(TAG, "Tried to stop listening when no MethodChannel had been initialized.");
            return;
        }

        channel.setMethodCallHandler(null);
        channel = null;
    }
}