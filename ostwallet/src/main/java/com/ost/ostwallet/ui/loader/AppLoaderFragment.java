package com.ost.ostwallet.ui.loader;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ost.ostwallet.R;
import com.ost.walletsdk.ui.loader.LoaderFragment;
import com.ost.walletsdk.ui.loader.OstWorkflowLoader;
import com.ost.walletsdk.ui.workflow.OstLoaderCompletionDelegate;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;


public class AppLoaderFragment extends LoaderFragment implements OstWorkflowLoader {
    private HeartBeatView heartBeatView;
    private boolean mViewActive;
    private String mLoaderString = "Loading...";
    private TextView mLoaderTextView;
    private AppProgress mProgressHorizontal;
    private ViewGroup mViewGroup;
    private Button mStatusButton;
    private View mStatusImageView;

    public static AppLoaderFragment newInstance() {
        return new AppLoaderFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, com.ost.walletsdk.R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_app_loader, container, false);
        mViewActive = true;
        heartBeatView = mViewGroup.findViewById(R.id.progressBar);
        mLoaderTextView = mViewGroup.findViewById(R.id.loaderText);
        mProgressHorizontal = mViewGroup.findViewById(R.id.progressBarIndef);

        mStatusButton = mViewGroup.findViewById(R.id.statusButton);
        mStatusImageView = mViewGroup.findViewById(R.id.statusImageView);

        mLoaderTextView.setText(mLoaderString);
        return mViewGroup;
    }

    public void setLoaderString(String loaderString) {
        mLoaderString = loaderString;
    }

    @Override
    public void onInitLoader(JSONObject contentConfig) {
        if (mViewActive) {
            hideStatus();

            showLoader();
        }
    }

    @Override
    public void onPostAuthentication(JSONObject contentConfig) {
        if (mViewActive) {
            hideStatus();

            showLoader();
        }
    }

    @Override
    public void onAcknowledge(JSONObject contentConfig) {
        mLoaderTextView.setText("Request Acknowledged");
    }

    @Override
    public void onSuccess(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, final OstLoaderCompletionDelegate delegate) {
        if (mViewActive) {
            hideLoader();

            showSuccessStatus(ostWorkflowContext, ostContextEntity);

            mViewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delegate.dismissWorkflow();
                }
            });
        }
    }

    @Override
    public void onFailure(OstWorkflowContext ostWorkflowContext, OstError ostError, final OstLoaderCompletionDelegate delegate) {
        if (mViewActive) {

            hideLoader();

            showFailureStatus(ostWorkflowContext, ostError);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delegate.dismissWorkflow();
                }
            };
            mViewGroup.setOnClickListener(listener);
            mStatusButton.setOnClickListener(listener);
        }
    }

    private void hideStatus() {
        mStatusButton.setVisibility(View.GONE);
        mStatusImageView.setVisibility(View.GONE);
    }

    private void showSuccessStatus(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        mStatusImageView.setVisibility(View.VISIBLE);
        mStatusButton.setVisibility(View.GONE);
        mLoaderTextView.setVisibility(View.VISIBLE);
        mLoaderTextView.setText("Success");

        mStatusImageView.setBackground(getResources().getDrawable(R.drawable.toast_success, null));
    }

    private void showFailureStatus(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        mStatusImageView.setVisibility(View.VISIBLE);
        mStatusButton.setVisibility(View.VISIBLE);
        mLoaderTextView.setVisibility(View.VISIBLE);

        mLoaderTextView.setText(new OstSdkErrors().getErrorMessage(ostWorkflowContext, ostError));
        mStatusImageView.setBackground(getResources().getDrawable(R.drawable.toast_error, null));
        mStatusButton.setText("Failure");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewActive = false;
    }

    private void showLoader() {
        heartBeatView.setVisibility(View.VISIBLE);
        mLoaderTextView.setVisibility(View.VISIBLE);
        heartBeatView.start();
        mProgressHorizontal.start();
    }

    private void hideLoader() {
        heartBeatView.setVisibility(View.GONE);
        heartBeatView.stop();
        mProgressHorizontal.setVisibility(View.GONE);
        mProgressHorizontal.stop();
    }
}