# OST Wallet SDK Changelog

## Version 2.4.1
### Feature
* User can authorize external session by scanning QR-Code.
* User can pass QR-Code payload to perform QR-Code actions without opening Scanner in OstWalletUI.
This functionality is available for `scanQRCodeToAuthorizeSession`, `scanQRCodeToExecuteTransaction`, `scanQRCodeToAuthorizeDevice`.

## Version 2.4.0
### Feature
* `getRedeemableSkus` and `getRedeemableSkuDetails` apis added in `OstJsonApi`.

## Version 2.3.8
### Changes:
* Reduced recovery key generation time substantially by leveraging on NDK.

### Bug Fix:
* In OstWalletSDK UI workflows progress bar crashes in background.

## Version 2.3.7
### Bug Fix:
* Inaccurate error is thrown when application runs out of memory during recover device workflow.

## Version 2.3.6
### Changes:
* Added support for custom loader for OstWalletSDK UI workflows.
* OstWalletSDK now uses custom annotations to provide AndroidX compatibility.
### Bug Fix:
* Rectified OST_PLATFORM_ERROR error message.

## Version 2.3.5
### Security Enhancements:
* Use of FLAG_SECURE flag to protect show mnemonices view against screen recording and screen shotting.
* Use of filterTouchesWhenObscured security flag in base view to protects against tapjacking attacks.

## Version 2.3.4
### Bug Fixes:
* Device list inconsistency fix in manage devices.
* User entity current device caching fix.
* Converted Toast error message of enter mnemonics view to inline error message

## Version 2.3.3
### Changes:
* Proguard usage to remove verbose and debug logs.

## Version 2.3.2
### Bug Fixes:
* Fixed a bug where some android phones were not able to provide pin.

## Version 2.3.1
### Feature:
* OstWalletUI now supports
    - get add device QR-Code
    - scan QR-Code to authorize device
    - scan QR-Code to execute transaction
    - authorize current device with mnemonics
* Api provided to fetch current device from OstPlatform.
* Now supports getting active sessions from Sdk.

## Version 2.3.0
### Feature:
* OstWalletSdk now contains UI.
* UI components can be modified.
* Languages for UI workflow components can be modified.
* OstWalletUI now supports
    - activate user
    - create session
    - get device mnemonics
    - revoke device
    - reset pin
    - initiate device recovery
    - abort device recovery
    - update biometric preference

## Version 2.2.2
### Bug Fix:
* Crash fixes in OstWallet

### Security Enhancements
* Trustkit reinitialization check

## Version 2.2.1
### Bug Fix:
* Add `No Network Access` error to OstApiError

### Security Enhancements
* Implemented public-key pinning for api.ost.com

## Version v2.2.0
### Changes: 
* Added Multi Currency Feature which allows developers to specify fiat-currency at runtime while executing a transaction.
* Added OstJsonApi that allows developers to fetch data from Ost Platform. Please see README.MD for supported Api(s).

## Version 2.1.0
### Changes: 
* Biometric preferences are now saved in the SDK
* Remove hard-coding of OST as the value token that backs Brand Tokens 
* Now supports device access revocation via API

## Version 2.0.1

### Changes:
* Added CHANGELOG.md
* Removed OstBaseWorkFlow.loadCurrentDevice method and changed it's usage in OstResetPin
* Removed OstBaseWorkFlow.loadUser method and changed it's usage in OstResetPin
* Removed OstBaseWorkFlow.loadToken method
* Removed OstBaseWorkFlow(String userId, Handler handler, OstWorkFlowCallback callback) constructor
* Removed unused method OstUser.sign() 
* Removed OstSdkCrypto class & OstCrypto interface
* Removed utils.KeyGenProcess class & KeyGenProcessTest test-case
* Use a deterministic password along with Mnemonics to generate keys. Using a deterministic password not only increases security, but also ensures that no two users can accidentally generate the same key
* `USE_SEED_PASSWORD` configuration added to support backwards compatibility with v2.0.0

