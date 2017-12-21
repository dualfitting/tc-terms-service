# ApTermsMicroservice.DefaultApi

All URIs are relative to *http://api.topcoder.com/v3*

Method | HTTP request | Description
------------- | ------------- | -------------
[**agreeTermsOfUse**](DefaultApi.md#agreeTermsOfUse) | **POST** /terms/{termsOfUseId}/agree | 
[**docusignCallback**](DefaultApi.md#docusignCallback) | **POST** /terms/docusignCallback | 
[**getDocusignViewURL**](DefaultApi.md#getDocusignViewURL) | **POST** /terms/docusign/viewURL | 
[**getTermsOfUse**](DefaultApi.md#getTermsOfUse) | **GET** /terms/detail/{termsOfUseId} | 


<a name="agreeTermsOfUse"></a>
# **agreeTermsOfUse**
> NullResponse agreeTermsOfUse(termsOfUseId)



Agree terms of use

### Example
```javascript
var ApTermsMicroservice = require('ap_terms_microservice');
var defaultClient = ApTermsMicroservice.ApiClient.default;

// Configure API key authorization: bearer
var bearer = defaultClient.authentications['bearer'];
bearer.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//bearer.apiKeyPrefix = 'Token';

var apiInstance = new ApTermsMicroservice.DefaultApi();

var termsOfUseId = 3.4; // Number | the terms of use id


var callback = function(error, data, response) {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
};
apiInstance.agreeTermsOfUse(termsOfUseId, callback);
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **termsOfUseId** | **Number**| the terms of use id | 

### Return type

[**NullResponse**](NullResponse.md)

### Authorization

[bearer](../README.md#bearer)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="docusignCallback"></a>
# **docusignCallback**
> InlineResponse2002 docusignCallback(body)



docusign callback

### Example
```javascript
var ApTermsMicroservice = require('ap_terms_microservice');
var defaultClient = ApTermsMicroservice.ApiClient.default;

// Configure API key authorization: bearer
var bearer = defaultClient.authentications['bearer'];
bearer.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//bearer.apiKeyPrefix = 'Token';

var apiInstance = new ApTermsMicroservice.DefaultApi();

var body = new ApTermsMicroservice.Body1(); // Body1 | The docusign call back param


var callback = function(error, data, response) {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
};
apiInstance.docusignCallback(body, callback);
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Body1**](Body1.md)| The docusign call back param | 

### Return type

[**InlineResponse2002**](InlineResponse2002.md)

### Authorization

[bearer](../README.md#bearer)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="getDocusignViewURL"></a>
# **getDocusignViewURL**
> InlineResponse2001 getDocusignViewURL(body)



Get docusign view url

### Example
```javascript
var ApTermsMicroservice = require('ap_terms_microservice');
var defaultClient = ApTermsMicroservice.ApiClient.default;

// Configure API key authorization: bearer
var bearer = defaultClient.authentications['bearer'];
bearer.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//bearer.apiKeyPrefix = 'Token';

var apiInstance = new ApTermsMicroservice.DefaultApi();

var body = new ApTermsMicroservice.Body(); // Body | The docusign param


var callback = function(error, data, response) {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
};
apiInstance.getDocusignViewURL(body, callback);
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Body**](Body.md)| The docusign param | 

### Return type

[**InlineResponse2001**](InlineResponse2001.md)

### Authorization

[bearer](../README.md#bearer)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="getTermsOfUse"></a>
# **getTermsOfUse**
> InlineResponse200 getTermsOfUse(termsOfUseId, opts)



Get terms of use

### Example
```javascript
var ApTermsMicroservice = require('ap_terms_microservice');
var defaultClient = ApTermsMicroservice.ApiClient.default;

// Configure API key authorization: bearer
var bearer = defaultClient.authentications['bearer'];
bearer.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//bearer.apiKeyPrefix = 'Token';

var apiInstance = new ApTermsMicroservice.DefaultApi();

var termsOfUseId = 3.4; // Number | the terms of use id

var opts = { 
  'noAuth': true // Boolean | a bool indicates no authentication required, default to false
};

var callback = function(error, data, response) {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
};
apiInstance.getTermsOfUse(termsOfUseId, opts, callback);
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **termsOfUseId** | **Number**| the terms of use id | 
 **noAuth** | **Boolean**| a bool indicates no authentication required, default to false | [optional] 

### Return type

[**InlineResponse200**](InlineResponse200.md)

### Authorization

[bearer](../README.md#bearer)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

