package customloader.src;

import com.ost.walletsdk.ui.loader.OstLoaderDelegate;
import com.ost.walletsdk.ui.loader.OstLoaderFragment;
import com.ost.walletsdk.workflows.OstWorkflowContext;

public class OstMockLoaderManager implements OstLoaderDelegate {

    static OstMockLoaderManager INSTANCE = new OstMockLoaderManager();
    private OstMockLoaderManager(){

    }

    public static OstLoaderDelegate getInstance() {
        return INSTANCE;
    }

    @Override
    public OstLoaderFragment getLoader(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return customloader.src.OstMockLoaderFragment.newInstance();
    }

    @Override
    public boolean waitForFinalization(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return true;
    }
}