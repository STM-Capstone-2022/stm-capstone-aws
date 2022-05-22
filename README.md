# Alexa IoT Demo

This repository demonstrates an AWS lambda interfacing with an STM32 Discovery Kit.

The repo has a couple Maven submodules:

- **alexa-iot-application** - The Amazon CDK code that defines a CloudFormation stack for the Alexa skill.
- **alexa-iot-worker** - The source code for the AWS Lambda function that drives the Alexa Skill.

## Building the Project

For a first time setup:

- Install the AWS cli online, and then the AWS CDK cli via `npm install -g aws-cdk`
- Make sure to configure your AWS account
- Run `cdk bootstrap` to spawn the CDK stack on your account

Then to build and deploy, follow the following steps:

- `mvn package -pl !alexa-iot-application` to build the application code
- `cdk synth` to Generate the application template and upload assets to the CDK stack
- `cdk deploy` to launch the stack to your account