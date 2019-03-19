package ost.com.sampleostsdkapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.sampleostsdkapplication.App;
import ost.com.sampleostsdkapplication.MainActivity;
import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the Logout sessions screen for OstDemoApp.
 */
public class LogoutFragment extends BaseFragment {

    private static final String TAG = "LogoutFragment";
    private String mUserId;

    public String getPageTitle() {
        return getResources().getString(R.string.logout_all_sessions);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = super.onCreateView(inflater, container, savedInstanceState);

        //No need of cancel and next button for logout fragment
        getNextButton().setVisibility(View.GONE);
        getCancelButton().setVisibility(View.GONE);

        return parentView;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LogoutFragment.
     */
    public static LogoutFragment newInstance(String userId) {
        LogoutFragment fragment = new LogoutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mUserId = userId;
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        OstSdk.logoutAllSessions(mUserId, this);
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        //Clear local login user details;
        super.flowComplete(ostWorkflowContext, ostContextEntity);
        relaunchApp();
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        super.flowInterrupt(ostWorkflowContext, ostError);
        relaunchApp();
    }

    void relaunchApp() {
        if (null == getActivity()) {
            Log.e(TAG, "Get activity is null");
        }
        App app = ((App) getActivity().getApplicationContext());
        app.setLoggedUser(null);

        Intent i = new Intent(app, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        app.startActivity(i);
    }
}