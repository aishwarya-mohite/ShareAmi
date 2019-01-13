# shareSmi-generator
Used to share list of AMI's from one region of one  AWS account to other regions of same account and also to multiple regions of other AWS account

## How to use this utility ?
### How to build ?
Execute build.sh file to build this utility which will generate fat jar named shareAmi-generator-0.0.1.jar. You can use this jar to share one or many AMI's accross regions and aws accounts

### How to run ?
Use following command to run utility jar

java -jar shareAmi-generator-0.0.1.jar --input-json-file-path=/tmp/input.json
After successful execution of above command Ami's listed in input.json will be shared to regions in Aws accounts.
