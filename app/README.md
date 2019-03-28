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


## Demo-App: Mappy Server Api Specifications 

### Create User Api Call

| Details      |            |
|--------------|------------|
| Request Type | **POST**   |
| End Point    | /api/users |



| Parameter     | Description                                         |
|---------------|-----------------------------------------------------|
| description   | Description set by the use. Can be empty.           | 
| mobile_number | 10 digit mobile using which user has signed-up.     |
| username      | User's username                                     |



**Response Body:**
```
{
  "created_at": "2019-03-27T18:44:09.348Z",
  "_id": "5c9bc479832e8535706d9d73",
  "token_id": "1211",
  "token_holder_address": null,
  "device_manager_address": null,
  "status": "CREATED",
  "updated_timestamp": "1553712249",
  "app_user_id": "5c9bc478832e8535706d9d72",
  "user_id": "57b12930-cd52-457e-9168-a21b5a226bd5",
  "user_pin_salt": "tuna recall exotic awful embody destroy coach task gorilla runway dry film"
}
```

### Validate User Api Call (Login)

| Details      |                                  |
|--------------|----------------------------------|
| Request Type | **POST**                         |
| End Point    | /api/users/validate/             |

| Parameter     | Description                                     |
|---------------|-------------------------------------------------|
| mobile_number | 10 digit mobile using which user has signed-up. |
| username      | User's username                                 |

**Response Body:** User entity returned by OST Platfrom along with user_pin_salt & app_user_id
```
{
  "created_at": "2019-03-27T18:46:03.527Z",
  "_id": "5c9bc479832e8535706d9d73",
  "token_id": "1211",
  "token_holder_address": "0xa45e56d6d2859b9fb8b884daec90272a79b12db2",
  "device_manager_address": "0xcc599b64b83bf40aab84600c1ed4b257cb790082",
  "status": "ACTIVATED",
  "updated_timestamp": "1553712430",
  "app_user_id": "5c9bc4ea832e8535706d9d75",
  "user_id": "b94b5fbf-cba2-425b-8cca-328cd51daf62",
  "user_pin_salt": "churn antique charge guard crisp duty mercy quote journey chicken answer blouse",
}
```

### List Users Api Call
| Details      |            |
|--------------|------------|
| Request Type | **GET**    |
| End Point    | /api/users |

**Response Body:** Array of users entity *wrapped* inside data along with success flag
```
{
  "success": true,
  "result_type": "users",
  "users": [
    {
      "created_at": "2019-03-27T18:47:26.287Z",
      "_id": "5c9bc53e832e8535706d9d78",
      "username": "chirs",
      "user_display_name": "Chirs",
      "mobile_number": "3000000003",
      "description": "description",
      "ost_user_id": "342a64d8-9d46-46a0-b74b-4e125fa40498"
    },
    {
      "created_at": "2019-03-27T18:46:02.699Z",
      "_id": "5c9bc4ea832e8535706d9d75",
      "username": "bob",
      "user_display_name": "Bob",
      "mobile_number": "2000000002",
      "description": "description",
      "ost_user_id": "b94b5fbf-cba2-425b-8cca-328cd51daf62",
      "token_holder_address": "0xa45e56d6d2859b9fb8b884daec90272a79b12db2"
    },
    {
      "created_at": "2019-03-27T18:44:08.063Z",
      "_id": "5c9bc478832e8535706d9d72",
      "username": "alice",
      "user_display_name": "Alice",
      "mobile_number": "1000000001",
      "description": "description",
      "ost_user_id": "57b12930-cd52-457e-9168-a21b5a226bd5"
    }
  ]
}
```




### Register Device Api Call
| Details      |                                  |
|--------------|----------------------------------|
| Request Type | **POST**                         |
| End Point    | /api/users/**:app_user_id**/devices/ |

**Parameters**: As given by Sdk. These parameters are be used to create device using server side sdk.

**Response Body:** Device Entity returned by OST Platform

```
{
  "createdAt": "2019-03-27T18:47:28.985Z",
  "_id": "5c9bc479832e8535706d9d73",
  "user_id": "b94b5fbf-cba2-425b-8cca-328cd51daf62",
  "address": "0x56aE1C43fE97c2352100F986e8cA8d1cFeCF8EFB",
  "api_signer_address": "0xfe06c88bE1042901b9FddBb73569DACF51241707",
  "status": "REGISTERED",
  "updated_timestamp": "1553712448",
  "app_user_id": "5c9bc53e832e8535706d9d78",
}
```
