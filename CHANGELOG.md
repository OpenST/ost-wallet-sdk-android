# OST Wallet SDK Changelog

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

