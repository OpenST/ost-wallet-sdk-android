# OstWalletUI Custom Loader

Developer can set application loader instead of OstWalletSdk default loader while using OstWalletUI

## Setup
### Set Loader Manager
#### Creade Loader Manager Object
```java
import com.ost.walletsdk.ui.loader.OstLoaderDelegate;

class LoaderManager implements OstLoaderDelegate {

    private LoaderManager(){

    }

    @Override
    public OstLoaderFragment getLoader(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
    	//Returns Custom Loader Implemtation which inherits OstLoaderFragment.
    }

    @Override
    public boolean waitForFinalization(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
       // Returns boolean flag, which determine whether loader should be shown till workflow completion.
    }
}
```

#### Set Loader Manager Object
Loader manager object could be the plan `java` class. OstWalletUI holds reference of it.
Caution:
```java
OstWalletUI.setLoaderManager(new LoaderManager());
```
> **Caution**<br/>
>Implementing OstLoaderDelegate to Activities or fragment may cause memory leak.<br/>

#### Create Application loader Fragment
Loader Fragment should be subclass of  `OstLoaderFragment`.

* onInitLoader: method gets called when OstWalletUI is processing
* onPostAuthentication: OstWalletUI call this method after user enters pin
* onAcknowledge: method gets called after request is submitted for confirmation
* onSuccess: This method gets called after workflow confirmation
* onFailure: After failure of workflow, sdk calls onFailure

>**Note**<br/>
>Developer should call `dismissWorkflow` of `OstLoaderCompletionDelegate` to close Loader UI.<br/>
>Not calling delegate `dismissWorkflow` will keep the workflow Acitivty on the screen.

```java
import com.ost.walletsdk.ui.loader.OstLoaderFragment;
import com.ost.walletsdk.ui.loader.OstWorkflowLoader;
import com.ost.walletsdk.ui.workflow.OstLoaderCompletionDelegate;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;


public class AppLoaderFragment extends OstLoaderFragment implements OstWorkflowLoader {

    public static AppLoaderFragment newInstance() {
        return new AppLoaderFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_app_loader, container, false);
        return mViewGroup;
    }

    public void setLoaderString(String loaderString) {
        StringConfig stringConfig = StringConfig.instance(contentConfig.optJSONObject("initial_loader"));
        mLoaderTextView.setText(stringConfig.getString());
        //Method gets called from SDK to set String for the loader
    }

    @Override
    public void onInitLoader(JSONObject contentConfig) {
        //Method gets called from SDK after workflow launch
        StringConfig stringConfig = StringConfig.instance(contentConfig.optJSONObject("loader"));
        mLoaderTextView.setText(stringConfig.getString());
    }

    @Override
    public void onPostAuthentication(JSONObject contentConfig) {
        //Method gets called from SDK after authentication through pin or biometric
        StringConfig stringConfig = StringConfig.instance(contentConfig.optJSONObject("acknowledge"));
        mLoaderTextView.setText(stringConfig.getString());
    }

    @Override
    public void onAcknowledge(JSONObject contentConfig) {
        //Method gets called from SDK after request is submitted to the Ost platform successfully
        StringConfig stringConfig = StringConfig.instance(contentConfig.optJSONObject("acknowledge"));
        mLoaderTextView.setText(stringConfig.getString());
    }

    @Override
    public void onSuccess(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, JSONObject contentConfig ,final OstLoaderCompletionDelegate delegate) {
        //Method get called when the worflow has completed successfully, show success dialog here.
        //To close workflow, call delegate method dismissWorkflow
        delegate.dismissWorkflow();
    }

    @Override
    public void onFailure(OstWorkflowContext ostWorkflowContext, OstError ostError, JSONObject contentConfig, final OstLoaderCompletionDelegate delegate) {
        //Method get called when the worflow has failed, show failure dialog here.
        //To close workflow, call delegate method dismissWorkflow
        delegate.dismissWorkflow();
    }
}
```