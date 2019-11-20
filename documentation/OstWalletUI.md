# OST Wallet UI Android

## Introduction

For quick and easy integration with SDK, developers can use built-in User Interface Components which are themeable and support content customization.

## Setup

To setup OstWalletUI, please refer [setup](../README.md#setup).

## OstWalletUI SDK APIs
### Important Notes
1. App must [initialize](../README.md#initialize-the-sdk) the sdk <em><b>before</b></em> initiating any UI workflows.
2. App must perform [setupDevice](../README.md#set-up-the-device) workflow <em><b>before</b></em> initiating any UI workflows.


To use OstWalletUI 
```java
import com.ost.walletsdk.ui.OstWalletUI;
```

### Set Theme Config

Theme for OstWalletUI can be initialized by calling `setThemeConfig` API.
To define custom theme config, please refer [ThemeConfig](./ThemeConfig.md) documentation.

**Parameters**<br/>
&nbsp;_config: Config to use for UI_<br/>

* Create config file by title `theme-config.json` in assets directory

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
To define custom content config, please refer [ContentConfig](./ContentConfig.md) documentation.

**Parameters**<br/>
&nbsp;_config: Config to use for UI_<br/>

* Create config file by title `content-config.json` in assets directory
For detailed explaination of how to build Content Config. [Ref](ContentConfig.md)

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

### Set Loader Manager

Application loader for OstWalletUI can be initialized by calling `setLoaderManager` API. <br/>
To setup application loader, please refer [CustomLoader](./OstCustomLoader.md) documentation.

**Parameters**<br/>
&nbsp;_loaderManager: class which inherits `OstLoaderFragment` protocol_<br/>
```java
OstWalletUI.setLoaderManager(loaderManager)
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

A user can control their Brand Tokens using their authorized devices.
If they lose their authorized device, they can recover access to their BrandTokens by authorizing a new device via the recovery process.
To use built-in device list UI, pass `recoverDeviceAddress` as `null`.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_recoverDeviceAddress: Device address which wants to recover. When null is passed, the user is asked to choose a device._<br/>
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

To revoke device access. To use built-in device list UI, pass `revokeDeviceAddress` as `null`.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_revokeDeviceAddress: Device address to revoke. When null is passed, the user is asked to choose a device._<br/>
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

### Authorize Current Device With Mnemonics

This workflow should be used to add a new device using 12 words recovery phrase.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.authorizeCurrentDeviceWithMnemonics(@NonNull Activity currentActivity,
                                      String userId,
                                      OstUserPassphraseCallback userPassphraseCallback
                                      ) -> String
```

### Get Add Device QR Code

This workflow shows QR Code to scan from another authorized device

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.getAddDeviceQRCode(@NonNull Activity currentActivity,
                                      String userId,
                                      ) -> String
```

### Scan QR Code To Authorize Device

This workflow can be used to authorize device by scanning QR Code.
> The device to be authorized must be a `REGISTERED` device and must be associated with the same user.
> To display the QR code on registered device, application can use `OstWalletUI.getAddDeviceQRCode` workflow.

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>
&nbsp;_userPassphraseCallback: Callback implementation object to get passphrase prefix from application_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.scanQRCodeToAuthorizeDevice(@NonNull Activity currentActivity,
                                      String userId,
                                      OstUserPassphraseCallback userPassphraseCallback
                                      ) -> String
```

### Scan QR Code To Execute Transaction

This workflow can be used to execute transaction by scanning transaction QR Code.

QR Code Sample:
```json
{
    "dd":"TX",
    "ddv":"1.1.0",
    "d":{
            "rn":"direct transfer",
            "ads":[
                "0x7701af46018fc57c443b63e839eb24872755a2f8",
                "0xed09dc167a72d939ecf3d3854ad0978fb13a8fe9"
            ],
            "ams":[
                "1000000000000000000",
                "1000000000000000000"
            ],
            "tid": 1140,
            "o":{
                    "cs":"USD",
                    "s": "$"
            }
        },
    "m":{
            "tn":"comment",
            "tt":"user_to_user",
            "td":"Thanks for comment"
        }
}
```

**Parameters**<br/>
&nbsp;_currentActivity: Context of current activity of the application from which workflow will initiate_<br/>
&nbsp;_userId: OST Platform user id provided by application server_<br/>

&nbsp;_Returns: Workflow Id(use to subscribe object to listen callbacks from perticular workflow id)_<br/>

```java
OstWalletUI.scanQRCodeToExecuteTransaction(@NonNull Activity currentActivity,
                                      String userId,
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

## UI Workflow Delegates

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

  /**
    * To get workflowId call workflowContext.getWorkflowId() method.
    * To identify the workflow type, use workflowContext.getWorkflowType() property.
    */
```

### OstWalletUIListener

This is a markup interface and does not define any methods. The the interfaces defined below are extended from this interface.


### Request Acknowledged Listener
Implement `RequestAcknowledgedListener` interface to get request acknowlege updates of UI workflow.

```java
   /**
     * Acknowledge user about the request which is going to make by SDK.
     * @param ostWorkflowContext A context that describes the workflow for which the callback was triggered with workflow id.
     * @param ostContextEntity Context Entity
     */
   void requestAcknowledged(OstWorkflowContext ostWorkflowContext,
                         OstContextEntity ostContextEntity)

  /**
    * To get workflowId call workflowContext.getWorkflowId() method.
    * To identify the workflow type, use workflowContext.getWorkflowType() property.
    */
```

### Flow Complete Listener
Implement `FlowCompleteListener` interface to get flow complete update of UI workflow

```java
   /**
     * Inform SDK user that the flow is complete.
     * @param ostWorkflowContext A context that describes the workflow for which the callback was triggered with workflow id.
     * @param ostContextEntity Context Entity
     */
   void flowComplete(OstWorkflowContext ostWorkflowContext,
                  OstContextEntity ostContextEntity);

  /**
    * To get workflowId call workflowContext.getWorkflowId() method.
    * To identify the workflow type, use workflowContext.getWorkflowType() property.
    */
```

### Flow Interrupt Listener
Implement `FlowInterruptListener` interface to get flow interrupt update of UI workflow

```java
   /**
     * Inform SDK user that flow is interrupted with errorCode.
     * @param ostWorkflowContext A context that describes the workflow for which the callback was triggered with workflow id.
     * @param ostError Error Entity
     */
   void flowInterrupt(OstWorkflowContext ostWorkflowContext,
                   OstError ostError);

  /**
    * To get workflowId call workflowContext.getWorkflowId() method.
    * To identify the workflow type, use workflowContext.getWorkflowType() property.
    */
```
