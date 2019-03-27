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
Add the OST SDK as a dependency in build.gradle
```
dependencies {
        implementation 'com.ost:ost-wallet-sdk-android:0.1.0.beta.11'
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
        "PRICE_POINT_TOKEN_SYMBOL": "OST",
        "PRICE_POINT_CURRENCY_SYMBOL": "USD"
  }
 ```
- Place the file under main directory's assets folder <br>

  File path example: app -> src -> main -> assets -> ost-mobilesdk.json</br>

### Update build.gradle files
Add compile options for Java 1.8 version in the Application's `build.gradle` files
```
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

## OST SDK APIs

### Initialize the SDK
The SDK can be initialized by calling the `initialize()` API which
initializes all the required instances and runs migrations of local databases.<br/>
We recommended that you call initialize() in Application sub-class.<br/><br/>
**Parameters**<br/>
&nbsp; parameter context: ApplicationContext.<br/>
&nbsp; parameter baseUrl: OST Platform endpoint.<br/>
&nbsp; **initialize(context, baseUrl)**<br/>
```java
public void onCreate() {
        super.onCreate();
        OstSdk.initialize(getApplicationContext(), BASE_URL);
}
```

### Set up the device
The `setupDevice` API should be called each time the application is launched to confirm that the current device is in the `registered` state and therefore able to call the OST Platform APIs.<br/><br/>

We recommended that you call `setupDevice()` in MainActivity.<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter tokenId: Id assigned by Ost to token<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void setupDevice(String userId, String tokenId, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.setupDevice(userId, tokenId, new OstWorkFlowCallbackImpl());
```

### Activate the user
User activation refers to the deployment of smart-contracts that form the user's Brand Token wallet. An activated user can engage with a Brand Token economy.<br/><br/>
**Parameters**<br/>
&nbsp; parameter UserPassphrase: object which will contain user Id, user pin and passphrasePrefix<br/>
&nbsp; parameter expiresAfterInSecs: session key expiry time<br/>
&nbsp; parameter spendingLimitInWei: spending limit once in a transaction of session<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void activateUser(UserPassphrase passphrase, long expiresAfterInSecs, String spendingLimitInWei, OstWorkFlowCallback callback)**<br/>

```java
UserPassphrase userPassPhrase = new UserPassphrase(userId, pin, passphrasePrefix)
OstSdk.activateUser(userPassPhrase, expiresAfterInSecs, spendingLimitInWei, new OstWorkFlowCallbackImpl())
```

### Authorize a session
A session is a period of time during which a sessionKey is authorized to sign transactions under a pre-set limit on behalf of the user.
The device manager, which controls the tokens, authorizes sessions.
 <br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter expiresAfterInSecs: sessions key expiry time<br/>
&nbsp; parameter spendingLimitInWei: spending limit once in a transaction of session<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void addSession(String userId, long expireAfterInSecs, String spendingLimitInWei, OstWorkFlowCallback workFlowCallback)**<br/>

```java
OstSdk.addSession(userId, expireAfterInSecs, spendingLimitInWei, new OstWorkFlowCallbackImpl())

```
### Execute a transaction
A transaction where Brand Tokens are transferred from a user to another actor within the Brand Token economy are signed using `sessionKey` if there is an active session. In the absence of an active session, a new session is authorized.<br/><br/>

