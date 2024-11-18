package com.tolotra.account_picker

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** AccountPickerPlugin */
class AccountPickerPlugin : AccountPickerPluginMethod(), ActivityAware {


    // ActivityAware Methods
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        val componentActivity = activity as? ComponentActivity
            ?: throw IllegalStateException("Activity must extend ComponentActivity to use registerForActivityResult")

        accountPicker = AccountPicker(componentActivity)
//        binding.addActivityResultListener(accountPicker)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
        accountPicker = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity

        val componentActivity = activity as? ComponentActivity
            ?: throw IllegalStateException("Activity must extend ComponentActivity to use registerForActivityResult")

        accountPicker = AccountPicker(componentActivity)
//        binding.addActivityResultListener(accountPicker!!)
    }

    override fun onDetachedFromActivity() {
        activity = null
        accountPicker = null
    }
}

abstract class AccountPickerPluginMethod : AccountPickerPluginEngine() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "requestEmailHint") {
            accountPicker?.requestEmailHint(result)
        } else if (call.method == "requestPhoneHint") {
            accountPicker?.requestPhoneHint(result)
        } else {
            result.notImplemented()
        }
    }
}

abstract class AccountPickerPluginEngine : FlutterPlugin, MethodCallHandler {

    /// Reference to the Activity and Context
    var activity: Activity? = null
    var context: Context? = null
    var accountPicker: AccountPicker? = null

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "account_picker")
        channel.setMethodCallHandler(this)
    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

}
