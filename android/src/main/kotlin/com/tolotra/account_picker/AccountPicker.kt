package com.tolotra.account_picker

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.Nullable
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import java.util.UUID
import androidx.activity.result.ActivityResultCallback as ActivityResultCallback1

class AccountPicker(
    private var activity: Activity?
) {
    private var pendingHintResult: MethodChannel.Result? = null

    companion object {
        private const val TAG = "AccountPicker"
    }

    // context getter
    private val applicationContext: Context?
        get() = activity?.applicationContext


    fun <I, O> ComponentActivity.registerActivityResultLauncher(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback1<O>
    ): ActivityResultLauncher<I> {
        val key = UUID.randomUUID().toString()
        return activityResultRegistry.register(key, contract, callback)
    }

    fun requestPhoneHint(result: MethodChannel.Result) {
        pendingHintResult = result
        Log.d(TAG, "Requesting phone hint")

        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val context = applicationContext ?: return
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(Auth.CREDENTIALS_API)
            .build()


        val componentActivity = activity as? ComponentActivity
            ?: throw IllegalStateException("Activity must extend ComponentActivity to use registerForActivityResult")

        val resultLauncher =
            componentActivity.registerActivityResultLauncher(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode != Activity.RESULT_OK) {
                    pendingHintResult?.success(null)
                    return@registerActivityResultLauncher
                }
                val data = result.data
                val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
                val phoneNumber = credential?.id
                pendingHintResult?.success(phoneNumber)
            }
        val hintPickerIntent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest)

        try {
            val intentBuilder = IntentSenderRequest.Builder(hintPickerIntent.intentSender)
            val intentSenderRequest = intentBuilder.build()

            // Launch the intent
            resultLauncher.launch(intentSenderRequest)
        } catch (e: Exception) {
            Log.e("HintPicker", "Failed to launch hint picker", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestEmailHint(result: MethodChannel.Result) {
        pendingHintResult = result
        Log.d(TAG, "Requesting email hint")

        val filter: List<Account>? = null
        val intent = AccountManager.newChooseAccountIntent(
            null,
            filter,
            arrayOf("com.google", "com.google.android.legacyimap"),
            null,
            null,
            null,
            null
        )


        val componentActivity = activity as? ComponentActivity
            ?: throw IllegalStateException("Activity must extend ComponentActivity to use registerForActivityResult")

        val resultLauncher =
            componentActivity.registerActivityResultLauncher(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != Activity.RESULT_OK) {
                    pendingHintResult?.success(null)
                    return@registerActivityResultLauncher
                }
                val data = result.data
                val accountName = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                val accountType = data?.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
                pendingHintResult?.success(listOf(accountName, accountType))

            }
        try {
            resultLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            result.error("IntentSender error", null, null)
        }
    }

}
