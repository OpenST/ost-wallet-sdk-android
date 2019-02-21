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

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.user_recycler_view);

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

        if (id == R.id.activate_user) {
            Log.d(TAG, "Activate User Clicked");
            LogInUser logInUser = ((App) getApplication()).getLoggedUser();
            String userId = logInUser.getOstUserId();
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
            LogInUser logInUser = ((App) getApplication()).getLoggedUser();

            String userId = logInUser.getOstUserId();
            OstSdk.addDevice(userId, new WorkFlowHelper(getApplicationContext()));
//            OstSdk.addDevice(userId, new WorkFlowHelper(getApplicationContext()){
//                @Override
//                public void showQR(Bitmap qrImage, OstStartPollingInterface startPollingInterface) {
//                    Log.i(TAG, "showing QR code");
//                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//                    qrImage.compress(Bitmap.CompressFormat.PNG, 100, bStream);
//                    byte[] byteArray = bStream.toByteArray();
//                    showQRFragment(qrImage);
//                }
//            });
        } else if (id == R.id.scan_qr) {
            Intent intent = new Intent(getApplicationContext(), SimpleScannerActivity.class);
            startActivityForResult(intent, QR_REQUEST_CODE);
        } else if (id == R.id.add_session) {
            LogInUser logInUser = ((App) getApplication()).getLoggedUser();
            String userId = logInUser.getOstUserId();
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

        final TextView label = (TextView) promptsView
                .findViewById(R.id.textView);

        label.setText(message);
        final EditText userInput = (EditText) promptsView
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
                                    getPinDialog( callback,  "Pin size less than 6 char enter again:");
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
                OstSdk.scanQRCode(userId, returnedResult, new WorkFlowHelper(getApplicationContext()));
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