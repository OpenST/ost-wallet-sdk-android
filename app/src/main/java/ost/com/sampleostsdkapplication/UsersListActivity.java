package ost.com.sampleostsdkapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import ost.com.sampleostsdkapplication.fragments.AbortDeviceRecoveryFragment;
import ost.com.sampleostsdkapplication.fragments.CreateSessionFragment;
import ost.com.sampleostsdkapplication.fragments.DeviceRecoveryFragment;
import ost.com.sampleostsdkapplication.fragments.PaperWalletFragment;
import ost.com.sampleostsdkapplication.fragments.QRPerformFragment;
import ost.com.sampleostsdkapplication.fragments.ResetPinFragment;
import ost.com.sampleostsdkapplication.fragments.SetUpUserFragment;
import ost.com.sampleostsdkapplication.fragments.UserDetailsFragment;
import ost.com.sampleostsdkapplication.fragments.UserListFragment;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UsersListActivity extends MappyBaseActivity implements
        SetUpUserFragment.OnSetUpUserFragmentListener,
        ResetPinFragment.OnResetPinFragmentListener,
        CreateSessionFragment.OnCreateSessionFragmentListener {

    private static final String TAG = "UsersListActivity";
    private static final int QR_REQUEST_CODE = 2;
    private UserDetailsFragment userDetailsFragment;
    private SetUpUserFragment userSetupFragment;
    private PaperWalletFragment paperWalletFragment;
    private ResetPinFragment resetPinFragment;
    private CreateSessionFragment createSessionFragment;
    private QRPerformFragment qrPerformFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list_activity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        UserListFragment userListFragment = UserListFragment.newInstance();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, userListFragment, "users_list");
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users_list, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        LogInUser logInUser = ((App) getApplication()).getLoggedUser();
        String userId = logInUser.getOstUserId();

        if (id == R.id.user_detail) {
            Log.d(TAG, "Show user details clicked");
            loadUserDetailsFragment(logInUser.getTokenId(), userId);
        } else if (id == R.id.activate_user) {
            Log.d(TAG, "Activate User Clicked");
            loadSetUpUserFragment(logInUser.getTokenId(), userId);
        } else if (id == R.id.add_device) {
            Log.d(TAG, "Add device clicked");
            Bitmap qrImage = OstSdk.getAddDeviceQRCode(userId);
            if (null == qrImage) {
                Toast.makeText(getApplicationContext(), "QR building issue... Please check Logs", Toast.LENGTH_SHORT);
                return false;
            }
            Log.i(TAG, "showing QR code");
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            qrImage.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] byteArray = bStream.toByteArray();

            Intent anotherIntent = new Intent(getApplicationContext(), QR_view.class);
            anotherIntent.putExtra("image", byteArray);
            startActivity(anotherIntent);
        } else if (id == R.id.scan_qr) {
            loadQRPerformFragment(logInUser.getTokenId(), userId);
            Intent intent = new Intent(getApplicationContext(), SimpleScannerActivity.class);
            startActivityForResult(intent, QR_REQUEST_CODE);
        } else if (id == R.id.add_session) {
            loadCreateSessionFragment(logInUser.getTokenId(), userId);
        } else if (id == R.id.show_paper_wallet) {
            Log.d(TAG, "Show Paper Wallet Clicked");
            loadPaperWalletFragment(logInUser.getTokenId(), userId, false);
        } else if (id == R.id.device_words) {
            Log.d(TAG, "Add Device using mMnemonicsList Clicked");
            loadPaperWalletFragment(logInUser.getTokenId(), userId, true);
        } else if (id == R.id.transactions) {
            loadQRPerformFragment(logInUser.getTokenId(), userId);
            Intent intent = new Intent(getApplicationContext(), SimpleScannerActivity.class);
            startActivityForResult(intent, QR_REQUEST_CODE);
        } else if (id == R.id.pay_txn) {
            Log.d(TAG, "Execute Pay Transaction Clicked");
            String tokenHolderAddress = "0x3530b7d78132ff484f4a1fe7b6d7a1dd0c94fd2c";
            String amount = "1";
            String ruleName = "Pricer";
            OstSdk.executeTransaction(userId, Arrays.asList(tokenHolderAddress), Arrays.asList(amount), ruleName, new WorkFlowHelper() {
            });
        } else if (id == R.id.reset_pin) {
            Log.d(TAG, "Reset pin");
            loadResetPinFragment(logInUser.getTokenId(), userId);
        } else if (id == R.id.device_recovery) {
            Log.d(TAG, "Device Recovery");
            byte[] appSalt = logInUser.getPassphrasePrefix().getBytes(UTF_8);
            loadDeviceRecoveryFragment(logInUser.getTokenId(), userId, appSalt);
        } else if (id == R.id.device_abort_recovery) {
            Log.d(TAG, "Device Recovery");
            byte[] appSalt = logInUser.getPassphrasePrefix().getBytes(UTF_8);
            AbortRecoveryFragment(logInUser.getTokenId(), userId, appSalt);
        }
        return super.onOptionsItemSelected(item);
    }

    private void AbortRecoveryFragment(String tokenId, String userId, byte[] appSalt) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AbortDeviceRecoveryFragment deviceRecoveryFragment = AbortDeviceRecoveryFragment.newInstance(tokenId, userId, appSalt);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, deviceRecoveryFragment, "abort_device_recovery");
        transaction.addToBackStack("abort_device_recovery");
        transaction.commit();
    }

    private void loadDeviceRecoveryFragment(String tokenId, String userId, byte[] appSalt) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DeviceRecoveryFragment deviceRecoveryFragment = DeviceRecoveryFragment.newInstance(tokenId, userId, appSalt);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, deviceRecoveryFragment, "device_recovery");
        transaction.addToBackStack("device_recovery");
        transaction.commit();
    }

    private void loadUserDetailsFragment(String tokenId, String userId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        userDetailsFragment = UserDetailsFragment.newInstance(tokenId, userId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, userDetailsFragment, "user_details");
        transaction.addToBackStack("users_list");
        transaction.commit();
    }

    private void loadSetUpUserFragment(String tokenId, String userId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        userSetupFragment = SetUpUserFragment.newInstance(tokenId, userId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, userSetupFragment, "user_setup");
        transaction.addToBackStack("users_list");
        transaction.commit();
    }

    private void loadPaperWalletFragment(String tokenId, String userId, boolean loadForAuthorize) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        paperWalletFragment = PaperWalletFragment.newInstance(tokenId, userId, loadForAuthorize);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, paperWalletFragment, "paper_wallet");
        transaction.addToBackStack("users_list");
        transaction.commit();
    }

    private void loadResetPinFragment(String tokenId, String userId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        resetPinFragment = ResetPinFragment.newInstance(tokenId, userId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, resetPinFragment, "reset_pin");
        transaction.addToBackStack("users_list");
        transaction.commit();
    }

    private void loadCreateSessionFragment(String tokenId, String userId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        createSessionFragment = CreateSessionFragment.newInstance(tokenId, userId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, createSessionFragment, "create_session");
        transaction.addToBackStack("users_list");
        transaction.commit();
    }

    private void loadQRPerformFragment(String tokenId, String userId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        qrPerformFragment = QRPerformFragment.newInstance(tokenId, userId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, qrPerformFragment, "qr_perform");
        transaction.addToBackStack("users_list");
        transaction.commit();
    }

    private void getPinDialog(final DialogCallback callback, String message) {
        LayoutInflater li = LayoutInflater.from(UsersListActivity.this);
        View promptsView = li.inflate(R.layout.pin_prompt, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UsersListActivity.this);
        alertDialogBuilder.setView(promptsView);

        final TextView label = promptsView.findViewById(R.id.textView);

        label.setText(message);
        final EditText userInput = promptsView.findViewById(R.id.editTextDialogUserInput);

        boolean errorFlag = false;
        // set dialog message
        alertDialogBuilder
                .setCancelable(false);

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final Button cancelButton = promptsView.findViewById(R.id.buttonCancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callback.onCancel();
            }
        });
        final Button doneButton = promptsView.findViewById(R.id.buttonDone);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = userInput.getText().toString();
                if (pin.length() < 6) {
                    Log.w(TAG, "Pin length to small");
                    getPinDialog(callback, "Pin size less than 6 char enter again:");
                } else {
                    alertDialog.dismiss();
                    callback.onSubmit(pin);
                }
            }
        });

        // show it
        alertDialog.show();
    }

    public void getPinDialog(final DialogCallback callback) {
        getPinDialog(callback, "Enter Pin : ");
    }

    public void showPinDialog(final OstPinAcceptInterface ostPinAcceptInterface) {
        final LogInUser logInUser = ((App) getApplication()).getLoggedUser();
        DialogCallback callback = new DialogCallback() {
            @Override
            public void onSubmit(String pin) {
                UserPassphrase passphrase = new UserPassphrase(logInUser.getOstUserId(), pin, logInUser.getPassphrasePrefix());
                ostPinAcceptInterface.pinEntered(passphrase);
            }

            @Override
            public void onCancel() {
                ostPinAcceptInterface.cancelFlow();
            }
        };
        getPinDialog(callback, "Enter Pin : ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String userId = ((App) getApplicationContext()).getLoggedUser().getOstUserId();
            String returnedResult = data.getData().toString();
            try {
                if (qrPerformFragment != null) {
                    OstSdk.ostPerform(userId, returnedResult, qrPerformFragment);
                    qrPerformFragment.flowStarted();
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSONException while parsing");
            }
        }
    }


    public interface DialogCallback {
        void onSubmit(String pin);

        void onCancel();
    }

    @Override
    public void onBack() {
        super.onBackPressed();
    }

    @Override
    public void onSetupUserSubmit(String pin) {
        Log.d(TAG, "Start user activation process");
        LogInUser logInUser = ((App) getApplication()).getLoggedUser();
        String userId = logInUser.getOstUserId();
        String passphrasePrefix = logInUser.getPassphrasePrefix();
        long expiresAfterInSecs = 2 * 7 * 24 * 60 * 60; //2 weeks
        String spendingLimit = "1000000000000";

        if (userSetupFragment != null) {
            OstSdk.activateUser(new UserPassphrase(userId, pin, passphrasePrefix), expiresAfterInSecs, spendingLimit,
                    userSetupFragment);
            userSetupFragment.flowStarted();
        }
    }

    @Override
    public void onResetPinSubmit(String oldPin, String newPin) {
        Log.d(TAG, "Start Reset pin process");
        LogInUser logInUser = ((App) getApplication()).getLoggedUser();
        byte[] appSalt = logInUser.getPassphrasePrefix().getBytes(UTF_8);
        UserPassphrase currentPassphrase = new UserPassphrase(logInUser.getOstUserId(), oldPin.getBytes(UTF_8), appSalt.clone());
        UserPassphrase newPassphrase = new UserPassphrase(logInUser.getOstUserId(), newPin.getBytes(UTF_8), appSalt.clone());
        if (resetPinFragment != null) {
            OstSdk.resetRecoveryPassphrase(logInUser.getOstUserId(), currentPassphrase, newPassphrase,
                    resetPinFragment);
            resetPinFragment.flowStarted();
        }
    }

    @Override
    public void onCreateSessionSubmit(String spendingLimit, long expiryAfterSecs) {
        LogInUser logInUser = ((App) getApplication()).getLoggedUser();
        if (createSessionFragment != null) {
            OstSdk.addSession(logInUser.getOstUserId(), spendingLimit,
                    expiryAfterSecs, createSessionFragment);
            createSessionFragment.flowStarted();
        }
    }
}