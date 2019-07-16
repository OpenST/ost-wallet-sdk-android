# OST Wallet SDK Android

## Introduction
Wallet SDK is a mobile application development SDK that enables developers to integrate the functionality of a non-custodial crypto-wallet into consumer applications. The SDK:

- Safely generates and stores keys on the user's mobile device
- Signs ethereum transactions and data as defined by contracts using EIP-1077
- Enables users to recover access to their Brand Tokens in case the user loses their authorized device</br>

## Support
- Java Compile version: 1.7
- Android version support: 22 and above





## Setup
#### a). Setting minSdkVersion to 22
```
android {
    defaultConfig {
        minSdkVersion 22
        ...
        ...
        ...
    }
}
```

#### b). Adding compile options
Add following code in your `build.gradle` file

```
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
    }
```

#### c). Adding android wallet sdk package in dependencies

```
dependencies {
    implementation 'com.ost:ost-wallet-sdk-android:2.2.1'
    ...
    ...
    ...
}
```
### Add mobile sdk config file
 A config file is needed for application-specific configuration of OST  SDK.</br>
 - Create file "ost-mobilesdk.json" with application specific configurations using  the json below as an example

 ```json
  {
        "BLOCK_GENERATION_TIME": 3,
        "PIN_MAX_RETRY_COUNT": 3,
        "REQUEST_TIMEOUT_DURATION": 60,
        "SESSION_BUFFER_TIME": 3600,
        "PRICE_POINT_CURRENCY_SYMBOL": "USD",
        "USE_SEED_PASSWORD": false
  }
 ```
- Place the file under main directory's assets folder <br>

  File path example: app -> src -> main -> assets -> ost-mobilesdk.json</br>
 **NOTE:These configurations are MANDATORY for successful operation. Failing to set them will significantly impact usage.**
 
 

## [Android SDK Usage](https://dev.ost.com/platform/docs/sdk/wallet_sdk_setup/android/#4-initialize-the-wallet-sdk)


## OST SDK Methods

### Types of Methods

1. `Workflows`: Workflows are the core functions provided by wallet SDK to do wallet related actions. Workflows can be called directly by importing the SDK.

	* Application must confirm to `OstWorkFlowCallback` interface. The `OstWorkFlowCallback` interface defines methods that allow application to interact with Android Wallet SDK.


2. `Getters`: These functions are synchronous and will return the value when requested.

## Workflows


### 1. initialize

