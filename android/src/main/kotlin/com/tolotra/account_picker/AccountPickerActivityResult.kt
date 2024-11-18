package com.tolotra.account_picker

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import java.lang.Exception

class AccountPickerActivityResult(
    private var activity: Activity?
) : PluginRegistry.ActivityResultListener {

    private var pendingHintResult: MethodChannel.Result? = null
    private fun generateRequestCode(name: String): Int {
        return name.hashCode() and 0xFFFF // Ensure it fits within 16-bit limit
    }
    private val PHONE_HINT_REQUEST_CODE = generateRequestCode("PHONE_HINT")
    private val EMAIL_HINT_REQUEST_CODE = generateRequestCode("EMAIL_HINT")

    companion object {
        private const val TAG = "AccountPickerActivityResult"
//        private const val PHONE_HINT_REQUEST_CODE = 1001
//        private const val EMAIL_HINT_REQUEST_CODE = 1002
    }

    // context getter
    private val applicationContext: Context?
        get() = activity?.applicationContext


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

        val hintPickerIntent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest)

        try {
            activity?.startIntentSenderForResult(
                hintPickerIntent.intentSender,
                PHONE_HINT_REQUEST_CODE,
                null,
                0,
                0,
                0
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch hint picker", e)
            pendingHintResult?.success(null)
        }
    }

    fun requestEmailHint(result: MethodChannel.Result) {
        pendingHintResult = result
        Log.d(TAG, "Requesting email hint")


        val filter: List<Account>? = null
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AccountManager.newChooseAccountIntent(
                null,
                filter,
                arrayOf("com.google", "com.google.android.legacyimap"),
                null,
                null,
                null,
                null
            )
        } else {
            TODO("VERSION.SDK_INT < M")
            return;
        }

        try {
            activity?.startActivityForResult(intent, EMAIL_HINT_REQUEST_CODE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch account picker", e)
            pendingHintResult?.success(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        when (requestCode) {
            PHONE_HINT_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
                    val phoneNumber = credential?.id
                    pendingHintResult?.success(phoneNumber)
                } else {
                    pendingHintResult?.success(null)
                }
                return true
            }

            EMAIL_HINT_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val accountName = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    val accountType = data?.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
                    pendingHintResult?.success(listOf(accountName, accountType))
                } else {
                    pendingHintResult?.success(null)
                }
                return true
            }
        }
        return false
    }
}
