package com.tolotra.account_picker;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

class AccountPicker implements PluginRegistry.ActivityResultListener {
    private final Context applicationContext;
    private MethodChannel.Result pendingHintResult;
    private Activity activity;

    private static final int EMAIL_HINT_REQUEST = 11011;
    private static final int PHONE_HINT_REQUEST = 11012;

    AccountPicker(Context applicationContext, @Nullable Activity activity) {
        this.applicationContext = applicationContext;
        this.activity = activity;
    }

    AccountPicker(PluginRegistry.Registrar registrar) {
        this(registrar.context(), registrar.activity());
    }

    void setActivity(@Nullable Activity activity) {
        this.activity = activity;
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void requestPhoneHint(MethodChannel.Result result) {
        pendingHintResult = result;
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        GoogleApiClient mCredentialsClient = new GoogleApiClient.Builder(activity)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                mCredentialsClient, hintRequest);
        try {
            activity.startIntentSenderForResult(intent.getIntentSender(),
                    PHONE_HINT_REQUEST, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public void requestEmailHint(MethodChannel.Result result) {
        pendingHintResult = result;
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = AccountManager.newChooseAccountIntent(
                    null,
                    null,
                    new String[]{"com.google", "com.google.android.legacyimap"},
                    null,
                    null,
                    null,
                    null);
            activity.startActivityForResult(intent, EMAIL_HINT_REQUEST);

        } else {
            result.error("Platform does not support API", null, null);
        }

    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHONE_HINT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                final String phoneNumber = credential.getId();
                pendingHintResult.success(phoneNumber);
            } else {
                pendingHintResult.success(null);
            }
            return true;
        } else if (requestCode == EMAIL_HINT_REQUEST) {
            String accountType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            pendingHintResult.success(Arrays.asList(accountName, accountType));
        }
        return false;
    }
}