You must initialize the SDK before start using it.
<br> **Recommended location to call initialize() is in [Application](https://developer.android.com/reference/android/app/Application) sub-class.**

```
  void initialize(context, baseUrl)
```

| Parameter | Description |
|---|---|
| **context** <br> **ApplicationContext**	| Application context can be retrieved by calling **getApplicationContext()**  |
| **baseUrl** <br> **String**	| OST Platform API endpoints: <br> 1. Sandbox Environment: `https://api.ost.com/testnet/v2/` <br> 2. Production Environment: `https://api.ost.com/mainnet/v2/` |

#### Sample code to initialize SDK

```java
import com.ost.mobilesdk.OstWalletSdk;
....
....

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		OstWalletSdk.initialize(getApplicationContext(), <OST_PLATFORM_API_ENDPOINT>);
	}
	
	...
	...
	...

}

```

<br>

### 2. setupDevice
This workflow needs `userId` and `tokenId` so `setupDevice` should be called after your app login or signup is successful.
Using the mapping between userId in OST Platform and your app user, you have access to `userId` and `tokenId`.

**If the user is logged in, then `setupDevice` should be called every time the app launches, this ensures that the current device is registered before communicating with OST Platform server.**


```
void setupDevice( String userId, 
                  String tokenId, 
                  OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform  |
| **tokenId** <br> **String**	| Unique identifier for the token economy |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/).<br> This should implement `registerDevice` function. `registerDevice` will be called during the execution of this workflow.  |

<br>

### 3. activateUser
It `authorizes` the registered device and activates the user. User activation deploys  **TokenHolder**, Device manager  contracts on blockchain. Session keys are also created and authorized during `activateUser` workflow. So after `user activation`, users can perform wallet actions like executing transactions and reset pin. 

```
void activateUser(UserPassphrase passphrase, 
                  long expiresAfterInSecs, 
                  String spendingLimit, 
                  OstWorkFlowCallback callback)
```

| Parameter | Description |
|---|---|
| **userPassPhrase** <br> **UserPassphrase**	| A simple struct to hold and transfer pin information via app and SDK. |
| **expiresAfterInSecs** <br> **long**	| Expire time of session key in seconds. |
| **spendingLimit** <br> **String**	| Spending limit of session key in [atto BT](/platform/docs/sdk/guides/execute_transaction/#converting-brand-token-to-atto-brand-token).  |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/).  |

<br>

### 4. addSession
This workflow will create and authorize the session key that is needed to do the transactions. This flow should be called if the session key is expired or not present. 

```
 void addSession( String userId, 
                  long expireAfterInSecs, 
                  String spendingLimit, 
                  OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform  |
| **expiresAfterInSecs** <br> **long**	| Expire time of session key in seconds.  |
| **spendingLimit** <br> **String**	| Spending limit of session key in [atto BT](/platform/docs/sdk/guides/execute_transaction/#converting-brand-token-to-atto-brand-token).   |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/).   |

<br>

### 5. performQRAction
This workflow will perform operations after reading data from a QRCode. This workflow can used to add a new device and to do the transactions.

```
  void performQRAction(String userId, 
                  String data, 
                  OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform  |
| **data** <br> **String**	| JSON object string scanned from QR code. |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/).   |

<br>

### 6. getDeviceMnemonics
To get the 12 words recovery phrase of the current device key. Users will use it to prove that it is their wallet.  

```
 void getPaperWallet( String userId, 
                      OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform  |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/).  |


<br>

### 7. executeTransaction
Workflow should be used to do the `user-to-company` and `user-to-user` transactions.

```java
void executeTransaction(String userId, 
                        String tokenId, 
                        List tokenHolderAddresses, 
                        List amounts, 
                        String ruleName, 
                        Map<String,String> meta, 
                        Map<String,String> options,
                        OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **tokenId** <br> **String**	| Unique identifier for the token economy |
| **tokenHolderAddresses** <br> **List**	|  **TokenHolder**  addresses of amount receiver |
| **amounts** <br> **List**	| Amount to be transferred in atto.  |
| **ruleName** <br> **String**	|  Rule name to be executed.  |
| **meta** <br> **Map<String,String>**	|  Transaction Meta properties. <br> Example: `{"name": "transaction name","type": "user-to-user","details": "like"}`  |
| **options** <br> **Map<String,String>**	| Optional settings parameters. You can set following values: <br> 1. `currency_code`: Currency code for the pay currency. <br> 2. `wait_for_finalization`: If set `false` then SDK will stop polling for transaction status. By default the SDK will do polling to check the transaction status. <br> Example: `{"currency_code": "USD", "wait_for_finalization": false}`|
| **workFlowCallback** <br> **OstWorkFlowCallback**	|An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/).  |


<br>
### 8. authorizeCurrentDeviceWithMnemonics
This workflow should be used to add a new device using 12 words recovery phrase. 

```
void addDeviceUsingMnemonics( String userId, 
                              byte[] mnemonics, 
                              OstWorkFlowCallback ostWorkFlowCallback)

```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **mnemonics** <br> **byte[]**	| byte array of 12 words. |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/).   |

<br>

### 9. resetPin
This workflow can be used to change the PIN.

**User will have to provide the current PIN in order to change it.**

```
  void resetPin(String userId,  
                String appSalt, 
                String currentPin, 
                String newPin, 
                OstWorkFlowCallback workFlowCallback)
```


| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **appSalt** <br> **String**	|   |
| **currentPin** <br> **String**	| Current PIN  |
| **newPin** <br> **String**	| New PIN |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/). |
<br>

### 10. initiateDeviceRecovery
A user can control their Brand Tokens using their authorized devices. If they lose their authorized device, they can recover access to their Brand Tokens by authorizing a new device by initiating the recovery process.

```java
void initiateDeviceRecovery(String userId, 
                            UserPassphrase passphrase, 
                            String deviceAddressToRecover, 
                            OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **passphrase** <br> **UserPassphrase**	| A simple struct to hold and transfer pin information via app and SDK.  |
| **deviceAddressToRecover** <br> **String**	| Address of device to recover  |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/). |
<br>
### 11. abortDeviceRecovery
This workflow can be used to abort the initiated device recovery.

```java
void abortDeviceRecovery(String userId, 
                        UserPassphrase passphrase, 
                        OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **passphrase** <br> **UserPassphrase**	| A simple struct to hold and transfer pin information via app and SDK.  |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/). |


<br>

### 12. logoutAllSessions
This workflow will revoke all the sessions associated with the provided userId.

```java
void logoutAllSessions(String userId, 
                       OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/). |

<br>

## Getters



### 1. getAddDeviceQRCode
This getter function will return the QRCode Bitmap that can be used to show on screen. This QRCode can then be scanned to add the new device.

```
Bitmap getAddDeviceQRCode(String userId)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |

<br>


## Interface
Android SDK provides an interface to be implemented by the Java class calling the `workflows`. 
<br>
The interface name is `OstWorkFlowCallback`

### Importing the interface

```
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
```

![walletSDKCommunication](/platform/docs/sdk/assets/wallet-sdk-communication.png)


## Interface Functions


### 1. flowComplete

This function will be called by wallet SDK when a workflow is completed. The details of workflow and the entity that was updated during the workflow will be available in arguments.

```
void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity)
```

| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	|	Information about the workflow	|
| **ostContextEntity** <br> **OstContextEntity**	| Information about the entity |



<br>




### 2. flowInterrupt
This function will be called by wallet SDK when a workflow is cancelled. The workflow details and error details will be available in arguments.

```
void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError)
```

| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	| Information about the workflow |
| **ostError** <br> **OstError**	| ostError object will have details about the error that interrupted the flow |



<br>




### 3. requestAcknowledged
This function will be called by wallet SDK when the core API request was successful which happens during the execution of workflows. At this stage the workflow is not completed but it shows that the main communication between the wallet SDK and OST Platform server is complete. <br>Once the workflow is complete the `app` will receive the details in `flowComplete` (described below) function. 

```
void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity)
```

| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	| Information about the workflow	|
| **ostContextEntity** <br> **OstContextEntity**	| Information about the entity |

<br>




### 4. getPin
This function will be called by wallet SDK when it needs to get the PIN from the `app` user to authenticate any authorised action.
<br>**Expected Function Definition:** Developers of client company are expected to launch their user interface to get the PIN from the user and pass back this PIN to SDK by calling **ostPinAcceptInterface.pinEntered()** 

```
void getPin(String userId, OstPinAcceptInterface ostPinAcceptInterface)
```

| Argument | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user |
| **ostPinAcceptInterface** <br> **OstPinAcceptInterface**	| **ostPinAcceptInterface.pinEntered()** should be called to pass the PIN back to SDK. <br> For some reason if the developer wants to cancel the current workflow they can do it by calling **ostPinAcceptInterface.cancelFlow()** |


<br>




### 5. pinValidated
This function will be called by wallet SDK when the last entered PIN is validated. 

```
void pinValidated(String userId)
```

| Argument | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user |




<br>



### 6. invalidPin
This function will be called by wallet SDK when the last entered PIN was wrong and `app` user has to provide the PIN again. Developers are expected to repeat the `getPin` method here and pass back the PIN again back to the SDK by calling  **ostPinAcceptInterface.pinEntered()** .

```
void invalidPin(String userId, OstPinAcceptInterface ostPinAcceptInterface)
```

| Argument | Description |
|---|---|
| **userId** <br> **String**	|	Unique identifier of the user	|
| **ostPinAcceptInterface** <br> **OstPinAcceptInterface**	| **ostPinAcceptInterface.pinEntered()** should be called to again pass the PIN back to SDK. <br> For some reason if the developer wants to cancel the current workflow they can do it by calling **ostPinAcceptInterface.cancelFlow()**  |


<br>


### 7. registerDevice
This function will be called by wallet SDK to register the device.<br>**Expected Function Definition:** Developers of client company are expected to register the device by communicating with client company's server. On client company's server they can use `Server SDK` to register this device in OST Platform. Once the device is registered on OST Platform client company's server will receive the newly created `device` entity. This device entity should be passed back to the `app`.<br>
Finally they should pass back this newly created device entity back to the wallet SDK by calling **OstDeviceRegisteredInterface.deviceRegistered(JSONObject newDeviceEntity )**.

```
void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface)
```

| Argument | Description |
|---|---|
| **apiParams** <br> **JSONObject**	|	Device information for registration	|
| **ostDeviceRegisteredInterface** <br> **OstDeviceRegisteredInterface**	| **OstDeviceRegisteredInterface.deviceRegistered(JSONObject newDeviceEntity )** should be called to pass the newly created device entity back to SDK. <br>In case data is not verified the current workflow should be canceled by developer by calling **OstDeviceRegisteredInterface.cancelFlow()**  |



<br>

### 8. verifyData
This function will be called by wallet SDK to verify data during `performQRAction` workflow.


```
void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface)
```


| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	| Information about the current workflow during which this callback will be called	|
| **ostContextEntity** <br> **OstContextEntity**	| Information about the entity |
| **ostVerifyDataInterface** <br> **OstVerifyDataInterface**	| **ostVerifyDataInterface.dataVerified()** should be called if the data is verified successfully. <br>In case data is not verified the current workflow should be canceled by developer by calling **ostVerifyDataInterface.cancelFlow()** |



## OST JSON APIs

### 1. getBalance

Api to get user balance. Balance of only current logged-in user can be fetched.<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: User Id of the current logged-in user.<br/>
&nbsp; parameter callback: callback where to receive data/error.<br/>
&nbsp; **getBalance(userId, callback)**<br/>
```java
OstJsonApi.getBalance(userId, new OstJsonApiCallback() {
        @Override
        public void onOstJsonApiSuccess(@Nullable JSONObject data) { }

        @Override
        public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) { }
    }
);
```

### 2. getPricePoints

Api to get Price Points. 
It will provide latest conversion rates of base token to fiat currency.<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: User Id of the current logged-in user.<br/>
&nbsp; parameter callback: callback where to receive data/error.<br/>
&nbsp; **getPricePoints(userId, callback)**<br/>
```java
OstJsonApi.getPricePoints(userId, new OstJsonApiCallback() {
        @Override
        public void onOstJsonApiSuccess(@Nullable JSONObject data) { }

        @Override
        public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) { }
    }
);
```

### 3. getBalanceWithPricePoints

Api to get user balance and Price Points. Balance of only current logged-in user can be fetched.
It will also provide latest conversion rates of base token to fiat currency.<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: User Id of the current logged-in user.<br/>
&nbsp; parameter callback: callback where to receive data/error.<br/>
&nbsp; **getBalanceWithPricePoints(userId, callback)**<br/>
```java
OstJsonApi.getBalanceWithPricePoints(userId, new OstJsonApiCallback() {
        @Override
        public void onOstJsonApiSuccess(@Nullable JSONObject data) { }

        @Override
        public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) { }
    }
);
```

### 4. getTransactions

Api to get user transactions. Transactions of only current logged-in user can be fetched.<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: User Id of the current logged-in user.<br/>
&nbsp; parameter requestPayload: request payload. Such as next-page payload, filters etc.
&nbsp; parameter callback: callback where to receive data/error.<br/>
&nbsp; **getTransactions(userId, callback)**<br/>
```java
OstJsonApi.getTransactions(userId, requestPayload, new OstJsonApiCallback() {
        @Override
        public void onOstJsonApiSuccess(@Nullable JSONObject data) { }

        @Override
        public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) { }
    }
);
```

### 5. getPendingRecovery

Api to get status of pending ongoing recovery.<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: User Id of the current logged-in user.<br/>
&nbsp; parameter callback: callback where to receive data/error.<br/>
&nbsp; **getPendingRecovery(userId, callback)**<br/>
```java
OstJsonApi.getPendingRecovery(userId, requestPayload, new OstJsonApiCallback() {
        @Override
        public void onOstJsonApiSuccess(@Nullable JSONObject data) { }

        @Override
        public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) { }
    }
);
```
## Json Api Response Callback
&nbsp; Callbacks to be implemented by application before calling any of the above OstJsonApis.

### 1. onOstJsonApiSuccess

```java
   /**
     * Inform SDK user about Success of OstJsonApi
     * @param data Response data
     */
    public void onOstJsonApiSuccess(@Nullable JSONObject data) { }
