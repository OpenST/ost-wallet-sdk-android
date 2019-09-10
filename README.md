# OST Wallet SDK Android

## Introduction
OST Android Wallet SDK is a mobile application development SDK that enables developers to integrate the functionality of a non-custodial crypto-wallet into consumer applications. 

OST Android Wallet SDK...

- Safely generates and stores keys on the user's mobile device
- Signs data as defined by contracts using EIP-1077 and EIP-712
- Enables users to recover access to their Brand Tokens in case the user loses their authorized device

## Support
- Java Compile version: 1.7
- Android version support: 22 and above


## Table of Contents

- [Setup](#setup)
    + [a). Setting minSdkVersion to 22](#a--setting-minsdkversion-to-22)
    + [b). Adding compile options](#b--adding-compile-options)
    + [c). Adding Android Wallet SDK package in dependencies](#c--adding-android-wallet-sdk-package-in-dependencies)
  * [Add mobile SDK config file](#add-mobile-sdk-config-file)
  * [Initialize the Wallet SDK](#initialize-the-wallet-sdk)
- [OST SDK Methods](#ost-sdk-methods)
  * [Types of Methods](#types-of-methods)
- [Workflows](#workflows)
  * [setupDevice](#setupdevice)
  * [activateUser](#activateuser)
  * [addSession](#addsession)
  * [performQRAction](#performqraction)
  * [getDeviceMnemonics](#getdevicemnemonics)
  * [executeTransaction](#executetransaction)
  * [authorizeCurrentDeviceWithMnemonics](#authorizecurrentdevicewithmnemonics)
  * [resetPin](#resetpin)
  * [initiateDeviceRecovery](#initiatedevicerecovery)
  * [abortDeviceRecovery](#abortdevicerecovery)
  * [logoutAllSessions](#logoutallsessions)
- [Getters](#getters)
  * [getAddDeviceQRCode](#getadddeviceqrcode)
  * [getUser](#getuser)
  * [getCurrentDeviceForUserId](#getcurrentdeviceforuserid)
  * [getToken](#gettoken)
  * [isBiometricEnabled](#isbiometricenabled)
  * [getActiveSessionsForUserId](#getactivesessionsforuserid)
- [OST JSON APIs](#ost-json-apis)
  * [getBalance](#getbalance)
  * [getPricePoints](#getpricepoints)
  * [getBalanceWithPricePoints](#getbalancewithpricepoints)
  * [getTransactions](#gettransactions)
  * [getPendingRecovery](#getpendingrecovery)
- [JSON API Response Callback](#json-api-response-callback)
  * [onOstJsonApiSuccess](#onostjsonapisuccess)
  * [onOstJsonApiError](#onostjsonapierror)
- [OST Workflow Callback Interface](#ost-workflow-callback-interface)
  * [Importing the interface](#importing-the-interface)
  * [Interface Functions](#interface-functions)
    + [flowComplete](#flowcomplete)
    + [flowInterrupt](#flowinterrupt)
    + [requestAcknowledged](#requestacknowledged)
    + [getPin](#getpin)
    + [pinValidated](#pinvalidated)
    + [invalidPin](#invalidpin)
    + [registerDevice](#registerdevice)
    + [verifyData](#verifydata)
- [Application development supporting documentation](#application-development-supporting-documentation)
  * [Entities status on User Activities](#entities-status-on-user-activities)
  * [Get Entity Status Updates](#get-entity-status-updates)
  * [Wallet Check on App Launch](#wallet-check-on-app-launch)
  * [Balance Calculation](#balance-calculation)
- [Classes](#classes)
  * [OstError](#osterror)
    + [i). Methods](#i--methods)
  * [OstContextEntity](#ostcontextentity)
    + [i). Methods](#i--methods-1)
  * [OstWorkflowContext](#ostworkflowcontext)
    + [i). Methods](#i--methods-2)
- [Steps to use Android mobile SDK through AAR lib](#steps-to-use-android-mobile-sdk-through-aar-lib)
- [OST Wallet UI](#ost-wallet-ui)
- [Certificate Public Key Pinning](#certificate-public-key-pinning)
  * [TrustKit usage](#trustKit-usage)
  * [Initializing TrustKit with the Pinning Policy](#initializing-trustKit-with-the-pinning-policy)

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

#### c). Adding Android Wallet SDK package in dependencies

```
dependencies {
    implementation 'com.ost:ost-wallet-sdk-android:2.3.0'
    ...
    ...
    ...
}
```
Then sync your dependencies through gradle<br/>
**Note**: Gradle sync might fail for the first time due to build time. Please retry if this happens.

### Add mobile SDK config file
 A config file is needed for application-specific configuration of OST SDK.</br>
 - Create file "ost-mobilesdk.json" with application specific configurations using the JSON below as an example

 ```json
  {
        "BLOCK_GENERATION_TIME": 3,
        "PIN_MAX_RETRY_COUNT": 3,
        "REQUEST_TIMEOUT_DURATION": 60,
        "SESSION_BUFFER_TIME": 3600,
        "PRICE_POINT_CURRENCY_SYMBOL": "USD",
        "PRICE_POINT_TOKEN_SYMBOL": "OST",
        "USE_SEED_PASSWORD": false,
        "NO_OF_SESSIONS_ON_ACTIVATE_USER": 1
  }
 ```

1. BLOCK_GENERATION_TIME: The time in seconds it takes to mine a block on auxiliary chain.
2. PRICE_POINT_CURRENCY_SYMBOL: It is the symbol of quote currency used in price conversion.
3. REQUEST_TIMEOUT_DURATION: Request timeout in seconds for https calls made by ostWalletSdk.
4. PIN_MAX_RETRY_COUNT: Maximum retry count to get the wallet Pin from user.
5. SESSION_BUFFER_TIME: Buffer expiration time for session keys in seconds. Default value is 3600 seconds.
6. USE_SEED_PASSWORD: The seed password is salt to PBKDF2 used to generate seed from the mnemonic. When `UseSeedPassword` set to true, different deterministic salts are used for different keys.
7. PRICE_POINT_TOKEN_SYMBOL: This is the symbol of base currency. So its value will be `OST`.
8. NO_OF_SESSIONS_ON_ACTIVATE_USER: No of session keys to be created and whitelisted while activating user. 


- Place the file under main directory's assets folder <br>

  File path example: app -> src -> main -> assets -> ost-mobilesdk.json</br>
  **NOTE: These configurations are MANDATORY for successful operation. Failing to set them will significantly impact usage.**

### Initialize the Wallet SDK
SDK initialization should happen before calling any other `workflow`. To initialize the SDK, you need to call `initialize` method of Wallet SDK.

**Recommended location to call init() is in Application sub-class.**

```java
import android.app.Application;

import com.ost.mobilesdk.OstWalletSdk;

public class App extends Application {

    public static final String OST_PLATFORM_API_BASE_URL = "https://api.ost.com/testnet/v2";
    @Override
    public void onCreate() {
        super.onCreate();

        OstWalletSdk.initialize(getApplicationContext(), OST_PLATFORM_API_BASE_URL);
    }

}

```


```
  void initialize(context, baseUrl)
```

| Parameter | Description |
|---|---|
| **context** <br> **ApplicationContext**	| Application context can be retrieved by calling **getApplicationContext()**  |
| **baseUrl** <br> **String**	| OST Platform API endpoints: <br> 1. Sandbox Environment: `https://api.ost.com/testnet/v2/` <br> 2. Production Environment: `https://api.ost.com/mainnet/v2/` |

## OST SDK Methods

### Types of Methods

1. `Workflows`: Workflows are the core functions provided by wallet SDK to do wallet related actions. Workflows can be called directly by importing the SDK.

    * Application must confirm to `OstWorkFlowCallback` interface. The `OstWorkFlowCallback` interface defines methods that allow applications to interact with Android Wallet SDK.

2. `Getters`: The SDK provides getter methods that applications can use for various purposes. These methods provide the application with data as available in the device's database. These functions are synchronous and will return the value when requested. 

3. `JSON APIs`: Allows application to access OST Platform APIs


## Workflows

### setupDevice
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
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface).<br> This should implement `registerDevice` function. `registerDevice` will be called during the execution of this workflow.  |


### activateUser
It `authorizes` the registered device and activates the user. User activation deploys the TokenHolder and Device manager  contracts on blockchain. Session keys are also created and authorized during `activateUser` workflow. So after `user activation`, users can perform wallet actions like executing transactions and reset PIN. 

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
| **spendingLimit** <br> **String**	| Spending limit of session key in [atto BT](https://dev.ost.com/platform/docs/guides/execute-transactions/).  |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface).  |


### addSession
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
| **spendingLimit** <br> **String**	| Spending limit of session key in [atto BT](https://dev.ost.com/platform/docs/guides/execute-transactions/).   |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface).   |


### performQRAction
This workflow will perform operations after reading data from a QRCode. This workflow can used to add a new device and to execute transactions.

```
  void performQRAction(String userId, 
                  String data, 
                  OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform  |
| **data** <br> **String**	| JSON object string scanned from QR code. |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface).   |


### getDeviceMnemonics
To get the 12 words recovery phrase of the current device key. Users will use it to prove that it is their wallet.  

```
 void getPaperWallet( String userId, 
                      OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform  |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface).  |


### executeTransaction
To do `user-to-company` and `user-to-user` transactions.

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
| **meta** <br> **Map<String,String>**	|  Transaction Meta properties. <br> Example: <br>{"name": "transaction name",<br>"type": "user-to-user" (it can take one of the following values: `user_to_user`, `user_to_company` and `company_to_user`), <br> "details": "like"}  |
| **options** <br> **Map<String,String>**	| Optional settings parameters. You can set following values: <br> 1. `currency_code`: Currency code for the pay currency. <br> Example: `{"currency_code": "USD"}`|
| **workFlowCallback** <br> **OstWorkFlowCallback**	|An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface).  |


### authorizeCurrentDeviceWithMnemonics
To add a new device using 12 words recovery phrase. 

```
void addDeviceUsingMnemonics( String userId, 
                              byte[] mnemonics, 
                              OstWorkFlowCallback ostWorkFlowCallback)

```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **mnemonics** <br> **byte[]**	| byte array of 12 words. |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface).   |


### resetPin
To change the PIN.

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
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface). |


### initiateDeviceRecovery
A user can control their tokens using their authorized device(s). If a user loses their authorized device, the user can recover access to her tokens by authorizing a new device by initiating the recovery process.

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
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface). |


### abortDeviceRecovery
To abort the initiated device recovery.

```java
void abortDeviceRecovery(String userId, 
                        UserPassphrase passphrase, 
                        OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **passphrase** <br> **UserPassphrase**	| A simple struct to hold and transfer pin information via app and SDK.  |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface). |


### logoutAllSessions
To revoke all the sessions associated with provided userId.

```java
void logoutAllSessions(String userId, 
                       OstWorkFlowCallback workFlowCallback)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **workFlowCallback** <br> **OstWorkFlowCallback**	| An object that implements the callback functions available in `OstWorkFlowCallback` interface. These callback functions are needed for communication between app and wallet SDK. Implement `flowComplete` and `flowInterrupt` callback functions to get the workflow status. Details about other callback function can be found in [OstWorkFlowCallback interface reference](#ostworkflowcallback-interface). |


## Getters

### getAddDeviceQRCode
This getter function will return the QRCode Bitmap that can be used to show on screen. This QRCode can then be scanned to add the new device.

```
Bitmap getAddDeviceQRCode(String userId)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |


### getUser
This returns the loggedin User entity.

```java
OstUser getUser(userId)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |


### getCurrentDeviceForUserId
Method to get user's current device by Id.</br>
This is a synchronous method and must be used only after calling `setupDevice` workflow.</br>
This method returns OstToken only if available with SDK. Returns `null` otherwise.</br>
It does NOT make any server side calls.

```java
OstDevice getCurrentDeviceForUserId(String userId)
```
  
| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |


### getToken
This returns the token entity.

```java
OstToken getToken(tokenId)
```

| Parameter | Description |
|---|---|
| **tokenId** <br> **String**	| Unique identifier of token economy in OST Platform |


### isBiometricEnabled
To get the biometric preferneces call this function.

```java
boolean isBiometricEnabled(userId)
```


| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |


### getActiveSessionsForUserId
Method to get user's active sessions available in current device that can execute transactions of given spending limit.</br>
This is a synchronous method and must be used only after calling `setupDevice` workflow.

```java
List<OstSession> getActiveSessionsForUserId(@NonNull String userId, @Nullable String minimumSpendingLimitInWei)
```

| Parameter | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user stored in OST Platform |
| **minimumSpendingLimitInWei** <br> **String**	| Minimum spending limit of the sessions |

This can also be initialized without `minimumSpendingLimitInWei` <br>

```java
List<OstSession> getActiveSessionsForUserId(@NonNull String userId)
```

## OST JSON APIs

### getBalance
Api to get user balance. Balance of only current logged-in user can be fetched.

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

### getPricePoints
Api to get Price Points. 
It will provide latest conversion rates of base token to fiat currency.

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

### getBalanceWithPricePoints
Api to get user balance and Price Points. Balance of only current logged-in user can be fetched.
It will also provide latest conversion rates of base token to fiat currency.

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

### getTransactions
Api to get user transactions. Transactions of only current logged-in user can be fetched.

**Parameters**<br/>
&nbsp; parameter userId: User Id of the current logged-in user.<br/>
&nbsp; parameter requestPayload: request payload. Such as next-page payload, filters etc.
&nbsp; parameter callback: callback where to receive data/error.<br/>
&nbsp; **getTransactions(userId, requestPayload, callback)**<br/>
```java
OstJsonApi.getTransactions(userId, requestPayload, new OstJsonApiCallback() {
        @Override
        public void onOstJsonApiSuccess(@Nullable JSONObject data) { }

        @Override
        public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) { }
    }
);
```

### getPendingRecovery
Api to get status of pending ongoing recovery.

**Parameters**<br/>
&nbsp; parameter userId: User Id of the current logged-in user.<br/>
&nbsp; parameter callback: callback where to receive data/error.<br/>
&nbsp; **getPendingRecovery(userId, callback)**<br/>
```java
OstJsonApi.getPendingRecovery(userId, new OstJsonApiCallback() {
        @Override
        public void onOstJsonApiSuccess(@Nullable JSONObject data) { }

        @Override
        public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) { }
    }
);
```

## JSON API Response Callback
&nbsp; Callbacks to be implemented by application before calling any of the above OstJsonApis.

### onOstJsonApiSuccess

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


### onOstJsonApiError
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

## OST Workflow Callback Interface
Android SDK provides an interface to be implemented by the Java class calling the `workflows`. 

The interface name is `OstWorkFlowCallback`

### Importing the interface

```
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
```

![walletSDKCommunication](https://dev.ost.com/platform/docs/sdk/assets/wallet-sdk-communication.png)

### Interface Functions

#### flowComplete

This function will be called by wallet SDK when a workflow is completed. The details of workflow and the entity that was updated during the workflow will be available in arguments.

```
void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity)
```

| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	|	Information about the workflow	|
| **ostContextEntity** <br> **OstContextEntity**	| Information about the entity |


#### flowInterrupt
This function will be called by wallet SDK when a workflow is cancelled. The workflow details and error details will be available in arguments.

```
void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError)
```

| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	| Information about the workflow |
| **ostError** <br> **OstError**	| ostError object will have details about the error that interrupted the flow |

#### requestAcknowledged
This function will be called by wallet SDK when the core API request was successful which happens during the execution of workflows. At this stage the workflow is not completed but it shows that the main communication between the wallet SDK and OST Platform server is complete. <br>Once the workflow is complete the `app` will receive the details in `flowComplete` (described below) function. 

```
void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity)
```

| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	| Information about the workflow	|
| **ostContextEntity** <br> **OstContextEntity**	| Information about the entity |



#### getPin
This function will be called by wallet SDK when it needs to get the PIN from the `app` user to authenticate any authorised action.

<br>**Expected Function Definition:** Developers of client company are expected to launch their user interface to get the PIN from the user and pass back this PIN to SDK by calling **ostPinAcceptInterface.pinEntered()** 

```
void getPin(String userId, OstPinAcceptInterface ostPinAcceptInterface)
```

| Argument | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user |
| **ostPinAcceptInterface** <br> **OstPinAcceptInterface**	| **ostPinAcceptInterface.pinEntered()** should be called to pass the PIN back to SDK. <br> For some reason if the developer wants to cancel the current workflow they can do it by calling **ostPinAcceptInterface.cancelFlow()** |


#### pinValidated
This function will be called by wallet SDK when the last entered PIN is validated. 

```
void pinValidated(String userId)
```

| Argument | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user |


#### invalidPin
This function will be called by wallet SDK when the last entered PIN was wrong and `app` user has to provide the PIN again. Developers are expected to repeat the `getPin` method here and pass back the PIN again back to the SDK by calling **ostPinAcceptInterface.pinEntered()** .

```
void invalidPin(String userId, OstPinAcceptInterface ostPinAcceptInterface)
```

| Argument | Description |
|---|---|
| **userId** <br> **String**	|	Unique identifier of the user	|
| **ostPinAcceptInterface** <br> **OstPinAcceptInterface**	| **ostPinAcceptInterface.pinEntered()** should be called to again pass the PIN back to SDK. <br> For some reason if the developer wants to cancel the current workflow they can do it by calling **ostPinAcceptInterface.cancelFlow()**  |


#### registerDevice
This function will be called by wallet SDK to register the device.<br>**Expected Function Definition:** Developers of client company are expected to register the device by communicating with client company's server. On client company's server they can use `Server SDK` to register this device in OST Platform. Once the device is registered on OST Platform client company's server will receive the newly created `device` entity. This device entity should be passed back to the `app`.<br>
Finally they should pass back this newly created device entity back to the wallet SDK by calling **OstDeviceRegisteredInterface.deviceRegistered(JSONObject newDeviceEntity )**.

```
void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface)
```

| Argument | Description |
|---|---|
| **apiParams** <br> **JSONObject**	|	Device information for registration	|
| **ostDeviceRegisteredInterface** <br> **OstDeviceRegisteredInterface**	| **OstDeviceRegisteredInterface.deviceRegistered(JSONObject newDeviceEntity )** should be called to pass the newly created device entity back to SDK. <br>In case data is not verified the current workflow should be canceled by developer by calling **OstDeviceRegisteredInterface.cancelFlow()**  |


#### verifyData
This function will be called by wallet SDK to verify data during `performQRAction` workflow.


```
void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface)
```


| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	| Information about the current workflow during which this callback will be called	|
| **ostContextEntity** <br> **OstContextEntity**	| Information about the entity |
| **ostVerifyDataInterface** <br> **OstVerifyDataInterface**	| **ostVerifyDataInterface.dataVerified()** should be called if the data is verified successfully. <br>In case data is not verified the current workflow should be canceled by developer by calling **ostVerifyDataInterface.cancelFlow()** |



## Application development supporting documentation
 
### Entities status on User Activities
|User Activity |App State|User Status|Device Status|Session status|
| --- | --- | :---: | :---: | :---: |
|Installs app for the first time|Not Login|CREATED|UNREGISTED| `NA`|
|Login in the app for the first time|Log In|CREATED|REGISTERED| `NA`|
|Initiate Activate Wallet by entering pin|Activating Wallet|ACTIVATING|AUTHORIZING|INITIALIZING|
|Activates Wallet after waiting|Activated Wallet|ACTIVATED|AUTHORIZED|AUTHORISED|
|Performs transactions|Activated Wallet|ACTIVATED|AUTHORIZED|AUTHORISED|
|Session get expired|Activated Wallet|ACTIVATED|AUTHORIZED|EXPIRED|
|Logout all Sessions|Activated Wallet|ACTIVATED|AUTHORIZED|REVOKING -> REVOKED|
|Add Session|Activated Wallet|ACTIVATED|AUTHORIZED|INITIALIZING -> AUTHORISED|
|Log out from app|Not Login|ACTIVATED|AUTHORIZED|AUTHORISED|
|Log in back to App|Activated Wallet|ACTIVATED|AUTHORIZED|AUTHORISED|
|Reinstall the App|No Login|CREATED|UNREGISTED| `NA`|
|Login in the app|Log In|ACTIVATED|REGISTERED| `NA`|
|Recover Wallet Or Add Wallet|Activating Wallet|ACTIVATED|AUTHORIZING -> AUTHORISED| `NA`|
|Revoked Device from other device|Activated Wallet|ACTIVATED|REVOKING -> REVOKED| `NA`|

### Get Entity Status Updates
To get real time updates of entities like ongoing activation Or transactions, server side SDK's [WebHooks](https://dev.ost.com/platform/docs/api/#webhooks) services can be used.

### Wallet Check on App Launch
* Check whether User need Activation.
* Check whether Wallet need Device Addition Or Recovery.
  * For device addition, the current Device which is to be Authorized should used **OstSdk.getAddDeviceQRCode** to generate QR code And **OstSdk.performQRAction()** method should be used to process that QR from AUTHORIZED deivce.
  * Device can also be added through **OstSdk.authorizeCurrentDeviceWithMnemonics()** by passing AUTHORIZED device mnemonics.
  * Or Device can be recovered through **OstSdk.initiateDeviceRecovery()** by passing Device address of the Device to be recovered from.
```java
if (!(ostUser.isActivated() || ostUser.isActivating())) {
        //TODO:: Wallet need Activation
} else if (ostUser.isActivated() && ostUser.getCurrentDevice().canBeAuthorized()) { 
        //TODO:: Ask user whether he wants to Add device through QR or Mnemonics Or want to recover device. 
} else {
        //TODO:: App Dashboard
}
```
### Balance Calculation
* TokenHolder Balance can be shown in Token currency or in Fiat currency.
  * For Token currency conversion, the fetched balance is in Wei unit, which needs to be converted to Base unit.
  * For Fiat currency conversion, the fetched balance first need to be converted to fiat equivalent using current converion rate from price points and then to its Base unit.
```java
OstJsonApi.getBalanceWithPricePoints(userId, new OstJsonApiCallback() {
            @Override
            public void onOstJsonApiSuccess(@Nullable JSONObject jsonObject) {
                if ( null != jsonObject ) {
                    String balance = "0";
                    JSONObject pricePoint = null;
                    try{
                        JSONObject balanceData = jsonObject.getJSONObject(jsonObject.getString("result_type"));
                        balance = balanceData.getString("available_balance");
                        pricePoint = jsonObject.optJSONObject("price_point");
                    } catch(Exception e){ 
                    }
                    //To user balance in token currency with two decimals.
                    convertWeiToTokenCurrency(balance);
                    
                    //To user balance in fiat(Dollar) with two decimals.
                    convertBTWeiToFiat(balance, pricePoint)
                } else {
                        //Todo:: Show fetch error
                }
            }

            @Override
            public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject data) {
                //Todo:: Show fetch error
            }
        });

public static String convertWeiToTokenCurrency(String balance) {
        if (null == balance) return "0";

        OstToken token = OstSdk.getToken(AppProvider.getTokenId());
        Integer decimals = Integer.parseInt(token.getBtDecimals());
        BigDecimal btWeiMultiplier = new BigDecimal(10).pow(decimals);
        BigDecimal balance = new BigDecimal(balance).divide(btWeiMultiplier);
        return balance.setScale(2, RoundingMode.HALF_UP).toString();
    }

public static String convertBTWeiToFiat(String balance, JSONObject pricePointObject) {
        if (null == balance || null == pricePointObject) return null;

        try{
            OstToken token = OstSdk.getToken(AppProvider.getTokenId());
            double pricePointOSTtoUSD = pricePointObject.getJSONObject(token.getBaseToken()).getDouble("USD");
            int fiatDecimalExponent = pricePointObject.getJSONObject(token.getBaseToken()).getInt("decimals");
            BigDecimal fiatToEthConversionFactor = new BigDecimal("10").pow(fiatDecimalExponent);

            BigDecimal tokenToFiatMultiplier = calTokenToFiatMultiplier(pricePointOSTtoUSD, fiatDecimalExponent, token.getConversionFactor(), Integer.parseInt(token.getBtDecimals()));

            BigDecimal fiatBalance = new BigDecimal(balance).multiply(tokenToFiatMultiplier);

            return fiatBalance.divide(fiatToEthConversionFactor, 2, RoundingMode.DOWN).toString();

        } catch (Exception e){
            return null;
        }
    }
```

## Classes

1. OstError
2. OstContextEntity
3. OstWorkflowContext


### OstError
This class is used to provide error details in [flowInterrupt](#ostworkflowcallback-interface#2-flowinterrupt) callback function. 


You can call the following methods on this object to get more details about the error.

#### i). Methods

1. `public OstErrors.ErrorCode getErrorCode()`
2. `public String getInternalErrorCode()`
3. `public boolean isApiError()`

### OstContextEntity
This class provides context about the `entity` that is being changed during a [workflow](#workflows). Callback functions that needs to know about the `entity` will receive an object of this class as an argument. 


You can call the following methods on this object to get more details about the entity.

#### i). Methods

1. `public OstContextEntity(String message, Object entity, String entityType)`
2. `public OstContextEntity(Object entity, String entityType)`
3. `public String getMessage()`
4. `public Object getEntity()`
5. `public String getEntityType()`


### OstWorkflowContext
This class provides context about the current [workflow](#workflows). Callback function that needs to know about the current [workflow](#workflows) will get the object of this class as an argument.

You can call the following methods on this object to get more details about the current [workflow](#workflows).

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

## Steps to use Android mobile SDK through AAR lib
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

## OST Wallet UI 
For quick and easy integration with SDK, developers can use built-in user interface components which are configurable and support content and theme customization. All OstWalletSdkUI workflows return workflow-id. The application can subscribe to the events of the workflow using the workflow-id. Please refer [OstWalletUI](./documentation/OstWalletUI.md).

## Certificate Public Key Pinning
App can do certificate public key pinning with the help of OstSdk. 
App can leverage TrustKit(Part of OstSdk)for certificate pinning.</br>
OstSdk exposes TrustKit library apis, So app does not have add TrustKit dependency separately.</br>
**Note** Application have to make sure they initialize TrustKit before OstSdk initialization.


### TrustKit usage
Deploying SSL pinning in the App requires initializing TrustKit with a pinning policy (domains, pins, and additional settings). The policy is wrapped in the official [Android N Network Security Configuration](https://developer.android.com/training/articles/security-config.html) </br>

App have to define its pinning policy in *network_security_config* file and should also add pinning policy of OstSdk.
Please add the below file with you app pinning policy in **Application Pinning Policy** section.
```xml
<!-- res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- Application Pinning Policy Begin -->
    
    <!-- Application Pinning Policy End -->
    
    <!-- Pinning Policy of OstSdk -->
    <domain-config>
        <domain includeSubdomains="false">api.ost.com</domain>
        <pin-set>
            <pin digest="SHA-256">s4vrk6by0cqKQ9p/mFOakoi0daivc7Le8q7fUuuo4/U=</pin>
            <pin digest="SHA-256">MvVeCJ2tAuJZmbqoXMqSNP2mKh+VjGiljvqWytjzasU=</pin>
            <pin digest="SHA-256">J+0IGhy08mkHR1Z1WbdrHEdHhXRohrdLHUYORlWGafA=</pin>
            <pin digest="SHA-256">aF+lKYb0WChlCTx5uPBw5ZWze/98vAXSzBBIrVSZWJE=</pin>
            <pin digest="SHA-256">efgWbb0q/zHFLub1SY5QpoQVlZp33QpLOj0EmhoK8tI=</pin>
        </pin-set>
        <trustkit-config enforcePinning="true">
        </trustkit-config>
    </domain-config>
</network-security-config>
```
### Initializing TrustKit with the Pinning Policy

The path to the XML policy should then be specified [in the App's manifest](https://developer.android.com/training/articles/security-config.html#manifest) in order to enable it as the App's [Network Security Configuration](https://developer.android.com/training/articles/security-config.html) on Android N.
To replace OstSdk networkSecurityConfig add *tools:replace="android:networkSecurityConfig*:

```
<?xml version="1.0" encoding="utf-8"?>
<manifest ... >
    <application android:networkSecurityConfig="@xml/network_security_config"
    tools:replace="android:networkSecurityConfig"
                    ... >
        ...
    </application>
</manifest>

```

Then, TrustKit should be initialized with the same path:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.OnCreate(savedInstanceState);

  // Using the default path - res/xml/network_security_config.xml
  TrustKit.initializeWithNetworkSecurityConfiguration(this);

  // OR using a custom resource (TrustKit can't be initialized twice)
  TrustKit.initializeWithNetworkSecurityConfiguration(this, R.xml.network_security_config);
  
  // String BASE_URL = <OstPlatform Url>
  // Initalize OstSdk  
  OstWalletUI.initialize(getApplicationContext(), BASE_URL);
}
