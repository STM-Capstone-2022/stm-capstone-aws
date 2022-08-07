# STM Capstone AWS integration

This repository integrates with 

The repo has a couple submodules:

- **stm-cloud-application** - The Amazon CDK code that defines a CloudFormation stack for the Alexa skill.
- **alexa-endpoint-worker** - The source code for the AWS Lambda function that drives the Alexa Skill.

## Developer Tools and Tool Setup

Developers need to install a few AWS tools and dev kits to set up the project. For Windows developers, please use the
Windows Subsystem for Linux to install the required software. Both our Mac and Linux developers have had an easier time
to install the tooling, and the README's shell commands assume POSIX syntax.

- AWS command line interface: <https://aws.amazon.com/cli/>
- Node.js's NPM to install the AWS CDK: <https://www.npmjs.com/>
    - Or on Mac, run: `brew install npm`
    - Or on Debian Linux, run: `sudo apt-get install npm`
- Install AWS CDK: `npm install -g aws-cdk`
    - If there's permission errors, run `sudo !!` directly afterwards. It means `npm` was installed on the whole system
      rather than local to the user, and needs root access
- Maven, to build the Java project: <https://maven.apache.org/>
    - Or on Mac, run: `brew install maven`
    - Or on Debian Linux, run: `sudo apt-get install maven`

1. Make sure you have access to an IAM account, and then run `aws configure`. Ensure the region is set to `us-east-1`.
2. Run `cdk bootstrap aws://$ACCOUNT_NUMBER/us-east-1` to set up CDK. `$ACCOUNT_NUMBER` should be the numeric root AWS
   account ID. The region **must** be `us-east-1`, as that's the region the Alexa service will talk to within the US
   market.

## Configuration

Constants are softcoded in [application.properties](./stm-cloud-application/src/main/resources/application.properties).
A sibling file `application-override.properties` is gitignore'd to hold sensitive information and may
supersede `application.properties` when both contain the same key.

These keys include:

- `iot.thing.generic.name` - The ID of the IoT Core Thing of the automated generic power-controllable device.
- `iot.thing.sensor.name` - The ID of the IoT Core Thing of the door knock sensor.
- `iot.thing.region` - The AWS-deployed region of both IoT Core Things.

## Building and Deploying the Project

To build and deploy, run the following commands within the project directory:

- `mvn package -pl '!alexa-iot-application'` to build all worker submodules (building the cloud application itself is
  not necessary).
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
   Select N. Virginia region (us-east-1) -> the newest stack on the top of the list.
4. Navigate through the Resources tab -> AlexaSkillFunction -> Configuration tab below the Function overview -> Triggers
5. Press **New Trigger** -> select **Alexa Smart Home** -> paste Your Skill ID.
6. Go back to the Function overview, copy the Function ARN, go back to your Alexa Skill management page and paste it in
   Smart Home -> Smart Home service endpoint -> Default endpoint. Repeat steps 5 and 6 whenever the Function is deleted
   in-between redeployment.

Finally, set up Account Linking in the AWS
documentation: <https://developer.amazon.com/en-US/docs/alexa/smarthome/set-up-account-linking-tutorial.html>