```
| Argument | Description |
|---|---|
| **data** <br> **JSONObject**	|	Api Response data	|


### 2. onOstJsonApiError
```java
   /**
     * Inform SDK user about Failure of OstJsonApi
     * @param err      OstError object containing error details
     * @param response Api response
     */
    public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) { }
```
| Argument | Description |
|---|---|
| **err** <br> **OstError**	|	OstError object containing error details	|
| **response** <br> **JSONObject**	|	Api Response	|

## Steps to use Android mobile sdk through AAR lib
- Download AAR file from S3 [Download link](https://sdk.stagingost.com.s3.amazonaws.com/Android/release/ostsdk-release.aar)
- Create libs folder under app directory in your application project.
- In libs folder add your downloaded aar file.
- Add aar lib dependency to your build.gradle file
```
 implementation files('libs/ostsdk-release.aar')
```
- Also add dependencies of ostsdk in you build.gradle

```groovy
dependencies {

    // your app dependencies

    //--- Section to Copy  ----

    // Room components
    implementation "android.arch.persistence.room:runtime:1.1.1"
    annotationProcessor "android.arch.persistence.room:compiler:1.1.1"
    implementation 'com.madgag.spongycastle:core:1.56.0.0'
    implementation 'org.web3j:core:4.1.0-android'
    // Lifecycle components
    implementation "android.arch.lifecycle:extensions:1.1.1"
    annotationProcessor "android.arch.lifecycle:compiler:1.1.1"
    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation 'com.google.guava:guava:18.0'
    // Zxing barcode dependency
    implementation 'me.dm7.barcodescanner:zxing:1.9.8'

    //---Section to Copy  ----

}
```

- Clean and then Build your Android project.


## Classes

1. OstError
2. OstContextEntity
3. OstWorkflowContext


### 1. OstError
This class is used to provide error details in [flowInterrupt](/platform/docs/sdk/references/wallet_sdk/android/latest/interfaces/#2-flowinterrupt) callback function. 


You can call [methods](#i-methods) on this object to get more details about the error.

#### i). Methods

1. `public OstErrors.ErrorCode getErrorCode()`
2. `public String getInternalErrorCode()`
3. `public boolean isApiError()`

### 2. OstContextEntity
This class provides context about the `entity` that is being changed during a [workflow](/platform/docs/sdk/references/wallet_sdk/android/latest/methods/#workflows). Callback functions that needs to know about the `entity` will receive an object of this class as an argument. 


You can call [methods](#i-methods-1) on this object to get more details about the entity.

#### i). Methods

1. `public OstContextEntity(String message, Object entity, String entityType)`
2. `public OstContextEntity(Object entity, String entityType)`
3. `public String getMessage()`
4. `public Object getEntity()`
5. `public String getEntityType()`


### 3. OstWorkflowContext
This class provides context about the current [workflow](/platform/docs/sdk/references/wallet_sdk/android/latest/methods/#workflows). Callback function that needs to know about the current [workflow](/platform/docs/sdk/references/wallet_sdk/android/latest/methods/#workflows) will get the object of this class as an argument.

You can call [methods](#i-methods-2) on this object to get more details about the current [workflow](/platform/docs/sdk/references/wallet_sdk/android/latest/methods/#workflows).


The `getWorkflow_type()` methods will return one of the strings from this enum.

```java
public enum WORKFLOW_TYPE {
        UNKNOWN,
        SETUP_DEVICE,
        ACTIVATE_USER,
        ADD_SESSION,
        GET_DEVICE_MNEMONICS,
        PERFORM_QR_ACTION,
        EXECUTE_TRANSACTION,
        AUTHORIZE_DEVICE_WITH_QR_CODE,
        AUTHORIZE_DEVICE_WITH_MNEMONICS,
        INITIATE_DEVICE_RECOVERY,
        ABORT_DEVICE_RECOVERY,
        REVOKE_DEVICE_WITH_QR_CODE,
        RESET_PIN,
        LOGOUT_ALL_SESSIONS
    }
```

#### i). Methods

1. `public OstWorkflowContext(WORKFLOW_TYPE workflow_type)`
2. `public OstWorkflowContext()`
3. `public WORKFLOW_TYPE getWorkflow_type()`
