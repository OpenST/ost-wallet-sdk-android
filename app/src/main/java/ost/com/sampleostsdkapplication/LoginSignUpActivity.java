package ost.com.sampleostsdkapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ost.mobilesdk.OstSdk;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginSignUpActivity extends MappyBaseActivity {

    private static final String TAG = "LoginSignUpActivity";

    private static final String REGISTER_TYPE = "register_type";
    private static final String OST_USER_ID = "ost_user_id";


    // UI references.
    private EditText mName;
    private EditText mMobileNumber;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mDescription;
    private boolean mIsRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        mIsRegister = getIntent().getBooleanExtra(REGISTER_TYPE, false);
        // Set up the login form.
        mName = findViewById(R.id.name);

        mMobileNumber = findViewById(R.id.mobile_number);
        mMobileNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mDescription = findViewById(R.id.description);

        mDescription.setVisibility(mIsRegister ? View.VISIBLE : View.GONE);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setText(mIsRegister ? R.string.action_register : R.string.sign_in);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(LoginSignUpActivity.this);
                attemptLogin();
            }
        });

        Button mRegisterForm = (Button) findViewById(R.id.toggle_button);
        mRegisterForm.setText(mIsRegister ? R.string.login_form : R.string.register_form);
        mRegisterForm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsRegister = !mIsRegister;
                mDescription.setVisibility(mIsRegister ? View.VISIBLE : View.GONE);
                mSignInButton.setText(mIsRegister ? R.string.action_register : R.string.sign_in);
                mRegisterForm.setText(mIsRegister ? R.string.login_form : R.string.register_form);
                hideSoftKeyboard(LoginSignUpActivity.this);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mName.setError(null);
        mMobileNumber.setError(null);

        // Store values at the time of the login attempt.
        String name = mName.getText().toString();
        String mobileNumber = mMobileNumber.getText().toString();
        String description = mDescription.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mobileNumber) && !isMobileNumberValid(mobileNumber)) {
            mMobileNumber.setError(getString(R.string.error_invalid_password));
            focusView = mMobileNumber;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            cancel = true;
        } else if (!isNameValid(name)) {
            mName.setError(getString(R.string.error_invalid_email));
            focusView = mName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            if (mIsRegister) {
                new MappyApiClient().createUser(name, mobileNumber, description, new MappyApiClient.Callback() {
                    @Override
                    public void onResponse(boolean success, JSONObject response) {
                        Log.i(TAG, "On User Register response");
                        parseResponse(response);
                    }
                });
            } else {
                new MappyApiClient().loginUser(name, mobileNumber, new MappyApiClient.Callback() {
                    @Override
                    public void onResponse(boolean success, JSONObject response) {
                        Log.i(TAG, "On User Login response");
                        parseResponse(response);
                    }
                });
            }
        }
    }

    private void parseResponse(JSONObject response) {
        if (null != response) {
            Toast.makeText(getApplicationContext(), mIsRegister ? "User Registered" : "User Authenticated", Toast.LENGTH_SHORT).show();
            Intent userListIntent = new Intent(LoginSignUpActivity.this, UsersListActivity.class);

            Log.i(TAG, String.format("JSON Response : %s", response.toString()));

            LogInUser logInUser = new LogInUser(response);
            ((App)getApplicationContext()).setLoggedUser(logInUser);
            String userId = logInUser.getOstUserId();
            String tokenId = logInUser.getTokenId();
            if (null != userId) {
                OstSdk.setupDevice(userId, tokenId , new WorkFlowHelper(getApplicationContext()));
                userListIntent.putExtra(OST_USER_ID, userId);
                startActivity(userListIntent);
                showProgress(false);
                return;
            }
            Log.i(TAG, "UserId is null");
        }
        showProgress(false);
        mName.setError(getString(R.string.error_incorrect_name));
        mMobileNumber.setError(getString(R.string.error_incorrect_mobilenumber));
        mMobileNumber.requestFocus();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private boolean isNameValid(String name) {
        return name.matches("[A-Za-z0-9]+");
    }

    private boolean isMobileNumberValid(String password) {
        return password.length() > 9;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}