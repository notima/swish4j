# Swish Java client library

Compatible with version 2 of the Swish API.

## Prerequisites

### Dependencies

The following bundle must be installed in order to start the bundle:

    mvn:com.google.code.gson/gson/2.8.9
    mvn:org.apache.commons/commons-csv/1.6

### Preparing certificates

In order to use the Swish client library, a client certificate and a ca certificate needs to be retrieved from swish. Certificates for testing using the Merchant Swish Simulator can be retrieved [here](https://developer.swish.nu/documentation/environments#certificates).

The root ca certificate is in `pem` format and needs to be converted to `der` format and loaded into a java key store in order to be used.

Convert to `der` format:

    openssl x509 -outform der -in Swish_TLS_RootCA.pem -out Swish_TLS_RootCA.der

Load into java key store:

    keytool -import -alias your-alias -keystore Swish_TLS_RootCA.jks -file Swish_TLS_RootCA.der


## Usage

### Instantiating a Swish client

Before creating a Swish client, a Keystore instance needs to be created to load the client certificate. For example:

    InputStream certInputStream = this.getClass().getClassLoader().getResourceAsStream(CERTIFICATE_FILE);
    KeyStore keyStore = KeyStore.getInstance("pkcs12");
    keyStore.load(certInputStream, cerificatePassword.toCharArray());

The CERTIFICATE_FILE constant needs to point to the client certificate `.p12` file. When using a test certicate, the password is "swish".

A trust store also needs to be created using the ca certificate keystore created earlier:

    InputStream certificateIS = this.getClass().getClassLoader().getResourceAsStream(CA_CERTIFICATE_FILE);
    KeyStore trustStore = KeyStore.getInstance("JKS");
    trustStore.load(str, caPassword.toCharArray());

A Swish client can then be instantiated:

    return new SwishClient(testMode, keyStore, cerificatePassword, trustStore);

### Create payment request

A payment request object needs to be passed to the Swish client. A payment request could look like the following:

    PaymentRequest request = new PaymentRequest();
    request.setCallbackUrl("https://myfakehost.se/swishcallback.cfm");
    request.setPayeeAlias("1231181189");
    request.setPayerAlias("46731930431");   // Can be omitted when creating M-commerce requests
    request.setAmount("100");
    request.message("Kingston USB Flash Drive 8 GB");

The payment request can then be sent to Swish:

    SwishResponseHeaders response = client.sendPaymentRequest(request, instructionUUID);

The `instructionUUID` parameter needs to be a unique 32 character upper case hex string (e.g. `21264A9B7A1F43AEBB1388A93012942D`). Such a string can be generated using `SwishClient.generateInstructionUUID()`.

### Checking status of payment request

Once a payment request has been created, a payment object can be retrieved:

    SwishPayment payment = client.retrievePayment(response.getLocation());

The `response` object is the object returned from `client.sendPaymentRequest`.