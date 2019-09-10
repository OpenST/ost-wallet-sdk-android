# Public Key Pinning Using TrustKit
OstSdk uses [TrustKit v1.1.2](https://github.com/datatheorem/TrustKit-Android/tree/1.1.2) for public key pinning. App can have single instance of TrustKit at a time.</br>
For App to use TrustKit they can initialize TrustKit with their pinning policy.</br>
**Note:** App have to make sure they initialize TrustKit before OstSdk initialization.

## TrustKit usage
Deploying SSL pinning in the App requires initializing TrustKit with a pinning policy (domains, pins, and additional settings). The policy is wrapped in the official [Android N Network Security Configuration](https://developer.android.com/training/articles/security-config.html) </br>

App have to define its pinning policy in *network_security_config* file (Don't use *ost_network_security_config* as file name).</br>
App also have to add pinning policy of OstSdk.
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
## Initializing TrustKit with the Pinning Policy

The path to the XML policy should be specified [in the App's manifest](https://developer.android.com/training/articles/security-config.html#manifest) in order to enable it as the App's [Network Security Configuration](https://developer.android.com/training/articles/security-config.html) on Android N.</br>
To resolve duplicate networkSecurityConfig error, App should add *tools:replace="android:networkSecurityConfig"* in mainfest:

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

Then, TrustKit should be initialized:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.OnCreate(savedInstanceState);

  // Using the default path - res/xml/network_security_config.xml
  TrustKit.initializeWithNetworkSecurityConfiguration(this);

  // OR using a resource (TrustKit can't be initialized twice)
  TrustKit.initializeWithNetworkSecurityConfiguration(this, R.xml.network_security_config);
  
  // String BASE_URL = <OstPlatform Url>
  // Initalize OstSdk  
  OstWalletUI.initialize(getApplicationContext(), BASE_URL);
}
