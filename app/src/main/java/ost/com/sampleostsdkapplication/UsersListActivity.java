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
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import ost.com.sampleostsdkapplication.fragments.AbortDeviceRecoveryFragment;
import ost.com.sampleostsdkapplication.fragments.BaseFragment;
import ost.com.sampleostsdkapplication.fragments.CreateSessionFragment;
import ost.com.sampleostsdkapplication.fragments.DeviceRecoveryFragment;
import ost.com.sampleostsdkapplication.fragments.LogoutFragment;
import ost.com.sampleostsdkapplication.fragments.PaperWalletFragment;
import ost.com.sampleostsdkapplication.fragments.QRPerformFragment;
import ost.com.sampleostsdkapplication.fragments.ResetPinFragment;
import ost.com.sampleostsdkapplication.fragments.RuleTransactionFragment;
import ost.com.sampleostsdkapplication.fragments.SetUpUserFragment;
import ost.com.sampleostsdkapplication.fragments.UserDetailsFragment;
import ost.com.sampleostsdkapplication.fragments.UserListFragment;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UsersListActivity extends MappyBaseActivity implements
        BaseFragment.OnBaseFragmentListener,
        UserAdapter.OnItemSelectedListener,
        LogoutFragment.OnLogoutFragmentListener {

    private static final String TAG = "OstUsersListActivity";
    private static final int QR_REQUEST_CODE = 2;
    private QRPerformFragment qrPerformFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list_activity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        UserListFragment userListFragment = UserListFragment.newInstance(this);
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
    protected void onResume() {
        super.onResume();
        LogInUser logInUser = ((App) getApplicationContext()).getLoggedUser();
        if (OstUser.CONST_STATUS.CREATED
                .equalsIgnoreCase(
                        logInUser.getOstUser().getStatus())) {
            loadSetUpUserFragment(logInUser.getTokenId(), logInUser.getOstUserId(), logInUser.getPassphrasePrefix());
        }
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
            loadSetUpUserFragment(logInUser.getTokenId(), userId, logInUser.getPassphrasePrefix());
        } else if (id == R.id.add_device) {
            Log.d(TAG, "Add device clicked");
            Bitmap qrImage = OstSdk.getAddDeviceQRCode(userId);
            if (null == qrImage) {
                Toast.makeText(getApplicationContext(),
                        "QR building issue... Please check Logs",
                        Toast.LENGTH_SHORT)
                        .show();
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
            byte[] appSalt = logInUser.getPassphrasePrefix().getBytes(UTF_8);
            loadResetPinFragment(logInUser.getTokenId(), userId, appSalt);
        } else if (id == R.id.device_recovery) {
            Log.d(TAG, "Device Recovery");
            byte[] appSalt = logInUser.getPassphrasePrefix().getBytes(UTF_8);
            loadDeviceRecoveryFragment(logInUser.getTokenId(), userId, appSalt);
        } else if (id == R.id.device_abort_recovery) {
            Log.d(TAG, "Device Recovery");
            byte[] appSalt = logInUser.getPassphrasePrefix().getBytes(UTF_8);
            AbortRecoveryFragment(logInUser.getTokenId(), userId, appSalt);
        } else if (id == R.id.device_sessions_logout) {
            Log.d(TAG, "Device Session Logout initiated");
            loadLogoutFragment( userId );
        } else if (id == R.id.app_logout) {
            Log.d(TAG, "App Logout initiated");
            relaunchApp();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void relaunchApp() {
        App app = ((App) getApplicationContext());
        //Clear local login user details;
        app.setLoggedUser(null);

        Intent i = new Intent(app, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        app.startActivity(i);
    }

    private void loadLogoutFragment(String userId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LogoutFragment logoutFragment = LogoutFragment.newInstance(userId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, logoutFragment, "device_logout");
        transaction.addToBackStack("device_logout");
        transaction.commit();
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
        UserDetailsFragment userDetailsFragment = UserDetailsFragment.newInstance(tokenId, userId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, userDetailsFragment, "user_details");
        transaction.addToBackStack("users_list");
        transaction.commit();
    }

    private void loadSetUpUserFragment(String tokenId, String userId, String passphrasePrefix) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SetUpUserFragment userSetupFragment = SetUpUserFragment.newInstance(tokenId, userId, passphrasePrefix);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, userSetupFragment, "user_setup");
        transaction.addToBackStack("user_setup");
        transaction.commit();
    }

    private void loadPaperWalletFragment(String tokenId, String userId, boolean loadForAuthorize) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaperWalletFragment paperWalletFragment = PaperWalletFragment.newInstance(tokenId, userId, loadForAuthorize);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, paperWalletFragment, "paper_wallet");
        transaction.addToBackStack("paper_wallet");
        transaction.commit();
    }

    private void loadResetPinFragment(String tokenId, String userId, byte[] appSalt) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ResetPinFragment resetPinFragment = ResetPinFragment.newInstance(tokenId, userId, appSalt);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, resetPinFragment, "reset_pin");
        transaction.addToBackStack("reset_pin");
        transaction.commit();
    }

    private void loadCreateSessionFragment(String tokenId, String userId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CreateSessionFragment createSessionFragment = CreateSessionFragment.newInstance(tokenId, userId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, createSessionFragment, "create_session");
        transaction.addToBackStack("create_session");
        transaction.commit();
    }

    private void loadQRPerformFragment(String tokenId, String userId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        qrPerformFragment = QRPerformFragment.newInstance(tokenId, userId);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, qrPerformFragment, "qr_perform");
        transaction.addToBackStack("qr_perform");
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

        // set dialog message
        alertDialogBuilder
                .setCancelable(false);

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final Button cancelButton = promptsView.findViewById(R.id.buttonCancel);

        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            callback.onCancel();
        });
        final Button doneButton = promptsView.findViewById(R.id.buttonDone);

        doneButton.setOnClickListener(v -> {
            String pin = userInput.getText().toString();
            if (pin.length() < 6) {
                Log.w(TAG, "Pin length to small");
                getPinDialog(callback, "Pin size less than 6 char enter again:");
            } else {
                alertDialog.dismiss();
                callback.onSubmit(pin);
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
                    OstSdk.performQRAction(userId, returnedResult, qrPerformFragment);
                    qrPerformFragment.flowStarted();
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSONException while parsing");
            }
        }
    }

    @Override
    public void onItemSelected(String tokenHolderAddress) {
        loadRuleTransactionFragment(tokenHolderAddress);
    }

    private void loadRuleTransactionFragment(String tokenHolderAddress) {
        LogInUser logInUser = ((App) getApplication()).getLoggedUser();
        String currentUserId = logInUser.getOstUserId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        RuleTransactionFragment ruleTransactionFragment = RuleTransactionFragment.newInstance(currentUserId, tokenHolderAddress);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, ruleTransactionFragment, "rule_transaction_fragment");
        transaction.addToBackStack("rule_transaction_fragment");
        transaction.commit();
    }


    public interface DialogCallback {
        void onSubmit(String pin);

        void onCancel();
    }

    @Override
    public void onBack() {
        super.onBackPressed();
    }
}