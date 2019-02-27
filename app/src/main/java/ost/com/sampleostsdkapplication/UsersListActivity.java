package ost.com.sampleostsdkapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersListActivity extends MappyBaseActivity {

    private static final String TAG = "UsersListActivity";
    private static final int QR_REQUEST_CODE = 2;
    private int PICK_IMAGE_REQUEST = 1;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private UserAdapter mAdapter;
    private List<UserData> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.user_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new UserAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
        new MappyApiClient().getUserList(new MappyApiClient.Callback() {
            @Override
            public void onResponse(boolean success, JSONObject response) {
                mDataList.clear();
                if (success) {
                    try {
                        JSONArray array = response.optJSONArray("users");
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject jsonObject = array.getJSONObject(i);
                            mDataList.add(UserData.parse(jsonObject));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mDataList.add(new UserData("", "Network Error", "", ""));
                }
                mAdapter.notifyDataSetChanged();
            }
        });
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

        if (id == R.id.activate_user) {
            Log.d(TAG, "Activate User Clicked");
            String password = logInUser.getPassword();
            long expiresAfterInSecs = 2 * 7 * 24 * 60 * 60; //2 weeks
            String spendingLimit = "1000000000000";
            getPinDialog(new DialogCallback() {
                @Override
                public void onSubmit(String pin) {
                    OstSdk.activateUser(userId, pin, password, expiresAfterInSecs, spendingLimit, new WorkFlowHelper(getApplicationContext()));
                }

                @Override
                public void onCancel() {
                    // Dialog cancelled;
                }
            });
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
            OstSdk.getPaperWallet(userId, new WorkFlowHelper(getApplicationContext()) {
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
                public void showPaperWallet(String[] mnemonicsArray) {
                    super.showPaperWallet(mnemonicsArray);
                    Log.d(TAG, "Paper wallet " + Arrays.asList(mnemonicsArray));
                }
            });
        } else if (id == R.id.device_words) {
            Log.d(TAG, "Add device clicked");
            List<String> mMnemonicsList = Arrays.asList("satisfy", "fish", "surround", "foster", "funny", "sword", "wisdom", "forward", "father", "pull", "lens", "joy");
            String mMnemonics = "satisfy fish surround foster funny sword wisdom forward father pull lens joy";
            OstSdk.addDeviceUsingMnemonics(userId, mMnemonics, new WorkFlowHelper(getApplicationContext()));
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
            String newPin = "123458";
            String appSalt = logInUser.getPassword();
            OstSdk.resetPin(userId, appSalt, currentPin, newPin, new WorkFlowHelper(getApplicationContext()) {
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void showQRFragment(Bitmap qrImage) {
        Context context = UsersListActivity.this;
        FragmentManager fragmentManager = ((UsersListActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        android.support.v4.app.Fragment fragment = new QRFragment();
        ((QRFragment) fragment).setQRImage(qrImage);
        transaction.add(R.id.user_recycler_view, fragment).commit();
    }

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

    public static class QRFragment extends android.support.v4.app.Fragment {

        private ImageView mImageView;
        private Button mBtnDone;
        private Bitmap mBitMap;
        private Activity mActivity;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_qr_view, container, false);
            mImageView = v.findViewById(R.id.imageDisplay);
            mBtnDone = v.findViewById(R.id.btnDone);
            mActivity = getActivity();
            mBtnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //OnClick start polling
                }
            });
            return v;
        }

        void setQRImage(Bitmap bitmap) {
            mBitMap = bitmap;
        }

        @Override
        public void onResume() {
            super.onResume();
            mImageView.setImageBitmap(mBitMap);
        }
    }
}