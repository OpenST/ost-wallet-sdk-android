# OST JSON APIs

OST JSON APIs are a set of *asynchronous* methods that make API calls to OST Platform servers.

## Table of Contents

- [Before We Begin](#before-we-begin)
- [JSON API Types](#json-api-types)
- [Importing OstJsonApi](#importing-ostjsonapi)
- [OstJsonApi Delegate](#delegate-ostjsonapi)
- [Entity API](#entity-api)
  - [Get Current Device](#get-current-device)
    - [Usage](#usage)
    - [Sample Response](#sample-response)
  - [Get Balance](#get-balance)
    - [Usage](#usage-1)
    - [Sample Response](#sample-response-1)
  - [Get Price Points](#get-price-points)
    - [Usage](#usage-2)
    - [Sample Response](#sample-response-2)
  - [Get Balance And Price Points](#get-balance-and-price-points)
    - [Usage](#usage-3)
    - [Sample Response](#sample-response-3)
  - [Get Pending Recovery](#get-pending-recovery)
    - [Usage](#usage-4)
    - [Sample Response](#sample-response-4)
    - [Sample Error](#sample-error)
- [List API](#list-api)
  - [Get Transactions](#get-transactions)
    - [Usage](#usage-5)
    - [Sample Response](#sample-response-5)
  - [Get Devices](#get-devices)
    - [Usage](#usage-6)
    - [Sample Response](#sample-response-6)


<a id="before-we-begin"></a>
## Before We Begin
- Although it is **NOT RECOMMENDED**, but if your app needs to allow multiple users to login on same device, the app must:
  - ensure to pass the `userId` of the currently **logged-in and authenticated** user.
  - ensure that the user has not logged-out **before** processing/displaying the response.
- App must [initialize](../README.md#vii-initialize-the-wallet-sdk) the sdk <em><b>before</b></em> initiating any JSON API.
- App must perform [setupDevice](../README.md#1-setupdevice) workflow <em><b>before</b></em> initiating any JSON API.
- All `OstJsonApi` methods expect `userId` as first parameter because all requests need to be signed by the user's API key.
- It's always good to check if the device can make API calls by calling `OstSdk.getCurrentDeviceForUserId` method.
  - Any device with status `REGISTERED`, `AUTHORIZING`, `AUTHORIZED`, `RECOVERING` or `REVOKING` can make this API call.


<a id="json-api-types"></a>
## JSON API Types
The JSON APIs can be categorized into 2 groups.
* [Entity API](#entity-api) - The APIs that get entities (e.g. current-device, price-point, balance etc.)
* [List API](#list-api) - The APIs that get list of entities and support pagination (e.g. device list, transactions)


<a id="importing-ostjsonapi"></a>
## Importing OstJsonApi

Use the following code to import `OstSdk`
```
import com.ost.walletsdk.OstSdk;
```
<a id="delegate-ostjsonapi"></a>
## Ost Json Api Delegate
 Developer need to pass object of OstJsonApiDelegate to get response.
 ```java
 class OstJsonApiCallbackImpl implements OstJsonApiCallback {

        @Override
        public void onOstJsonApiSuccess(@Nullable JSONObject data) {
            //Action on success response
        }

        @Override
        public void onOstJsonApiError(@Nonnull OstError err, @Nullable JSONObject data) {
            //Action on failure response
        }
    }
 ```

<a id="entity-api"></a>
## Entity API

<a id="get-current-device"></a>
### Get Current Device
API to get user's current device.
> While the equivalent getter method `OstSdk.getCurrentDeviceForUserId` gives the data stored in SDK's database, 
> this method makes an API call to OST Platform.

<a id="usage"></a>
##### Usage
```java
/*
  Please update userId as per your needs. 
  Since this userId does not belong to your economy, you will get an error if you do not change it.
*/
String userId = "71c59448-ff77-484c-99d8-abea8a419836";

OstJsonApiCallbackImpl ostJsonApiCallback = new OstJsonApiCallbackImpl();

/**
     * Api to get current user device.
     *
     * @param userId User Id of the current logged-in user.
     * @param callback callback where to receive data/error.
     */
OstJsonApi.getCurrentDevice(userId, ostJsonApiCallback);
```

<a id="sample-response"></a>
##### Sample Response
```json
{
  "device": {
    "updated_timestamp": 1566832473,
    "status": "AUTHORIZED",
    "api_signer_address": "0x674d0fc0d044f085a87ed742ea778b55e298b429",
    "linked_address": "0x0000000000000000000000000000000000000001",
    "address": "0x8d92cf567191f07e5c1b487ef422ff684ddf5dd3",
    "user_id": "71c59448-ff77-484c-99d8-abea8a419836"
  },
  "result_type": "device"
}
```

<a id="get-balance"></a>
### Get Balance
API to get user's balance.

<a id="usage-1"></a>
##### Usage
```java
/*
  Please update userId as per your needs. 
  Since this userId does not belong to your economy, you will get an error if you do not change it.
*/
String userId = "71c59448-ff77-484c-99d8-abea8a419836";

OstJsonApiCallbackImpl ostJsonApiCallback = new OstJsonApiCallbackImpl();

/**
     * Api to get user balance. Balance of only current logged-in user can be fetched.
     *
     * @param userId User Id of the current logged-in user.
     * @param callback callback where to receive data/error.
     */
OstJsonApi.getBalance(userId, ostJsonApiCallback);
```

<a id="sample-response-1"></a>
##### Sample Response
```json
{
  "balance": {
    "updated_timestamp": 1566832497,
    "unsettled_debit": "0",
    "available_balance": "10000000",
    "total_balance": "10000000",
    "user_id": "71c59448-ff77-484c-99d8-abea8a419836"
  },
  "result_type": "balance"
}
```

<a id="get-price-points"></a>
### Get Price Point
API to get price-points of token's staking currency (OST or USDC).
> This API call is generally needed to compute the current fiat value to your brand-tokens. 
> E.g. displaying user's balance in fiat.

<a id="usage-2"></a>
##### Usage
```java
/*
  Please update userId as per your needs. 
  Since this userId does not belong to your economy, you will get an error if you do not change it.
*/
String userId = "71c59448-ff77-484c-99d8-abea8a419836";

OstJsonApiCallbackImpl ostJsonApiCallback = new OstJsonApiCallbackImpl();

/**
     * Api to get Price Points.
     *
     * @param userId   User Id of the current logged-in user.
     * @param callback callback where to receive data/error
*/
OstJsonApi.getPricePoints(userId, ostJsonApiCallback);
```

<a id="sample-response-2"></a>
##### Sample Response
```json
{
  "price_point": {
    "USDC": {
      "updated_timestamp": 1566834913,
      "decimals": 18,
      "GBP": 0.8201717727,
      "EUR": 0.9028162679,
      "USD": 1.0025110673
    }
  },
  "result_type": "price_point"
}
```

<a id="get-balance-and-price-points"></a>
### Get Balance And Price Points
This is a convenience method that makes `OstJsonApi.getBalanceForUserId` and `OstJsonApi.getPricePointForUserId` API calls and merges the response.

<a id="usage-3"></a>
##### Usage
```java
/*
  Please update userId as per your needs. 
  Since this userId does not belong to your economy, you will get an error if you do not change it.
*/
String userId = "71c59448-ff77-484c-99d8-abea8a419836";

OstJsonApiCallbackImpl ostJsonApiCallback = new OstJsonApiCallbackImpl();

/**
     * Api to get user balance and Price Points. Balance of only current logged-in user can be fetched.
     *
     * @param userId User Id of the current logged-in user.
     * @param callback callback where to receive data/error.
*/
OstJsonApi.getBalanceWithPricePoints(userId, ostJsonApiCallback);
```

<a id="sample-response-3"></a>
##### Sample Response
```json
{
  "balance": {
    "updated_timestamp": 1566832497,
    "unsettled_debit": "0",
    "available_balance": "10000000",
    "total_balance": "10000000",
    "user_id": "71c59448-ff77-484c-99d8-abea8a419836"
  },
  "price_point": {
    "USDC": {
      "updated_timestamp": 1566834913,
      "decimals": 18,
      "GBP": 0.8201717727,
      "EUR": 0.9028162679,
      "USD": 1.0025110673
    }
  },
  "result_type": "balance"
}
```

<a id="get-pending-recovery"></a>
### Get Pending Recovery
API to get user's pending recovery. A pending recovery is created when the user recovers the device using their PIN.
> This API will respond with `UNPROCESSABLE_ENTITY` API error code when user does not have any recovery in progress.

<a id="usage-4"></a>
##### Usage
```java
/*
  Please update userId as per your needs. 
  Since this userId does not belong to your economy, you will get an error if you do not change it.
*/
String userId = "71c59448-ff77-484c-99d8-abea8a419836";

OstJsonApiCallbackImpl ostJsonApiCallback = new OstJsonApiCallbackImpl();

/**
     * Api to get pending ongoing recovery.
     *
     * @param userId User Id of the current logged-in user.
     * @param callback callback where to receive data/error.
*/

OstJsonApi.getPendingRecovery(userId, new ostJsonApiCallbackImpl);

/* After receiving error for this api request, check for following:
   if ("UNPROCESSABLE_ENTITY".equalIsIgnoreCase(err.internalCode)) {
       // There is no pending recovery
   }
*/
```

<a id="sample-response-4"></a>
##### Sample Response
```json
{
  "devices": [
    {
      "updated_timestamp": 1566902100,
      "status": "REVOKING",
      "api_signer_address": "0x903ad1a1017c14b8e6b0bb1dd32d3f65a8741732",
      "linked_address": "0x73722b0c0a6b6418893737e0ca33dd567e33f6aa",
      "address": "0x629e13063a2aa24e2fb2a49697ef871806071550",
      "user_id": "71c59448-ff77-484c-99d8-abea8a419836"
    },
    {
      "updated_timestamp": 1566902100,
      "status": "RECOVERING",
      "api_signer_address": "0x6f5b1b8df95cbc3bd8d18d6c378cef7c34644729",
      "linked_address": "null",
      "address": "0x33e736a4761bc07ed54b1ceb82e44dfb497f478c",
      "user_id": "71c59448-ff77-484c-99d8-abea8a419836"
    }
  ],
  "result_type": "devices"
}
```

<a id="sample-error"></a>
##### Sample Error
The `getPendingRecoveryForUserId` API will respond with `UNPROCESSABLE_ENTITY` API error code when user does not have any recovery in progress.
```json
{
  "api_error": {
    "internal_id": "***********",
    "error_data": [],
    "msg": "Initiate Recovery request for user not found.",
    "code": "UNPROCESSABLE_ENTITY"
  },
  "is_api_error": 1,
  "error_message": "OST Platform Api returned error.",
  "internal_error_code": "***********",
  "error_code": "API_RESPONSE_ERROR"
}
```

<a id="list-api"></a>
## List API
All `List` APIs support pagination. The response of all `List` APIs has an extra attribute `meta`.
To determine if next page is available, the app should look at `meta["next_page_payload"]`. 
If `meta["next_page_payload"]` is an empty object (`{}`), next page is not available.

<a id="get-transactions"></a>
### Get Transactions
API to get user's transactions.

<a id="usage-5"></a>
##### Usage
```java
/*
  Please update userId as per your needs. 
  Since this userId does not belong to your economy, you will get an error if you do not change it.
*/
String userId = "71c59448-ff77-484c-99d8-abea8a419836";

JSONObject nextPagePayload = null;

OstJsonApiCallbackImpl ostJsonApiCallback = new OstJsonApiCallbackImpl();

 /**
     * Api to get user transactions. Transactions of only current logged-in user can be fetched.
     *
     * @param userId User Id of the current logged-in user.
     * @param requestPayload request payload. Such as next-page payload, filters etc.
     * @param callback callback where to receive data/error.
*/
OstJsonApi.getTransactions(userId, nextPagePayload, ostJsonApiCallback);

/* After receiving data for this api request, check for following:
    JSONObject dataJSONObject  =  parseJSONData(responseData);
    JSONObject meta = dataJSONObject.optJSONObject("meta");
    if (null != meta) {
      nextPagePayload = meta.optJSONObject("next_page_payload");
    }
*/
```

<a id="sample-response-5"></a>
##### Sample Response
Please refer to the [Transactions Object](https://dev.ost.com/platform/docs/api/#transactions) for a detailed description.
```json
{
  "meta": {
    "total_no": 14,
    "next_page_payload": {
      "pagination_identifier": "*****************************************************"
    }
  },
  "transactions": [
    {
      "meta_property": {
        "details": "Awesome Post",
        "type": "user_to_user",
        "name": "Like"
      },
      "rule_name": "Direct Transfer",
      "block_timestamp": 1566843589,
      "block_confirmation": 969,
      "transaction_fee": "94234000000000",
      "gas_price": "1000000000",
      "nonce": 613,
      "from": "0x6ecbfdb2ebac8669c85d61dd028e698fd6403589",
      "id": "4efa1b45-8890-4978-a5f4-8f9368044852",
      "transfers": [
        {
          "kind": "transfer",
          "amount": "200000",
          "to_user_id": "a87fdd7f-4ce5-40e2-917c-d80a8828ba62",
          "to": "0xb29d32936280e8f05a5954bf9a60b941864a3442",
          "from_user_id": "71c59448-ff77-484c-99d8-abea8a419836",
          "from": "0xbf3df93b15c6933177237d9ed8400a2f41c8b8a9"
        }
      ],
      "block_number": 3581559,
      "updated_timestamp": 1566843589,
      "status": "SUCCESS",
      "gas_used": 94234,
      "value": "0",
      "to": "0xbf3df93b15c6933177237d9ed8400a2f41c8b8a9",
      "transaction_hash": "0xee8033f9ea7e9bf2d74435f0b6cc172d9378670e513a2b07cd855ef7e41dd2ad"
    },
    {
      "meta_property": {
        "details": "Nice Pic",
        "type": "user_to_user",
        "name": "Fave"
      },
      "rule_name": "Direct Transfer",
      "block_timestamp": 1566843547,
      "block_confirmation": 983,
      "transaction_fee": "109170000000000",
      "gas_price": "1000000000",
      "nonce": 612,
      "from": "0x6ecbfdb2ebac8669c85d61dd028e698fd6403589",
      "id": "7980ee91-7cf1-449c-bbaf-5074c2ba6b29",
      "transfers": [
        {
          "kind": "transfer",
          "amount": "1600000",
          "to_user_id": "a87fdd7f-4ce5-40e2-917c-d80a8828ba62",
          "to": "0xb29d32936280e8f05a5954bf9a60b941864a3442",
          "from_user_id": "71c59448-ff77-484c-99d8-abea8a419836",
          "from": "0xbf3df93b15c6933177237d9ed8400a2f41c8b8a9"
        }
      ],
      "block_number": 3581545,
      "updated_timestamp": 1566843549,
      "status": "SUCCESS",
      "gas_used": 109170,
      "value": "0",
      "to": "0xbf3df93b15c6933177237d9ed8400a2f41c8b8a9",
      "transaction_hash": "0x3e3bb3e25ab3a5123d1eaf20e1c31ab88bd56500c5cdfd2e32025c4df32735b3"
    },
    ...
    ...
  ],
  "result_type": "transactions"
}
```

<a id="get-devices"></a>
### Get Devices
API to get user's devices.

<a id="usage-6"></a>
##### Usage
```Swift
/*
  Please update userId as per your needs. 
  Since this userId does not belong to your economy, you will get an error if you do not change it.
*/
String userId = "71c59448-ff77-484c-99d8-abea8a419836";
JSONObject nextPagePayload = null;

OstJsonApiCallbackImpl ostJsonApiCallback = new OstJsonApiCallbackImpl();

/**
     * Api to get Device list. Device list of only current logged-in user can be fetched.
     *
     * @param userId User Id of the current logged-in user.
     * @param requestPayload request payload. Such as next-page payload, filters etc.
     * @param callback callback where to receive data/error.
*/
OstJsonApi.getDeviceList(userId, nextPagePayload, ostJsonApiCallback);

/* After receiving data for this api request, check for following:
    JSONObject dataJSONObject = parseJSONData(responseData);
    JSONObject meta = dataJSONObject.optJSONObject("meta");
    if (null != meta) {
      nextPagePayload = meta.optJSONObject("next_page_payload");
    }
*/
```

<a id="sample-response-6"></a>
##### Sample Response
```json
{
  "meta": {
    "next_page_payload": {}
  },
  "devices": [
    {
      "updated_timestamp": 1566832473,
      "status": "AUTHORIZED",
      "api_signer_address": "0x674d0fc0d044f085a87ed742ea778b55e298b429",
      "linked_address": "0x73722b0c0a6b6418893737e0ca33dd567e33f6aa",
      "address": "0x8d92cf567191f07e5c1b487ef422ff684ddf5dd3",
      "user_id": "71c59448-ff77-484c-99d8-abea8a419836"
    },
    {
      "updated_timestamp": 1566839512,
      "status": "AUTHORIZED",
      "api_signer_address": "0x2e12c4f6a27f7bdf8e58e628ec29bb4ce49c315e",
      "linked_address": "0x0000000000000000000000000000000000000001",
      "address": "0x73722b0c0a6b6418893737e0ca33dd567e33f6aa",
      "user_id": "71c59448-ff77-484c-99d8-abea8a419836"
    }
  ],
  "result_type": "devices"
}
```
