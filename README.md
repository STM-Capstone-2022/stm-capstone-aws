# STM Capstone AWS integration

This repository integrates with 

The repo has a couple submodules:

- **stm-cloud-application** - The Amazon CDK code that defines a CloudFormation stack for the Alexa skill.
- **alexa-endpoint-worker** - The source code for the AWS Lambda function that drives the Alexa Skill.

And deploys two AWS stacks:

- **stm-alexa-endpoint** - Integrates the STM IoT devices with Alexa SmartHome
- **stm-automation-workers** - Integrates detection automation to deploy smart HVAC in the form of humidifying and
  air-conditioning.

Neither stack is self-integrating, and needs some setup after deployment to hook it up with the STM dev kits.

## Developer Tools and Tool Setup

Developers need to install a few AWS tools and dev kits to set up the project. For Windows developers, please use the
Windows Subsystem for Linux to install the required software. Both our Mac and Linux developers have had an easier time
to install the tooling, and the README's shell commands assume POSIX syntax.

- AWS command line interface: <https://aws.amazon.com/cli/>
- Node.js's NPM to install the AWS CDK: <https://www.npmjs.com/> or <https://nodejs.org/en/download/>
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

- `iot.thing.relay.name` - The ID of an IoT Core Thing, the relay to the multiple smart devices.
- `iot.thing.region` - The AWS-deployed region of the IoT Core Thing.

## Building and Deploying the Project

To build and deploy, run the following commands within the project directory:

- `mvn package -pl '!alexa-iot-application'` to build all worker submodules (building the cloud application itself is
  not necessary).
- `cdk synth` to Generate the application templates for each stack and upload assets to the CDK bucket
- `cdk deploy` to launch the stacks to your account

## stm-alexa-endpoint: Attaching an Alexa Skill (Manual Activation)

I've found that setting up an Alexa Skill is a frustratingly un-automatable task for devs to set up on their own
accounts. Creating an Alexa Skill needs to happen once, and it needs to be reattached to the Lambda whenever it is fully
deleted and created (redeploying through CDK shouldn't require this, unless the original stack was deleted).

1. Create an Alexa Skill on <https://developer.amazon.com/alexa/console/ask>, creating an Amazon Developer account (for
   some reason, not an AWS account) if needed. Select Smart Home skill.
2. You should be navigated to the Smart Home tab of the new skill. In the Smart Home service endpoint section, copy Your
   Skill ID. Keep this page open
3. On a new tab, visit your AWS Console on the account that deployed the project -> Visit CloudFormation -> Stacks ->
   Select N. Virginia region (us-east-1) -> the stack named `stm-alexa-endpoint`.
4. Navigate through the Resources tab -> AlexaSkillFunction -> Configuration tab below the Function overview -> Triggers
5. Press **New Trigger** -> select **Alexa Smart Home** -> paste Your Skill ID.
6. Go back to the Function overview, copy the Function ARN, go back to your Alexa Skill management page and paste it in
   Smart Home -> Smart Home service endpoint -> Default endpoint. Repeat steps 5 and 6 whenever the Function is deleted
   in-between redeployment.

Finally, set up Account Linking in the AWS
documentation: <https://developer.amazon.com/en-US/docs/alexa/smarthome/set-up-account-linking-tutorial.html>

## stm-automation-workers: Attaching a Rules Engine to the Detector Models (Automatic Activation)

The stack should have deployed two IoT Event detector models, named `stm-fan-automation-detector`
and `stm-humidifier-automation-detector`. The AWS Rules Engine can integrate them together with a rule that forwards the
sensor data over.

1. Go to the AWS Console, and nagivate to AWS IoT >> Message Routing >> Rules.
2. Click the "Create rule" button to begin the process
3. Give a rule name and description of your choice, and click Next
4. Select the SQL version `2016-03-23` and provide the SQL
   statement `SELECT temp_1_c as temp, rh_pct as humidity FROM 'DEVICE/env_sensor_data'`. Rename "DEVICE" from
   the `FROM` clause to the name of your STM sensor device. Click Next.
5. Add a rule action. Select "IoT Events: Send a message to an IoT Events Input". Select the input name `stmSensorInput`
   and enable **Use Batch Mode**. Select the IAM role `task-detector-stm-automation`. (TODO: this role as it stands
   might not have the right perms. I've no idea why the rule needs perms here.)
6. Click Next. Review the rule, and click Create.

## Troubleshooting

### stm-automation-workers: Delete Failed for "stmSensorInput"

If this stack begins the deletion process, AWS throws an error that the Event Input `stmSensorInput` cannot be deleted,
because it is in use by one or more detector models. However, those detector models have already been deleted. The
current action is to manually delete the input and re-start deleting the stack.
