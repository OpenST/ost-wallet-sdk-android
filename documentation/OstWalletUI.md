# OST Wallet UI iOS

## Introduction

Wallet UI SDK is useful to integrate OstWalletSdk in application with available UI components.

## Setup

To setup OstWalletUI, please refer [setup](../../README.md#setup).

## OstWalletUI SDK APIs
### Important Notes
1. App must [initialize](../../README.md#initialize-the-sdk) the sdk <em><b>before</b></em> initiating any UI workflows.
2. App must perform [setupDevice](../../README.md#set-up-the-device) workflow <em><b>before</b></em> initiating any UI workflows.


To use OstWalletUI 
```java
import com.ost.walletsdk.ui.OstWalletUI;
```

### Set Theme Config

Theme for OstWalletUI can be initialized by calling `setThemeConfig` API

**Parameters**<br/>
&nbsp;_config: Config to use for UI_<br/>

* Create config file by title `theme-config.json in assets directory`

```java
try {
     InputStream configInputStream = context.getAssets().open("theme-config.json");
     int size = configInputStream.available();
     byte[] buffer = new byte[size];

     configInputStream.read(buffer);
     configInputStream.close();

     String json = new String(buffer, "UTF-8");
     JSONObject themeConfig = new JSONObject(json);

     } catch (Exception e) {
        //Error handling
     }
```

```java
OstWalletUI.setThemeConfig(themeConfig)
```

### Set Content Config

Content for OstWalletUI can be initialized by calling `setContentConfig` API.

**Parameters**<br/>
&nbsp;_config: Config to use for UI_<br/>

* Create config file by title `content-config.json in assets directory`

```java
try {
     InputStream configInputStream = context.getAssets().open("content-config.json");
     int size = configInputStream.available();
     byte[] buffer = new byte[size];

     configInputStream.read(buffer);
     configInputStream.close();

     String json = new String(buffer, "UTF-8");
     JSONObject themeConfig = new JSONObject(json);

     } catch (Exception e) {
        //Error handling
     }
```

```java
OstWalletUI.setContentConfig(contentConfig)
```

### Activate User

User activation refers to the deployment of smart-contracts that form the user's Brand Token wallet. An activated user can engage with a Brand Token economy.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_expireAfterInSec: Session key valid duration_<br/>
&nbsp;_spendingLimit: Spending limit in a transaction in atto BT_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.activateUser(@NonNull Activity currentActivity,
                         String userId,
                         long expiredAfterSecs,
                         String spendingLimit,
                         OstUserPassphraseCallback userPassphraseCallback
                         ) -> String
```
### Authorize session

A session is a period of time during which a sessionKey is authorized to sign transactions under a pre-set limit on behalf of the user.
The device manager, which controls the tokens, authorizes sessions.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_expireAfterInSec: Session key validat duration_<br/>
&nbsp;_spendingLimit: Spending limit in a transaction in atto BT_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.createSession(@NonNull Activity currentActivity,
                          String userId,
                          long expireAfterInSec,
                          String spendingLimit,
                          OstUserPassphraseCallback userPassphraseCallback
                          ) -> String
```

### Get Mnemonic Phrase

The mnemonic phrase represents a human-readable way to authorize a new device. This phrase is 12 words long.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.getDeviceMnemonics(@NonNull Activity currentActivity,
                              String userId,
                              OstUserPassphraseCallback userPassphraseCallback
                              ) -> String
```

### Reset a User's PIN

The user's PIN is set when activating the user. This method supports re-setting a PIN and re-creating the recoveryOwner as part of that.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.resetPin(@NonNull Activity currentActivity,
                     String userId,
                     OstUserPassphraseCallback userPassphraseCallback
                     ) -> String
```

### Initialize Recovery

A user can control their Brand Tokens using their authorized devices. If they lose their authorized device, they can recover access to their BrandTokens by authorizing a new device via the recovery process .

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_recoverDeviceAddress: Device address which wants to recover_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

If application set `recoverDeviceAddress` then OstWalletUI ask for `pin` to initiate device recovery. Else it displays authorized device list for given `userId` to select device from. 

```java
OstWalletUI.initiateDeviceRecovery(@NonNull Activity currentActivity,
                                   String userId,
                                   @Nullable String recoverDeviceAddress,
                                   OstUserPassphraseCallback userPassphraseCallback
                                   ) -> String
```

### Abort Device Recovery

To abort initiated device recovery.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.abortDeviceRecovery(@NonNull Activity currentActivity,
                                String userId,
                                OstUserPassphraseCallback userPassphraseCallback
                                ) -> String
```

###  Revoke Device

To revoke device access.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_revokeDeviceAddress: Device address to revoke_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

If application set `revokeDeviceAddress` then OstWalletUI ask for `pin` to revoke device. Else it displays authorized device list for given `userId` to select device from.

```java
OstWalletUI.revokeDevice(@NonNull Activity currentActivity,
                         String userId,
                         @Nullable String revokeDeviceAddress,
                         OstUserPassphraseCallback userPassphraseCallback
                         ) -> String
```

###  Update Biometric Preference

This method can be used to enable or disable the biometric.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_enable: Preference to use biometric_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.updateBiometricPreference(@NonNull Activity currentActivity,
                                      String userId,
                                      boolean enable,
                                      OstUserPassphraseCallback userPassphraseCallback
                                      ) -> String
```

### Subscribe 

Subscribe to specified event of UI Workflow
**Parameters**<br/>
&nbsp;_workflowId: Id of the workflow as returned by methods of OstWalletUI_<br/>
&nbsp;_listner: Callback implementation object to listen events_<br/>

```java
OstWalletUI.subscribe(String workflowId,
                      OstWalletUIListener listener)
```

### Unsubscribe

Unsubscribes the listner from the specified event of UI Workflow.
**Parameters**<br/>
&nbsp;_workflowId: Id of the workflow as returned by methods of OstWalletUI_<br/>
&nbsp;_listner: Callback implementation object to remove from listing events_<br/>

```java
OstWalletUI.unsubscribe(String workflowId,
                      OstWalletUIListener listener)
```

### View Component Sheet

Component sheet is collection of all components present in OstWalletUI. Developers can verify how components are going to look with provied theme.
**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>

```java
OstWalletUI.showComponentSheet(@NonNull Activity currentActivity)
```

## Workflow Delegates

### OstUserPassphraseCallback

```java
/**
     * Get passphrase prefix from application
     * @param userId Ost user id
     * @param ostWorkflowContext Workflow context
     * @param ostPassphraseAcceptor Passphrase prefix accept callback
     */
void getPassphrase(String userId,
                   OstWorkflowContext ostWorkflowContext,
                   OstPassphraseAcceptor ostPassphraseAcceptor)
```

### OstWorkflowUIDelegate

```java
/**
     * Acknowledge user about the request which is going to make by SDK.
     * @param ostWorkflowContext A context that describes the workflow for which the callback was triggered with workflow id.
     * @param ostContextEntity Context Entity
     */
void requestAcknowledged(OstWorkflowContext ostWorkflowContext,
                         OstContextEntity ostContextEntity)
```

```java
 /**
     * Inform SDK user the the flow is complete.
     * @param ostWorkflowContext A context that describes the workflow for which the callback was triggered with workflow id.
     * @param ostContextEntity Context Entity
     */
void flowComplete(OstWorkflowContext ostWorkflowContext,
                  OstContextEntity ostContextEntity);
```

```java
/**
     * Inform SDK user that flow is interrupted with errorCode.
     * Developers should dismiss pin dialog (if open) on this callback.
     * @param ostWorkflowContext A context that describes the workflow for which the callback was triggered with workflow id.
     * @param ostError Error Entity
     */
void flowInterrupt(OstWorkflowContext ostWorkflowContext,
                   OstError ostError);
```
