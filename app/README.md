# Android Demo App

## Introduction

Demo App is representation of how developer can use OstWalletSdk in their application.



### Add uris.xml file
Urls config file is needed to provide endpoints of Mappy Applcation and Ost Platform.</br>
 - Create file "uris.xml" with below template and update the respecteive endpoints urls.
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="base_url_mappy">https://[Mappy Server Endpoint]/api/</string>
    <string name="base_url_ost_platform">https://[Ost Platform Endpoint]/testnet/v2</string>
</resources>
```
Place uris.xml file in <b>app/src/main/res/values/</b>
