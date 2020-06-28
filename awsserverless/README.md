[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)
&nbsp;
#### Using Serverless (AWS lambdas)
###### &nbsp;
---
##### This app sets up an HTTP GET endpoint using Java.
###### Ping the deployed app, and it replies w/ the current time.
##### &nbsp;
---
&nbsp;
> Deployment of AWS Lambda functions is made easy by using the Serverless Framework.
> With the Serverless Framework, you create Lambda functions using our familiar tools, on your local machine then deploy to AWS in seconds.
> This keeps your function’s code and configuration in the Git repo.

&nbsp;
---
---
##### Features
 - Uses Jackson to serialize objects
 - mvn install
 - serverless deploy
 - In serverless.yml file, include this mapping:
&nbsp;
```
  package:
      artifact: target/aws-java-simple-http-endpoint.jar

```
&nbsp;

---
&nbsp;
---
---
##### Callsite Modalities(4)
 - curl
 - open (browser)
 - GET
 - Directly, via a lambda



`For Example`

    curl https://8ctnxl0jg6.execute-api.us-east-1.amazonaws.com/dev/ping
    open https://8ctnxl0jg6.execute-api.us-east-1.amazonaws.com/dev/ping
    GET  https://8ctnxl0jg6.execute-api.us-east-1.amazonaws.com/dev/ping
    Invoke the Lambda function directly, results in log statement
    .
&nbsp;

---
##### Serverless Deploy
The Serverless CLI deploy call:

    serverless deploy

`Console output: `

> Serverless: Creating Stack...
Serverless: Checking Stack create progress...
...
Serverless: Stack create finished...
Serverless: Uploading CloudFormation file to S3...
Serverless: Uploading service .zip file to S3...
Serverless: Updating Stack...
Serverless: Checking Stack update progress...
...
Serverless: Stack update finished...
Service Information
service: aws-java-simple-http-endpoint
stage: dev
region: us-east-1
api keys:
  None
endpoints:
  GET  https://8ctnxl0jg6.execute-api.us-east-1.amazonaws.com/dev/ping
functions:
  aws-java-simple-http-endpoint-dev-currentTime: arn:aws:lambda:us-east-1:XXXXXXX:function:aws-java-simple-http-endpoint
  > dev-currentTime

&nbsp;

---

##### Curl the endpoint:
&nbsp;
    curl https://8ctnxl0jg6.execute-api.us-east-1.amazonaws.com/dev/ping

`Console output:`
> { "message": "Hello, the current time is Thu Jun 04 23:44:37 UTC 2020" }%

 &nbsp;

---

##### Invoke the Lambda function directly, results in log statement.
&nbsp;
    `The CLI call (can be scripted):`

    serverlesslog  == serverless invoke --function currentTime --log

`The response (resembles):`
 >   {
 >     "statusCode": 200,
 >     "body": "{\"message\":\"Hey, current time  Wed Jan 04 23:44:37 UTC 2020\"}",
>      "headers": {
>          "X-Powered-By": "AWS Lambda & Serverless",
>          "Content-Type": "application/json"
>    },
>    "isBase64Encoded": false
>    }    
> START RequestId: XXXXXXX Version: $LATEST
> 2004 23:44:37 <XXXXXXX> INFO  com.serverless.Handler:18 - received: {}
> END RequestId: XXXXXXX
> REPORT RequestId: XXXXXXX	Duration: 0.51 ms	Billed Duration: 100 ms 	> Memory Size: 1024 MB	Max Memory Used: 53 MB

&nbsp;


---

## Scaling

By default, to prevent costs due to potential runaway or recursive functions,
AWS Lambda limits the total concurrent executions across all functions within a given region to 100.

To increase this limit, follow the steps:

http://docs.aws.amazon.com/lambda/latest/dg/concurrent-executions.html#increase-concurrent-executions-limit

---

##### Acknowledgement
&nbsp;
---


> `This app is a derivative of content found in this Guide:`
> https://bit.ly/2MASom9
# awsserverless
