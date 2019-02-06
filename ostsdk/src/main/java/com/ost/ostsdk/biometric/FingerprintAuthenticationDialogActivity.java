package com.ost.ostsdk.biometric;

import android.app.Activity;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ost.ostsdk.R;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
public class FingerprintAuthenticationDialogActivity extends Activity
        implements FingerprintUiHelper.Callback {

    private Button mCancelButton;
    private View mFingerprintContent;

    private Stage mStage = Stage.FINGERPRINT;

    private FingerprintUiHelper mFingerprintUiHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_dialog_container);
        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setFinishOnTouchOutside(false);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onError();
            }
        });

        mFingerprintContent = findViewById(R.id.fingerprint_container);

        mFingerprintUiHelper = new FingerprintUiHelper(
                getSystemService(FingerprintManager.class),
                (ImageView) findViewById(R.id.fingerprint_icon),
                (TextView) findViewById(R.id.fingerprint_status), this);
        updateStage();

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
                mFingerprintContent.setVisibility(View.VISIBLE);
                break;
            case NEW_FINGERPRINT_ENROLLED:
                // Intentional fall through
            case PASSWORD:
                mCancelButton.setText(R.string.cancel);
                mFingerprintContent.setVisibility(View.GONE);
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

    /**
     * Enumeration to indicate which authentication method the user is trying to authenticate with.
     */
    public enum Stage {
        FINGERPRINT,
        NEW_FINGERPRINT_ENROLLED,
        PASSWORD
    }
}