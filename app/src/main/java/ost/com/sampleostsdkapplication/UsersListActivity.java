package ost.com.sampleostsdkapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.CommonUtils;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import ost.com.sampleostsdkapplication.fragments.PaperWalletFragment;
import ost.com.sampleostsdkapplication.fragments.SetUpUserFragment;
import ost.com.sampleostsdkapplication.fragments.UserDetailsFragment;
import ost.com.sampleostsdkapplication.fragments.UserListFragment;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UsersListActivity extends MappyBaseActivity implements
        SetUpUserFragment.OnSetUpUserFragmentListener,
        PaperWalletFragment.OnPaperWalletFragmentListener {

    private static final String TAG = "UsersListActivity";
    private static final int QR_REQUEST_CODE = 2;
    private int PICK_IMAGE_REQUEST = 1;
    private UserDetailsFragment userDetailsFragment;
    private SetUpUserFragment userSetupFragment;
    private PaperWalletFragment paperWalletFragment;

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

        if (id == R.id.user_detail){
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
            getApplicationContext().startActivity(anotherIntent);
            //Need to be on click of button "start polling"
            OstSdk.startPolling(userId, userId, OstSdk.USER, OstUser.CONST_STATUS.ACTIVATING,
                    OstUser.CONST_STATUS.ACTIVATED, new WorkFlowHelper(getApplicationContext()));
            //startPollingInterface.startPolling();
        } else if (id == R.id.scan_qr) {
            Intent intent = new Intent(getApplicationContext(), SimpleScannerActivity.class);
            startActivityForResult(intent, QR_REQUEST_CODE);
        } else if (id == R.id.add_session) {
            OstSdk.addSession(userId, "1000000000",
                    System.currentTimeMillis() / 1000, new WorkFlowHelper(getApplicationContext()) {
                        @Override
                        public void getPin(String userId, OstPinAcceptInterface ostPinAcceptInterface) {
                            super.getPin(userId, ostPinAcceptInterface);
                            getPinDialog(new DialogCallback() {
                                @Override
                                public void onSubmit(String pin) {
                                    ostPinAcceptInterface.pinEntered(pin, logInUser.getPassword());
                                }

                                @Override
                                public void onCancel() {
                                    ostPinAcceptInterface.cancelFlow(new OstError("Don't know pin"));
                                }
                            });
                        }
                    });
        } else if (id == R.id.show_paper_wallet) {
            Log.d(TAG, "Show Paper Wallet Clicked");
            loadPaperWalletFragment(logInUser.getTokenId(), userId, false);
        } else if (id == R.id.device_words) {
            Log.d(TAG, "Add Device using mMnemonicsList Clicked");
            loadPaperWalletFragment(logInUser.getTokenId(), userId, true);
        } else if (id == R.id.transactions) {
            Log.d(TAG, "Execute Transaction Clicked");
            String tokenHolderAddress = "0x30fa423c14625bb0bac6852d7b68f9d326ac1242";
            String amount = "5";
            String transactionType = "Direct Transfer";
            String tokenId = logInUser.getTokenId();
            OstSdk.executeTransaction(userId, tokenId, Arrays.asList(tokenHolderAddress), Arrays.asList(amount), transactionType, new WorkFlowHelper(getApplicationContext()) {
            });
        } else if (id == R.id.reset_pin) {
            Log.d(TAG, "Reset pin");
            String currentPin = "123456";
            String appSalt = logInUser.getPassword();
            getPinDialog(new DialogCallback() {
                @Override
                public void onSubmit(String pin) {
                    OstSdk.resetPin(userId, appSalt, currentPin, pin, new WorkFlowHelper(getApplicationContext()) {
                    });
                }

                @Override
                public void onCancel() {
                    // Dialog cancelled;
                }
            });
        } else if (id == R.id.device_recovery) {
            Log.d(TAG, "Device Recovery");
            String currentPin = "123456";
            String appSalt = logInUser.getPassword();
            String address = "0x30fa423c14625bb0bac6852d7b68f9d326ac1242";
            OstSdk.initiateRecoverDevice(userId, appSalt, currentPin, address, new WorkFlowHelper(getApplicationContext()) {
            });
        }
        return super.onOptionsItemSelected(item);
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

//    private void showQRFragment(Bitmap qrImage) {
//        Context context = UsersListActivity.this;
//        FragmentManager fragmentManager = ((UsersListActivity) context).getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        android.support.v4.app.Fragment fragment = new QRFragment();
//        ((QRFragment) fragment).setQRImage(qrImage);
//        transaction.add(R.id.user_recycler_view, fragment).commit();
//    }

    private void getPinDialog(final DialogCallback callback, String message) {
        LayoutInflater li = LayoutInflater.from(UsersListActivity.this);
        View promptsView = li.inflate(R.layout.pin_prompt, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UsersListActivity.this);
        alertDialogBuilder.setView(promptsView);

        final TextView label = promptsView
                .findViewById(R.id.textView);

        label.setText(message);
        final EditText userInput = promptsView
                .findViewById(R.id.editTextDialogUserInput);

        boolean errorFlag = false;
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                callback.onCancel();
                            }
                        })
                .setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("SetTextI18n")
                            public void onClick(DialogInterface dialog, int id) {
                                String pin = userInput.getText().toString();
                                if (pin.length() < 6) {
                                    Log.w(TAG, "Pin length to small");
                                    getPinDialog(callback, "Pin size less than 6 char enter again:");
                                } else {
                                    dialog.dismiss();
                                    callback.onSubmit(pin);
                                }
                            }
                        }
                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void getPinDialog(final DialogCallback callback) {
        getPinDialog(callback, "Enter Pin : ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d(TAG, String.valueOf(bitmap));
            } catch (Exception e) {
                Log.e(TAG, "IOException in on Activity result");
            }
        }
        if (requestCode == QR_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String userId = ((App) getApplicationContext()).getLoggedUser().getOstUserId();
            String returnedResult = data.getData().toString();
            try {
                OstSdk.ostPerform(userId, returnedResult, new WorkFlowHelper(getApplicationContext()));
            } catch (JSONException e) {
                Log.e(TAG, "JSONException while parsing");
            }
        }
    }

    interface DialogCallback {
        void onSubmit(String pin);

        void onCancel();
    }

    @Override
    public void onBack(){
        super.onBackPressed();
    }

    @Override
    public void onSetupUserSubmit(String pin){
        Log.d(TAG,"Start user activation process");
        LogInUser logInUser = ((App) getApplication()).getLoggedUser();
        String userId = logInUser.getOstUserId();
        String password = logInUser.getPassword();
        long expiresAfterInSecs = 2 * 7 * 24 * 60 * 60; //2 weeks
        String spendingLimit = "1000000000000";
        OstSdk.activateUser(userId, pin, password, expiresAfterInSecs, spendingLimit,
                new WorkFlowHelper(getApplicationContext()));
    }

    @Override
    public void onShowPaperWalletButton(){
        Log.d(TAG,"Ask for pin");
        LogInUser logInUser = ((App) getApplication()).getLoggedUser();
        OstSdk.getPaperWallet(logInUser.getOstUserId(), new WorkFlowHelper(getApplicationContext()) {
            @Override
            public void getPin(String userId, OstPinAcceptInterface ostPinAcceptInterface) {
                super.getPin(userId, ostPinAcceptInterface);
                getPinDialog(new DialogCallback() {
                    @Override
                    public void onSubmit(String pin) {
                        ostPinAcceptInterface.pinEntered(pin, logInUser.getPassword());
                    }

                    @Override
                    public void onCancel() {
                        ostPinAcceptInterface.cancelFlow(new OstError("Don't know pin"));
                    }
                });
            }

            @Override
            public void showPaperWallet(byte[] mnemonics) {
                super.showPaperWallet(mnemonics);
                Log.d(TAG, "Paper wallet " + Arrays.asList(mnemonics));
                paperWalletFetchingDone(new String(mnemonics), "Please Save these words carefully.");
                CommonUtils.clearBytes(mnemonics);
            }
            @Override
            public void invalidPin(String userId, OstPinAcceptInterface ostPinAcceptInterface) {
                Log.d(TAG, "Invalid Pin");
                paperWalletFetchingDone(null, "Invalid Pin.");
            }
        });
    }

    @Override
    public void paperWalletFetchingDone(String mnemonics, String showText) {
        if(paperWalletFragment != null){
            paperWalletFragment.showWalletWords(mnemonics, showText);
        }
    }

    @Override
    public void authorizeDeviceUsingMnemonics(String mnemonics, String userId){
        OstSdk.addDeviceUsingMnemonics(userId, mnemonics.getBytes(UTF_8), new WorkFlowHelper(getApplicationContext()));
    }

//    public static class QRFragment extends android.support.v4.app.Fragment {
//
//        private ImageView mImageView;
//        private Button mBtnDone;
//        private Bitmap mBitMap;
//        private Activity mActivity;
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View v = inflater.inflate(R.layout.activity_qr_view, container, false);
//            mImageView = v.findViewById(R.id.imageDisplay);
//            mBtnDone = v.findViewById(R.id.btnDone);
//            mActivity = getActivity();
//            mBtnDone.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //OnClick start polling
//                }
//            });
//            return v;
//        }
//
//        void setQRImage(Bitmap bitmap) {
//            mBitMap = bitmap;
//        }
//
//        @Override
//        public void onResume() {
//            super.onResume();
//            mImageView.setImageBitmap(mBitMap);
//        }
//    }
}