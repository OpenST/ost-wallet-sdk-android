package ost.com.sampleostsdkapplication.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.OstWorkflowContext;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;

import org.json.JSONObject;

import ost.com.sampleostsdkapplication.App;
import ost.com.sampleostsdkapplication.LogInUser;
import ost.com.sampleostsdkapplication.LoginViewController;
import ost.com.sampleostsdkapplication.MappyApiClient;
import ost.com.sampleostsdkapplication.R;
import ost.com.sampleostsdkapplication.UsersListActivity;

import static ost.com.sampleostsdkapplication.Constants.OST_USER_ID;

/**
 * Fragment representing the login screen for OstDemoApp.
 */
public class LoginFragment extends BaseFragment implements
        LoginViewController.LoginFragmentInterface {

    private static final String TAG = "LoginFragment";
    private LoginViewController mLoginViewController;
    private TextInputLayout mMobileTextInput;
    private EditText mNumberEditText;
    private EditText mUserNameEditText;
    private TextInputLayout mUserNameTextInput;
    private ProgressBar mProgressView;
    private LinearLayout mLoginFormView;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mappy_login_fragment, container, false);
        mUserNameEditText = view.findViewById(R.id.user_name_edit);
        mMobileTextInput = view.findViewById(R.id.mobile_number_input);
        mNumberEditText = view.findViewById(R.id.mobile_edit_number);
        mUserNameTextInput = view.findViewById(R.id.user_name_input);

        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);
        MaterialButton createAccountButton = view.findViewById(R.id.create_account_button);


        MaterialButton nextButton = view.findViewById(R.id.next_button);

        mLoginViewController = new LoginViewController(this);

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginViewController.onButtonAction(mUserNameEditText.getText(), mNumberEditText.getText(), false);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginViewController.onButtonAction(mUserNameEditText.getText(), mNumberEditText.getText(), true);
            }
        });

        // Clear the error once more than 8 characters are typed.
        mNumberEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                mLoginViewController.onNumberListner(mNumberEditText.getText());
                return false;
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoginViewController.onDestroy();
    }

    @Override
    public void setMobileNumberError(String errorText) {
        mMobileTextInput.setError(errorText);
    }

    @Override
    public void setUserNameError(String errorText) {
        mUserNameTextInput.setError(errorText);
    }

    @Override
    public void resetNumberError() {
        mMobileTextInput.setError(null);
    }

    @Override
    public void resetUserNameError() {
        mUserNameTextInput.setError(null);
    }

    @Override
    public void showProgress(boolean show) {
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

    @Override
    public void startUserListActivity() {
        if (null == getActivity()) {
            Log.e(TAG, "Activity is null");
        }
        Activity activity = getActivity();
        LogInUser logInUser = ((App) activity.getApplicationContext()).getLoggedUser();
        OstSdk.setupDevice(logInUser.getOstUserId(), logInUser.getTokenId(), LoginFragment.this);
    }

    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {
        Log.i(TAG, String.format("Device Object %s ", apiParams.toString()));
        if (null == getActivity()) {
            Log.e(TAG, "Activity is null");
            ostDeviceRegisteredInterface.cancelFlow();
            return;
        }
        LogInUser logInUser = ((App) getActivity().getApplicationContext()).getLoggedUser();
        String mUserId = logInUser.getId();
        new MappyApiClient().registerDevice(mUserId, apiParams, new MappyApiClient.Callback() {
            @Override
            public void onResponse(boolean success, JSONObject response) {
                if (success) {
                    ostDeviceRegisteredInterface.deviceRegistered(response);
                } else {
                    ostDeviceRegisteredInterface.cancelFlow();
                }
            }
        });
        super.registerDevice(apiParams, ostDeviceRegisteredInterface);
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        super.flowComplete(ostWorkflowContext, ostContextEntity);
        Activity activity = getActivity();
        if (null == getActivity()) {
            Log.e(TAG, "Activity is null");
            return;
        }
        LogInUser logInUser = ((App) activity.getApplicationContext()).getLoggedUser();
        String userId = logInUser.getOstUserId();

        Intent userListIntent = new Intent(activity.getApplicationContext(), UsersListActivity.class);
        userListIntent.putExtra(OST_USER_ID, userId);
        userListIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.getApplicationContext().startActivity(userListIntent);
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        super.flowInterrupt(ostWorkflowContext, ostError);
    }
}