**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter tokenHolderAddresses: Token holder addresses of amount receiver<br/>
&nbsp; parameter amounts: Amounts corresponding to tokenHolderAddresses in wei to be transfered<br/>
&nbsp; parameter ruleName: Rule name to be executed<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void executeTransaction(String userId, String tokenId, List<String> tokenHolderAddresses, List<String> amounts, String ruleName, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.executeTransaction(userId, tokenHolderAddresses, amounts, ruleName, new OstWorkFlowCallbackImpl())
````

### Get Mnemonic Phrase
The mnemonic phrase represents a human-readable way to authorize a new device. This phrase is 12 words long.
 <br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void getPaperWallet(String userId, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.getDeviceMnemonics(String userId, new OstWorkFlowCallbackImpl())
```


### Add a device using mnemonics
A user that has stored their mnemonic phrase can enter it into an appropriate user interface on a new mobile device and authorize that device to be able to control their Brand Tokens.<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter mnemonics: byte array of paper wallet<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void authorizeCurrentDeviceWithMnemonics(String userId, byte[] mnemonics, OstWorkFlowCallback ostWorkFlowCallback)**<br/>
```java
OstSdk.authorizeCurrentDeviceWithMnemonics(userId, mnemonics, new OstWorkFlowCallbackImpl())
```

### getAddDeviceQRCode
Getter method which return QR bitmap image for add device<br/>
Use this methods to generate QR code of current device to be added from authorized device<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; **Bitmap getAddDeviceQRCode(String userId)**<br/>
```java
OstSdk.getAddDeviceQRCode(userId)
```

### Perform QR action
QR codes can be used to encode transaction data for authorizing devices, making purchases via webstores, etc.This method can be  used to process the information scanned off a QR code and act on it.<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter data: JSON object string scanned from QR code<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void performQRAction(String userId, String data, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.performQRAction(userId, data, new OstWorkFlowCallbackImpl())
```

### startPolling
To poll provided entity.<br/>
Polling can be used when any entity is in transition status and desired status update is needed<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter entityId: entity id to be polled<br/>
&nbsp; parameter entityType: entity type to be polled<br/>
&nbsp; parameter successStatus: success status of transition<br/>
&nbsp; parameter failureStatus: failure status of transition<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp;** void startPolling(String userId, String entityId, String entityType, String successStatus, String failureStatus, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.startPolling(userId, entityId, entityType, successStatus, failureStatus, new OstWorkFlowCallbackImpl())
```

### resetPin
To update current Pin with new Pin.<br/><br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter appSalt: Salt provided by app<br/>
&nbsp; parameter currentPin: current pin to be change<br/>
&nbsp; parameter newPin: new pin to be updated<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void resetPin(String userId, String appSalt, String currentPin, String newPin, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.resetPin(userId, appSalt, currentPin, newPin, new OstWorkFlowCallbackImpl())
```

### initiateDeviceRecovery
To authorize the current device by revoking provided device address.<br/><br/>
&nbsp; parameter userId                 user id of recovery user<br/>
&nbsp; parameter passphrase             Struct of current passPhrase<br/>
&nbsp; parameter deviceAddressToRecover Address of device to recover<br/>
&nbsp; parameter workFlowCallback       Work flow interact<br/>
&nbsp; **void initiateDeviceRecovery(String userId, UserPassphrase passphrase, String deviceAddressToRecover, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.initiateDeviceRecovery(userId, passphrase, deviceAddressToRecover, new OstWorkFlowCallbackImpl())
```

### abortDeviceRecovery
If there are any ongoing initiate recovery in process, It will abort that recovery process<br/><br/>
&nbsp; parameter userId           userId of recovery user<br/>
&nbsp; parameter passphrase       A simple struct to transport pin information via app and Sdk.<br/>
&nbsp; parameter workFlowCallback Workflow callback Interact <br/>
&nbsp; **void abortDeviceRecovery(String userId, UserPassphrase passphrase, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.abortDeviceRecovery(userId, passphrase, new OstWorkFlowCallbackImpl())
```

### logoutAllSessions
It will revoke all the sessions associated with provided userId<br/><br/>
&nbsp; parameter userId           user Id whose sessions to revoke<br/>
&nbsp; parameter workFlowCallback Workflow callback interact<br/>
&nbsp; **void logoutAllSessions(String userId, OstWorkFlowCallback workFlowCallback)**
```java
OstSdk.logoutAllSessions(userId, new OstWorkFlowCallbackImpl())
```


## WorkFlow Callbacks
&nbsp; Callbacks to be implemented by application before calling any of the above WorkFlows.

```java
    /**
     * Register device passed as parameter
     *
     * @param apiParams                    Register Device API parameters
     * @param ostDeviceRegisteredInterface To pass response
     */
    void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface)
```

```java
    /**
     * Ask SDK user to verify data to proceed
     *
     * @param ostWorkflowContext       info about workflow type
     * @param ostContextEntity         info about entity
     * @param ostVerifyDataInterface to acknowledge workflow to proceed
     */
    void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface)
```

```java
    /**
     * Pin needed to check the authenticity of the user.
     * Developers should show pin dialog on this callback
     *
     * @param ostWorkflowContext    holds work flow type
     * @param userId                Id of user whose password and pin are needed.
     * @param ostPinAcceptInterface To pass pin
     */
    void getPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);
```

```java
   /**
     * Inform SDK user about invalid pin
     * Developers should show invalid pin error and ask for pin again on this callback
     *
     * @param ostWorkflowContext    holds work flow type
     * @param userId                Id of user whose password and pin are needed.
     * @param ostPinAcceptInterface to pass another pin
     */
    void invalidPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);
```

```java
   /**
     * Inform SDK user that entered pin is validated.
     * Developers should dismiss pin dialog on this callback
     *
     * @param ostWorkflowContext    holds work flow type
     * @param userId Id of user whose pin and password has been validated.
     */
    void pinValidated(OstWorkflowContext ostWorkflowContext, String userId);
```

```java
   /**
     * Inform SDK user about workflow core api call
     * @param ostWorkflowContext info about workflow type
     * @param ostContextEntity info about entity
     */
    void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity)
```

```java
   /**
     * Inform SDK user the the flow is complete
     *
     * @param ostWorkflowContext workflow type
     * @param ostContextEntity status of the flow
     * @see OstContextEntity
     */
    void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity)
```

```java
   /**
     * Inform SDK user that flow is interrupted with errorCode
     * Developers should dismiss pin dialog (if open) on this callback
     *
     * @param ostWorkflowContext workflow type
     * @param ostError reason of interruption
     */
    void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError)
```
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
