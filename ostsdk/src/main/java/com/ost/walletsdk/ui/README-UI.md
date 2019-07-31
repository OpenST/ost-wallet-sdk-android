# OST Wallet UI Android

## Introduction

Wallet UI SDK is useful to integrate OstWalletSdk in application with available UI components.

## Setup

To setup OstWalletUI, please refer [setup](https://github.com/ostdotcom/ost-wallet-sdk-android#setup).

## OstWalletUI SDK APIs
To use OstWalletUI `import com.ost.walletsdk.ui.*;`

### Set Theme Config

Theme for OstWalletUI can be initialized by calling `setThemeConfig` API, which setup OstWalletUI theme config

**Parameters**<br/>
&nbsp;_themeConfig: Config to use for UI_<br/>

theme-config.json 
```json
{
  "nav_bar_logo_image": {
    "asset_name": "nav_bar_logo"
  }
}
```
Change "nav_bar_logo" with your drawable file name in resource folder, which you want to show in navigation bar.
```java
JSONObject themeConfig = readFromFile("theme-config.json");
OstWalletUI.setThemeConfig(themeConfig)
```

### Set Content Config

Content for OstWalletUI can be initialized by calling `setContentConfig` API, which  setup OstWalletUI content config

**Parameters**<br/>
&nbsp;contentConfig: Config to use for UI_<br/>

content-config.json
```json
{
  "activate_user": {
    "create_pin": {
      "terms_and_condition_url": "https://ost.com/terms"
    },
    "confirm_pin": {
      "terms_and_condition_url": "https://ost.com/terms"
    }
  }
}
```
While activating user  `create_pin["terms_and_condition_url"]` url is used to show terms and conditions. Where as while confirming pin `terms_and_condition_url["terms_and_condition_url"]` url is used.
```java
JSONObject contentConfig = readFromFile("content-config.json");
OstWalletUI.setThemeConfig(contentConfig)
```

### Activate User

User activation refers to the deployment of smart-contracts that form the user's Brand Token wallet. An activated user can engage with a Brand Token economy.<br/><br/>
**Parameters**<br/>
&nbsp;_currentActivity: Current Activity instance on which Sdk UI activity to be launched_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_expireAfterInSec: Session key validat duration_<br/>
&nbsp;_spendingLimit: Spending limit in a transaction in atto BT_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.activateUser(@NonNull Activity currentActivity, String userId, long expiredAfterSecs,
                                                String spendingLimit, OstUserPassphraseCallback userPassphraseCallback)
```

### Initialize Recovery

A user can control their Brand Tokens using their authorized devices. If they lose their authorized device, they can recover access to their BrandTokens by authorizing a new device via the recovery process .<br/><br/>
**Parameters**<br/>
&nbsp;_currentActivity: Current Activity instance on which Sdk UI activity to be launched_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_recoverDeviceAddress: Device address which wants to recover_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

If application set `recoverDeviceAddress` then OstWalletUI ask for `pin` to initiate device recovery. Else it displays authorized device list for given `userId` to select device from. 

```java
OstWalletUI.initiateDeviceRecovery(@NonNull Activity currentActivity, String userId,
                                                    @Nullable String recoverDeviceAddress, OstUserPassphraseCallback userPassphraseCallback)
```

### Abort Device Recovery

To abort initiated device recovery.<br/><br/>
**Parameters**<br/>
&nbsp;_currentActivity: Current Activity instance on which Sdk UI activity to be launched_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
abortDeviceRecovery(@NonNull Activity currentActivity, String userId,
                                                       OstUserPassphraseCallback userPassphraseCallback)
```


### Subscribe 

Subscribe to specified event of UI Workflow
**Parameters**<br/>
&nbsp;_workflowId: Id of the workflow as returned by methods of OstWalletUI_<br/>
&nbsp;_listner: Callback implementation object to listen events_<br/>

```java
SdkInteract.getInstance().subscribe(String workflowId, SdkInteractListener listener)
```

### Unsubscribe

Unsubscribes the listner from the specified event of UI Workflow.
**Parameters**<br/>
&nbsp;_workflowId: Id of the workflow as returned by methods of OstWalletUI_<br/>
&nbsp;_listner: Callback implementation object to remove from listing events_<br/>

```java
SdkInteract.getInstance().unSubscribe(String workflowId, SdkInteractListener listener)
```

## Workflow Callbacks

### OstUserPassphraseCallback

```java
/** Get passphrase prefix from application
  *
  *   - Parameters:
  *   - userId: Ost user id
  *   - ostWorkflowContext: Workflow context
  *   - ostPassphraseAcceptor: Passphrase prefix accept callback
  */
void getPassphrase(String userId, OstWorkflowContext ostWorkflowContext, OstPassphraseAcceptor ostPassphraseAcceptor)
```

### SdkInteractListener

```java
/** Acknowledge user about the request which is going to make by SDK.
  *
  * - Parameters:
  *   - workflowId: Workflow id
  *   - ostWorkflowContext: A context that describes the workflow for which the callback was triggered.
  *   - ostContextEntity: Context Entity
  */
void requestAcknowledged(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
```

```java
/** Inform SDK user the the flow is complete.
  *
  * - Parameters:
  *   - workflowId: Workflow id
  *   - ostWorkflowContext: A context that describes the workflow for which the callback was triggered.
  *   - ostContextEntity: Context Entity
  */
void flowComplete(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
```

```java
 /** Inform SDK user that flow is interrupted with errorCode.
  * Developers should dismiss pin dialog (if open) on this callback.
  *
  * - Parameters:
  *   - workflowId: Workflow id
  *   - workflowContext: A context that describes the workflow for which the callback was triggered.
  *   - ostError: Error Entity
  */
void flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError);
```
