# Multiple Stores

This example demonstrates how multple stores can be provided and the decision about which to use can be delayed until runtime based on properties.

To use the filesystem, set `spring.content.storage=fs` in `application.properties`.  This will configure the application use an ephemeral directory as the storage for content.  Check the application logs during startup to establist the storage root: 

```
2020-11-08 20:51:17.584  INFO 91383 --- [           main] o.s.c.fs.io.FileSystemResourceLoader     : Defaulting filesystem root to /var/folders/65/d8zxcwh13sjfrkry92vgs5x80000gr/T/2663684109910933659
``` 

To use s3, set:

```
spring.content.storage=s3
spring.content.aws.accessKeyId=<access-key-id>
spring.content.aws.secretKey=<secret-key>
spring.content.s3.bucket=<bucket>
```

in `application.properties`.  This will configure the application to use an s3 bucket as the storage for content.