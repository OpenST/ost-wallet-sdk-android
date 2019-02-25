package ost.com.sampleostsdkapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Fragment representing the login screen for Shrine.
 */
public class LoginFragment extends Fragment {

    private LoginViewController mLoginViewController;
    private TextInputLayout mMobileTextInput;
    private EditText mNumberEditText;
    private EditText mUserNameEditText;
    private TextInputLayout mUserNameTextInput;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mappy_login_fragment, container, false);
        mUserNameEditText = view.findViewById(R.id.user_name_edit);
        mMobileTextInput = view.findViewById(R.id.mobile_number_input);
        mNumberEditText = view.findViewById(R.id.mobile_edit_number);
        mUserNameTextInput = view.findViewById(R.id.user_name_input);
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
                mLoginViewController.onButtonAction(mUserNameEditText.getText(), mNumberEditText.getText(), false);
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

    public void setMobileNumberError() {
        mMobileTextInput.setError(getString(R.string.shr_error_password));
    }

    public void setUserNameError() {
        mUserNameTextInput.setError(getString(R.string.shr_error_user_name));
    }

    public void resetNumberError() {
        mMobileTextInput.setError(null);
    }

    public void resetUserNameError() {
        mUserNameTextInput.setError(null);
    }

    public void showProgress(boolean b) {

    }
}
