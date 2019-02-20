package ost.com.sampleostsdkapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ost.mobilesdk.OstSdk;

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
            String uPin = "123456";
            long expiresAfterInSecs = 2 * 7 * 24 * 60 * 60; //2 weeks
            String spendingLimit = "1000000000000";
            OstSdk.activateUser(userId, uPin, password, expiresAfterInSecs, spendingLimit, new WorkFlowHelper(getApplicationContext()));
        } else if (id == R.id.add_device) {
            Log.d(TAG, "Add device clicked");
            LogInUser logInUser = ((App) getApplication()).getLoggedUser();

            String userId = logInUser.getOstUserId();
            OstSdk.addDevice(userId, new WorkFlowHelper(getApplicationContext()));
        } else if (id == R.id.scan_qr) {
            Intent intent = new Intent(getApplicationContext(), SimpleScannerActivity.class);
            startActivityForResult(intent, QR_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
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
            String userId = ((App)getApplicationContext()).getLoggedUser().getOstUserId();
            String returnedResult = data.getData().toString();
            try {
                OstSdk.scanQRCode(userId, returnedResult, new WorkFlowHelper(getApplicationContext()));
            } catch (JSONException e) {
                Log.e(TAG,"JSONException while parsing");
            }
        }
    }
}