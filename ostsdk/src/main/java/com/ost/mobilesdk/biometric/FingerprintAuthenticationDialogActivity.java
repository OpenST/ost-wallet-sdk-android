package com.ost.mobilesdk.biometric;

import android.app.Activity;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ost.mobilesdk.R;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
public class FingerprintAuthenticationDialogActivity extends Activity
        implements FingerprintUiHelper.Callback {

    private static final String TAG = "BiometricDailog";
    private TextView mCancelButton;

    private Stage mStage = Stage.FINGERPRINT;

    private FingerprintUiHelper mFingerprintUiHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_dialog_container);
        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setFinishOnTouchOutside(false);
        mCancelButton = findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onError();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintUiHelper = new FingerprintUiHelper(
                    getSystemService(FingerprintManager.class),
                    findViewById(R.id.fingerprint_icon),
                    findViewById(R.id.fingerprint_status), this);
            updateStage();
        } else {
            Log.e(TAG, "Bio metric is not supported for api less than 23");
            finish();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mFingerprintUiHelper.startListening(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    private void updateStage() {
        switch (mStage) {
            case FINGERPRINT:
                mCancelButton.setText(R.string.cancel);
                break;
            case NEW_FINGERPRINT_ENROLLED:
                // Intentional fall through
            case PASSWORD:
                break;
        }
    }

    @Override
    public void onAuthenticated() {
        // Callback from FingerprintUiHelper. Let the activity know that authentication was
        // successful.
        Intent intent = new Intent(OstBiometricAuthentication.INTENT_FILTER_FINGERPRINT_AUTH);
        intent.putExtra(OstBiometricAuthentication.IS_AUTHENTICATED, true);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onError() {
        Intent intent = new Intent(OstBiometricAuthentication.INTENT_FILTER_FINGERPRINT_AUTH);
        intent.putExtra(OstBiometricAuthentication.IS_AUTHENTICATED, false);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onError();
        super.onBackPressed();
    }

    /**
     * Enumeration to indicate which authentication method the user is trying to authenticate with.
     */
    public enum Stage {
        FINGERPRINT,
        NEW_FINGERPRINT_ENROLLED,
        PASSWORD
    }
}