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

Add the jitpack.io repository to your project.
```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}       
```
Apply maven plugin in app module build.gradle by adding below lines.<br/>
```
apply plugin: 'maven' 
group = 'com.github.ostdotcom'
```

Add the OST SDK as a dependency in app module build.gradle<br/>
```
dependencies {
        implementation 'com.github.ostdotcom:ost-wallet-sdk-android:2.2.2'
}
```
Then sync you dependencies through gradle<br/>
**Note**: Gradle sync might fail for the first time due to build time. Please retry if sync issue happen.


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

SDK initialization should happen before calling any other workflow. To initialize the SDK, we need to call init workflow of wallet SDK. It initializes all the required instances and run db migrations.

Recommended location to call init() is in Application sub-class.<br/><br/>
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
This workflow needs `userId` and `tokenId` so setupDevice may be called after the user logs in to the application. Using a mapping between userId in OST Platform and the app user, you have access to userId and tokenId.

If the user is logged in, then setupDevice should be called every time the app launches, this ensures that the current device is registered before communicating with OST Platform  server.<br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter tokenId: Id assigned by Ost to token<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void setupDevice(String userId, String tokenId, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.setupDevice(userId, tokenId, new OstWorkFlowCallbackImpl());
```

### Activate the user
User activation refers to the deployment of smart-contracts that form the user's Brand Token wallet. An activated user can engage with a Brand Token economy.
<br/><br/>
**Parameters**<br/>
&nbsp; parameter UserPassphrase: object which will contain user Id, user pin and passphrasePrefix<br/>
&nbsp; parameter expiresAfterInSecs: session key expiry time<br/>
&nbsp; parameter spendingLimit: spending limit once in a transaction of session<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void activateUser(UserPassphrase passphrase, long expiresAfterInSecs, String spendingLimit, OstWorkFlowCallback callback)**<br/>

```java
UserPassphrase userPassPhrase = new UserPassphrase(userId, pin, passphrasePrefix)
OstSdk.activateUser(userPassPhrase, expiresAfterInSecs, spendingLimit, new OstWorkFlowCallbackImpl())
```

### Authorize a session
A session is a period of time during which a `sessionKey` is authorized to sign transactions under a pre-set limit on behalf of the user.
The device manager, which controls the tokens, authorizes sessions.
 <br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter expiresAfterInSecs: sessions key expiry time<br/>
&nbsp; parameter spendingLimit: spending limit once in a transaction of session<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void addSession(String userId, long expireAfterInSecs, String spendingLimit, OstWorkFlowCallback workFlowCallback)**<br/>

```java
OstSdk.addSession(userId, expireAfterInSecs, spendingLimit, new OstWorkFlowCallbackImpl())

```
### Execute a transaction
A transaction where Brand Tokens are transferred from a user to another actor within the Brand Token economy are signed using `sessionKey` if there is an active session. In the absence of an active session, a new session is authorized.<br/><br/>

**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter tokenHolderAddresses: Token holder addresses of amount receiver<br/>
&nbsp; parameter amounts: Amounts corresponding to tokenHolderAddresses to be transfered<br/>
&nbsp; parameter ruleName: Rule name to be executed<br/>
&nbsp; parameter meta: meta data of transaction to be associated<br/>
Example:-
```json
                           {"name": "transaction name",
                           "type": "user-to-user",
                           "details": "like"}
```
&nbsp; parameter options: Map containing options of transactions<br/>
Example:-
```json
                           {"currency_code": "USD",
                           "wait_for_finalization": true}
```
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void executeTransaction(String userId, String tokenId, List tokenHolderAddresses, List amounts, String ruleName, Map meta, Map options, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.executeTransaction(userId, tokenHolderAddresses, amounts, ruleName, meta, options, new OstWorkFlowCallbackImpl())
````

### Get Mnemonic Phrase
The mnemonic phrase represents a human-readable way to authorize a new device. This phrase is 12 words long.
 <br/><br/>
**Parameters**<br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void getDeviceMnemonics(String userId, OstWorkFlowCallback workFlowCallback)**<br/>
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

### Generate a QR Code
A developer can use this method to generate a QR code that displays the information pertinent to the mobile device it is generated on. Scanning this QR code with an authorized mobile device will result in the new device being authorized.

<br/><br/>
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

### Reset a User's PIN
The user's PIN is set when activating the user. This method supports re-setting a PIN and re-creating the recoveryOwner. <br/><br/>
&nbsp; parameter userId: Ost User id<br/>
&nbsp; parameter appSalt: Salt provided by app<br/>
&nbsp; parameter currentPin: current pin to be change<br/>
&nbsp; parameter newPin: new pin to be updated<br/>
&nbsp; parameter workFlowCallback: callback implementation object for application communication <br/>
&nbsp; **void resetPin(String userId, String appSalt, String currentPin, String newPin, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.resetPin(userId, appSalt, currentPin, newPin, new OstWorkFlowCallbackImpl())
```

### Initialize Recovery
A user can control their Brand Tokens using their authorized devices. If they lose their authorized device, they can recover access to their BrandTokens by authorizing a new device via the recovery process .<br/><br/>
&nbsp; parameter userId                 user id of recovery user<br/>
&nbsp; parameter passphrase             Struct of current passPhrase<br/>
&nbsp; parameter deviceAddressToRecover Address of device to recover<br/>
&nbsp; parameter workFlowCallback       Work flow interact<br/>
&nbsp; **void initiateDeviceRecovery(String userId, UserPassphrase passphrase, String deviceAddressToRecover, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.initiateDeviceRecovery(userId, passphrase, deviceAddressToRecover, new OstWorkFlowCallbackImpl())
```

### Abort Device Recovery
To abort initiated device recovery.<br/><br/>
&nbsp; parameter userId           userId of recovery user<br/>
&nbsp; parameter passphrase       A simple struct to transport pin information via app and Sdk.<br/>
&nbsp; parameter workFlowCallback Workflow callback Interact <br/>
&nbsp; **void abortDeviceRecovery(String userId, UserPassphrase passphrase, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.abortDeviceRecovery(userId, passphrase, new OstWorkFlowCallbackImpl())
```

### Update Biometric Preference
This method can be used to enable or disable the biometric.<br/><br/>
&nbsp; parameter userId           userId of user<br/>
&nbsp; parameter enable           A flag to enable or disable user biometric preference.<br/>
&nbsp; parameter workFlowCallback Workflow callback Interact <br/>
&nbsp; **void updateBiometricPreference(String userId, boolean enable, OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.updateBiometricPreference(userId, enable, new OstWorkFlowCallbackImpl())
```

### Revoke Device
To revoke device access.<br/><br/>
&nbsp; parameter userId           userId of user<br/>
&nbsp; parameter deviceAddress    Address of device to revoke<br/>
&nbsp; parameter workFlowCallback Workflow callback Interact <br/>
&nbsp; **void revokeDevice(String userId, String  deviceAddress , OstWorkFlowCallback workFlowCallback)**<br/>
```java
OstSdk.revokeDevice(userId, deviceAddress, new OstWorkFlowCallbackImpl())
```

### Log out
It will revoke all the sessions associated with provided userId<br/><br/>
&nbsp; parameter userId           user Id whose sessions to revoke<br/>
&nbsp; parameter workFlowCallback Workflow callback interact<br/>
&nbsp; **void logoutAllSessions(String userId, OstWorkFlowCallback workFlowCallback)**
```java
OstSdk.logoutAllSessions(userId, new OstWorkFlowCallbackImpl())
```

### Get User
To get User Entity<br/><br/>
&nbsp; parameter userId           user Id whose entity to retrieve<br/>
&nbsp; **OstUser getUser(String userId)**
```java
OstSdk.getUser(userId)
```

### Get Token
To get Token Entity<br/><br/>
&nbsp; parameter tokenId           token Id whose entity to retrieve<br/>
&nbsp; **OstToken getToken(String tokenId)**
```java
OstSdk.getToken(tokenId)
```

### Get Biometric preference
To check whether biometric of provide userId is enabled for this device or not<br/><br/>
&nbsp; parameter userId           user Id whose biometric config to retrieve<br/>
&nbsp; **boolean isBiometricEnabled(String userId)**
```java
OstSdk.isBiometricEnabled(userId)
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


| Argument | Description |
|---|---|
| **apiParams** <br> **JSONObject**	|	Device information for registration	|
| **ostDeviceRegisteredInterface** <br> **OstDeviceRegisteredInterface**	| **OstDeviceRegisteredInterface.deviceRegistered(JSONObject newDeviceEntity )** should be called to pass the newly created device entity back to SDK. <br>In case data is not verified the current workflow should be canceled by developer by calling **OstDeviceRegisteredInterface.cancelFlow()**  |


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
| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	| Information about the current workflow during which this callback will be called	|
| **ostContextEntity** <br> **OstContextEntity**	| Information about the entity |
| **ostVerifyDataInterface** <br> **OstVerifyDataInterface**	| **ostVerifyDataInterface.dataVerified()** should be called if the data is verified successfully. <br>In case data is not verified the current workflow should be canceled by developer by calling **ostVerifyDataInterface.cancelFlow()** |




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

| Argument | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user |
| **ostPinAcceptInterface** <br> **OstPinAcceptInterface**	| **ostPinAcceptInterface.pinEntered()** should be called to pass the PIN back to SDK. <br> For some reason if the developer wants to cancel the current workflow they can do it by calling **ostPinAcceptInterface.cancelFlow()** |


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
| Argument | Description |
|---|---|
| **userId** <br> **String**	|	Unique identifier of the user	|
| **ostPinAcceptInterface** <br> **OstPinAcceptInterface**	| **ostPinAcceptInterface.pinEntered()** should be called to again pass the PIN back to SDK. <br> For some reason if the developer wants to cancel the current workflow they can do it by calling **ostPinAcceptInterface.cancelFlow()**  |



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

| Argument | Description |
|---|---|
| **userId** <br> **String**	| Unique identifier of the user |



```java
   /**
     * Inform SDK user about workflow core api call
     * @param ostWorkflowContext info about workflow type
     * @param ostContextEntity info about entity
     */
    void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity)
```

| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	| Information about the workflow	|
| **ostContextEntity** <br> **OstContextEntity**	| Information about the entity |



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
| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	|	Information about the workflow	|
| **ostContextEntity** <br> **OstContextEntity**	| Information about the entity |




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


| Argument | Description |
|---|---|
| **ostWorkflowContext** <br> **OstWorkflowContext**	| Information about the workflow |
| **ostError** <br> **OstError**	| ostError object will have details about the error that interrupted the flow |

## OST JSON APIs

### User Balance

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

### Price Points

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

### Balance With Price Points

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

### Transactions

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

### Pending Recovery

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

## Application development supporting doc
 
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

### Get Entity status updates
To get real time updates of entities like ongoing activation Or transactions, server side sdk's [WebHooks](https://dev.ost.com/platform/docs/api/#webhooks) services can be used.

### Wallet Check on App launch
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
### Balance calculation
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

## OstWalletUI
You can use available UI from OstSdk. Please refer [OstWalletUI](ostsdk/src/main/java/com/ost/walletsdk/ui/README-UI.md) - `Beta Version`

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
