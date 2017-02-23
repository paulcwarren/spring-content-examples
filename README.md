[![Build Status](https://travis-ci.org/paulcwarren/spring-content-examples.svg?branch=master)](https://travis-ci.org/paulcwarren/spring-content-examples)

# Spring Content Examples

Examples projects showing how to use each Spring Content module.

While each `boot-starter` example does not specify a meta-data store, the standard examples always specify HSQL.


## Spring-Eg-Content-FS
- This example stores content on the local file system, under your temp directory (OS dependent) followed by `/spring-content-CURRENTTIME-INSTANCENUM/`.
- No environment variables are need to run this example. If you desire to change the location of files being stored add the following bean to your class and change the return value.

```java
  @Bean
  public File fileSystemRoot() throws IOException {
  return //MY_DIRECTORY_PATH;
  }
```

## Spring-Eg-Content-Jpa
- This example stores content in a JPA compatible database which in this case is HSQL.
- No environment variables are need to run this example.
- To change the underlying content database the `dataSource()` method must be changed to return a DataSource handler to your database. See `ClaimTestConfig.java` for the HSQL example.

## Spring-Eg-Content-Mongo
- This example stores content in a MongoDB.
- If no environment variables are specified when running this example, we assume you have a MongoDB running locally without username/password.
- You can change the mongoDB host / username / password with the following ENV variables:
  - `spring_eg_content_mongo_host` -> MongoDB host
  - `spring_eg_content_mongo_port` ->  MongoDB host port
  - `spring_eg_content_mongo_username` -> MongoDB username
  - `spring_eg_content_mongo_password` -> MongoDB password

## Spring-Eg-Content-S3
- This example stores content in a S3 Bucket
- The following ENV variables need to be set when running this example:
 - `AWS_BUCKET` -> AWS S3 bucket that has been previously setup for storing content
 - `AWS_REGION` -> AWS Region in which the bucket above is provisioned.

    > Availability Zones:
    - us-gov-west-1
    - us-east-1
    - us-west-1
    - us-west-2
    - eu-west-1
    - eu-central-1
    - ap-south-1
    - ap-southeast-1
    - ap-southeast-2
    - ap-northeast-1
    - ap-northeast-2
    - sa-east-1
    - cn-north-1
  - `AWS_ACCESS_KEY_ID` -> AWS Key ID that has access to the bucket above
  - `AWS_SECRET_KEY` -> AWS Secret Key that corresponds the ID above

## Spring-Eg-Content-Solr
- This example:
  - by default is used in conjunction with the JPA interface as well as HSQL, this can be changed for your own scenario.
  - indexes your content when storing, later allowing for querying.
- The following ENV variables need to be set:
  - Solr Server with Basic Auth (SSL inclusive):
    - `EXAMPLES_AUTH_URL` -> https/http url of solr server including port.
    - `EXAMPLES_USERNAME` -> Username with access to create new entries and run queries.
    - `EXAMPLES_PASSWORD` -> Password for username above.
  - Insecure Solr Server:
    - `EXAMPLES_SOLR_URL` -> http url of solr server including port.
