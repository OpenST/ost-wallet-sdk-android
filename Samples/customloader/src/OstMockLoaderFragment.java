package customloader.src;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.ost.ostwallet.R;
import com.ost.walletsdk.ui.loader.OstLoaderFragment;
import com.ost.walletsdk.ui.loader.OstWorkflowLoader;
import com.ost.walletsdk.ui.workflow.OstLoaderCompletionDelegate;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;


public class OstMockLoaderFragment extends OstLoaderFragment implements OstWorkflowLoader {
    private boolean mViewActive;
    private String mLoaderString = "Loading...";
    private TextView mLoaderTextView;
    private ViewGroup mViewGroup;
    private Button mStatusButton;
    private View mStatusImageView;
    private customloader.src.GIFView mProgressGif;

    public static OstMockLoaderFragment newInstance() {
        return new OstMockLoaderFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OstFullScreenDialogStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewGroup = (ViewGroup) inflater.inflate(R.layout.mock_loader_fragment, container, false);
        mViewActive = true;
        mLoaderTextView = mViewGroup.findViewById(R.id.loaderText);
        mProgressGif = mViewGroup.findViewById(R.id.progressGif);
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
        mLoaderTextView.setText("Waiting for Confirmation...");
    }

    @Override
    public void onSuccess(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, JSONObject contentConfig , final OstLoaderCompletionDelegate delegate) {
        if (mViewActive) {
            hideLoader();

            showSuccessStatus(ostWorkflowContext, ostContextEntity);

            final Runnable dismissWorkflow = new Runnable() {
                @Override
                public void run() {
                    if (null != delegate) delegate.dismissWorkflow();
                }
            };
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(dismissWorkflow,3000);

            mViewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delegate.dismissWorkflow();
                    handler.removeCallbacks(dismissWorkflow);
                }
            });
        }
    }

    @Override
    public void onFailure(OstWorkflowContext ostWorkflowContext, OstError ostError, JSONObject contentConfig, final OstLoaderCompletionDelegate delegate) {
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
        mLoaderTextView.setText(new customloader.src.OstSdkMessageHelper(getContext()).getSuccessText(ostWorkflowContext));
        mStatusImageView.setBackground(getResources().getDrawable(R.drawable.ost_success_icon, null));
    }

    private void showFailureStatus(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        mStatusImageView.setVisibility(View.VISIBLE);
        mStatusButton.setVisibility(View.VISIBLE);
        mLoaderTextView.setVisibility(View.VISIBLE);

        mLoaderTextView.setText(new customloader.src.OstSdkMessageHelper(getContext()).getErrorMessage(ostWorkflowContext, ostError));
        mStatusImageView.setBackground(getResources().getDrawable(R.drawable.ost_failure_icon, null));
        mStatusButton.setText("Failure");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewActive = false;
    }

    private void showLoader() {
        mLoaderTextView.setVisibility(View.VISIBLE);
        mProgressGif.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        mProgressGif.setVisibility(View.GONE);
    }
}