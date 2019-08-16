# OST Wallet SDK Changelog

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

