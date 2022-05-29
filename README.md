# Alexa IoT Demo

This repository demonstrates an AWS lambda interfacing with an STM32 Discovery Kit.

The repo has a couple Maven submodules:

- **alexa-iot-application** - The Amazon CDK code that defines a CloudFormation stack for the Alexa skill.
- **alexa-iot-worker** - The source code for the AWS Lambda function that drives the Alexa Skill.

## Developer Tools and Tool Setup

Developers need to install a few AWS tools and dev kits to set up the project:

- AWS command line interface: <https://aws.amazon.com/cli/>
- Node package manager to install CDK: <https://www.npmjs.com/>
    - Or on Mac: `brew install npm`
    - Or on Debian: `sudo apt-get install npm`
- Install AWS CDK: `npm install -g aws-cdk`
    - If there's permission errors on Mac or Linux, run `sudo !!` directly afterwards. It means `npm` was installed as
      system files and needs root access
- Maven, to build the Java project: <https://maven.apache.org/>
    - Or on Mac: `brew install maven`
    - Or on Debian: `sudo apt-get install maven`

1. Make sure you have access to an IAM account, and then run `aws configure`.
2. Run `cdk bootstrap aws://$ACCOUNT_NUMBER/$REGION` to set up CDK. `$ACCOUNT_NUMBER` should be the numeric root AWS
   account ID. `$REGION` should look something like `us-west-2`.

## Building and Deploying the Project

For a first time setup:

Then to build and deploy, follow the following steps:

- `mvn package -pl '!alexa-iot-application'` to build the application code
- `cdk synth` to Generate the application template and upload assets to the CDK stack
- `cdk deploy` to launch the stack to your account

## Attaching an Alexa Skill

I've found that setting up an Alexa Skill is a frustratingly un-automatable task for devs to set up on their own
accounts. Creating an Alexa Skill needs to happen once, and it needs to be reattached to the Lambda whenever it is fully
deleted and created (redeploying through CDK shouldn't require this, unless the original stack was deleted).

1. Create an Alexa Skill on <https://developer.amazon.com/alexa/console/ask>, creating an Amazon Developer account (for
   some reason, not an AWS account) if needed. Select Smart Home skill.
2. You should be navigated to the Smart Home tab of the new skill. In the Smart Home service endpoint section, copy Your
   Skill ID. Keep this page open
3. On a new tab, visit your AWS Console on the account that deployed the project -> Visit CloudFormation -> Stacks ->
   Select Oregon region (us-west-2) -> the newest stack on the top of the list.
4. Navigate through the Resources tab -> AlexaSkillFunction -> Configuration tab below the Function overview -> Triggers
5. Press **New Trigger** -> select **Alexa Smart Home** -> paste Your Skill ID.
6. Go back to the Function overview, copy the Function ARN, go back to your Alexa Skill management page and paste it in
   Smart Home -> Smart Home service endpoint -> Default endpoint. Repeat steps 5 and 6 whenever the Function is deleted
   in-between redeployment.

TODO Set Up Account
Linking: <https://developer.amazon.com/en-US/docs/alexa/smarthome/set-up-account-linking-tutorial.html>