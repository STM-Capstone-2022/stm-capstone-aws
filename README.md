# Alexa IoT Demo

This repository demonstrates an AWS lambda interfacing with an STM32 Discovery Kit.

The repo has a couple Maven submodules:

- **alexa-iot-stack** - The Amazon CDK code that defines a CloudFormation stack for the Alexa skill.
- **alexa-iot-worker** - The source code for the AWS Lambda function that drives the Alexa Skill.