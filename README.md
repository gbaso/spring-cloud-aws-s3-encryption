# Spring Cloud AWS S3 Encryption Sample

Compile the application:

```shell
./mvnw package
```

## Using S3Template

Run with:

```shell
java -jar target/s3template-encryption.jar --method=template <bucket> HELP.md
```

HTTP request:

```http request
PUT /HELP.md HTTP/1.1
Host: <bucket>.s3.eu-central-1.amazonaws.com
amz-sdk-invocation-id: [redacted]
amz-sdk-request: attempt=1; max=4
Authorization: AWS4-HMAC-SHA256 Credential=[redacted]/20250204/eu-central-1/s3/aws4_request, SignedHeaders=amz-sdk-invocation-id;amz-sdk-request;content-length;content-md5;content-type;host;x-amz-content-sha256;x-amz-date;x-amz-meta-x-amz-cek-alg;x-amz-meta-x-amz-iv;x-amz-meta-x-amz-key-v2;x-amz-meta-x-amz-matdesc;x-amz-meta-x-amz-tag-len;x-amz-meta-x-amz-wrap-alg;x-amz-security-token, Signature=[redacted]
Content-Length: 844
Content-MD5: 6lb2XBSWjWZ0ZSJyvw+/Og==
Content-Type: application/octet-stream
Expect: 100-continue
User-Agent: aws-sdk-java/2.29.52 md/io#async md/http#NettyNio ua/2.1 os/Mac_OS_X#15.2 lang/java#21.0.5 md/OpenJDK_64-Bit_Server_VM#21.0.5+11-LTS md/vendor#Eclipse_Adoptium md/en_US cfg/auth-source#sso m/D AmazonS3Encrypt/3.3.0 spring-cloud-aws/3.3.0
x-amz-content-sha256: UNSIGNED-PAYLOAD
X-Amz-Date: 20250204T130455Z
x-amz-meta-x-amz-cek-alg: AES/GCM/NoPadding
x-amz-meta-x-amz-iv: [redacted]
x-amz-meta-x-amz-key-v2: [redacted]
x-amz-meta-x-amz-matdesc: {"aws:x-amz-cek-alg":"AES/GCM/NoPadding"}
x-amz-meta-x-amz-tag-len: 128
x-amz-meta-x-amz-wrap-alg: kms+context
X-Amz-Security-Token: [redacted]

<encrypted body in binary format>
```

```http response
HTTP/1.1 400 Bad Request
x-amz-request-id: [redacted]
x-amz-id-2: [redacted]
Content-Type: application/xml
Transfer-Encoding: chunked
Date: Tue, 04 Feb 2025 13:04:55 GMT
Connection: close
Server: AmazonS3
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Error>
  <Code>BadDigest</Code>
  <Message>The Content-MD5 you specified did not match what we received.</Message>
  <ExpectedDigest>ea56f65c14968d6674652272bf0fbf3a</ExpectedDigest>
  <CalculatedDigest>iE6VMJMWXoHinUXX7H7GWg==</CalculatedDigest>
  <RequestId>[redacted]</RequestId>
  <HostId>[redacted]</HostId>
</Error>
```

The request includes the header `Content-MD5` with value `6lb2XBSWjWZ0ZSJyvw+/Og==`, which is the md5 checksum of the plain-text `HELP.md` file, while S3 expected the checksum of the encrypted payload.

## Using S3Client

Run with:

```shell
java -jar target/s3template-encryption.jar --method=client <bucket> HELP.md
```

HTTP request:

```http request
PUT /HELP.md HTTP/1.1
Host: <bucket>.s3.eu-central-1.amazonaws.com
amz-sdk-invocation-id: [redacted]
amz-sdk-request: attempt=1; max=4
Authorization: AWS4-HMAC-SHA256 Credential=[redacted]/20250204/eu-central-1/s3/aws4_request, SignedHeaders=amz-sdk-invocation-id;amz-sdk-request;content-length;content-type;host;x-amz-content-sha256;x-amz-date;x-amz-meta-x-amz-cek-alg;x-amz-meta-x-amz-iv;x-amz-meta-x-amz-key-v2;x-amz-meta-x-amz-matdesc;x-amz-meta-x-amz-tag-len;x-amz-meta-x-amz-wrap-alg;x-amz-security-token, Signature=[redacted]
Content-Length: 844
Content-Type: application/octet-stream
Expect: 100-continue
User-Agent: aws-sdk-java/2.29.52 md/io#async md/http#NettyNio ua/2.1 os/Mac_OS_X#15.2 lang/java#21.0.5 md/OpenJDK_64-Bit_Server_VM#21.0.5+11-LTS md/vendor#Eclipse_Adoptium md/en_US cfg/auth-source#sso m/D AmazonS3Encrypt/3.3.0 spring-cloud-aws/3.3.0
x-amz-content-sha256: UNSIGNED-PAYLOAD
X-Amz-Date: 20250204T133955Z
x-amz-meta-x-amz-cek-alg: AES/GCM/NoPadding
x-amz-meta-x-amz-iv: [redacted]
x-amz-meta-x-amz-key-v2: [redacted]
x-amz-meta-x-amz-matdesc: {"aws:x-amz-cek-alg":"AES/GCM/NoPadding"}
x-amz-meta-x-amz-tag-len: 128
x-amz-meta-x-amz-wrap-alg: kms+context
X-Amz-Security-Token: [redacted]

<encrypted body in binary format>
```

```http response
HTTP/1.1 200 OK
x-amz-id-2: [redacted]
x-amz-request-id: [redacted]
Date: Tue, 04 Feb 2025 13:39:55 GMT
x-amz-server-side-encryption: AES256
ETag: "[redacted]"
Content-Length: 0
Server: AmazonS3
```

In this case the request does not include `Content-MD5` header, and the upload succeeds.
