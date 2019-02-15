package ost.com.sampleostsdkapplication;

import android.os.Bundle;
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
                    mDataList.add(new UserData("","Network Error", "",""));
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

        if (id == R.id.user_status) {
            Log.i(TAG, "User satus clicked");
            LogInUser logInUser = ((App)getApplication()).getLoggedUser();
            String userId = logInUser.getOstUserId();
            String password = logInUser.getPassword();
            String uPin = "123456";
            String expirationHeight = "1000000";
            String spendingLimit = "1000000000000";
            OstSdk.activateUser(userId, uPin, password, expirationHeight, spendingLimit, new WorkFlowHelper(getApplicationContext()));
        }
        return super.onOptionsItemSelected(item);
    }
}