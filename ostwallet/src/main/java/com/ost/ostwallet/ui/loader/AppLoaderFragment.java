package com.ost.ostwallet.ui.loader;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ost.ostwallet.R;
import com.ost.walletsdk.ui.loader.LoaderFragment;
import com.ost.walletsdk.ui.loader.OstWorkflowLoader;
import com.ost.walletsdk.ui.workflow.WorkflowCompleteDelegate;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;


public class AppLoaderFragment extends LoaderFragment implements OstWorkflowLoader {
    private ProgressBar mProgressBar;
    private boolean mViewActive;
    private String mLoaderString = "Loading...";
    private TextView mLoaderTextView;

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
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_app_loader, container, false);
        mViewActive = true;
        mProgressBar = viewGroup.findViewById(R.id.progressBar);
        mLoaderTextView = viewGroup.findViewById(R.id.loaderText);
        mLoaderTextView.setText(mLoaderString);
        return viewGroup;
    }

    public void setLoaderString(String loaderString) {
        mLoaderString = loaderString;
    }

    @Override
    public void onInitLoader() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (mViewActive)  {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mLoaderTextView.setVisibility(View.VISIBLE);
                    mProgressBar.animate();
                }
            }
        });
    }

    @Override
    public void onPostAuthentication() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (mViewActive)  {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mLoaderTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onAcknowledge() {

    }

    @Override
    public void onSuccess(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, final WorkflowCompleteDelegate delegate) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (mViewActive)  {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mLoaderTextView.setVisibility(View.VISIBLE);
                    mLoaderTextView.setText("Success");
                    mLoaderTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delegate.dismissWorkflow();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onFailure(OstWorkflowContext ostWorkflowContext, OstError ostError, final WorkflowCompleteDelegate delegate) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (mViewActive)  {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mLoaderTextView.setVisibility(View.VISIBLE);
                    mLoaderTextView.setText("Failed");
                    mLoaderTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delegate.dismissWorkflow();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewActive = false;
    }
}