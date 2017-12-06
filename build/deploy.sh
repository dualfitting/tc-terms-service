#!/usr/bin/env bash
# Define script variables
WORKSPACE=$1
DEPLOY_DIR=$2
SERVICE=$3
ENV=$4

DOCKER_REPO=appiriodevops
VER=`date "+%Y%m%d%H%M"`
IMAGE="$SERVICE-microservice:$ENV.$VER" 
DOCKERRUN_TEMPLATE=$DEPLOY_DIR/Dockerrun.aws.json.template
DOCKERRUN=$DEPLOY_DIR/Dockerrun.aws.json
AWS_S3_BUCKET=appirio-platform-dev
AWS_PROFILE="tc-$ENV"
AWS_S3_KEY="services/docker/$IMAGE"
ENV_JAVA_PARAMS=" "

# Elastic Beanstalk Application name
# dev
APPNAME="Development"
if [ "$ENV" = "qa" ]; then
    APPNAME="QA"
fi
if [ "$ENV" = "prod" ]; then
    APPNAME="Production"
    AWS_S3_BUCKET=appirio-platform-prod
    ENV_JAVA_PARAMS='-Dnewrelic.environment=production -javaagent:$NEWRELIC_JAR'
fi

cd $DEPLOY_DIR

echo "Generating Dockerfile"
cat Dockerfile.template | sed -e "s/@ENV_JAVA_PARAMS@/$ENV_JAVA_PARAMS/g" > Dockerfile

echo "Generate sumo config files"
cat sumo.conf.template | sed -e "s/@env@/${ENV}/g" > sumo.conf
cat sumo-sources.json.template | sed -e "s/@env@/${ENV}/g" > sumo-sources.json

echo "Copying deployment files to build folder"
cp $WORKSPACE/target/terms-microservice*.jar terms-microservice.jar
cp $WORKSPACE/src/main/resources/terms-service.yaml terms-service.yaml

echo "Building docker image $DOCKER_REPO/$IMAGE"
sudo docker build -t $DOCKER_REPO/$IMAGE $DEPLOY_DIR

echo "Pushing image to docker $DOCKER_REPO/$IMAGE"
sudo docker push $DOCKER_REPO/$IMAGE

echo "Generating dockerrun file"
cat $DOCKERRUN_TEMPLATE | sed -e "s/@IMAGE@/${IMAGE}/g" > $DOCKERRUN

echo "Uploading dockerrun file to aws"

echo "Pushing Dockerrun.aws.json to S3: ${AWS_S3_BUCKET}/${AWS_S3_KEY}"
aws s3api put-object --profile $AWS_PROFILE --bucket "${AWS_S3_BUCKET}" --key "${AWS_S3_KEY}" --body $DOCKERRUN

echo "Creating new application version $IMAGE in $APPNAME from s3:${AWS_S3_BUCKET}/${AWS_S3_KEY}"
aws elasticbeanstalk create-application-version --profile $AWS_PROFILE --application-name $APPNAME --version-label $IMAGE --source-bundle S3Bucket="$AWS_S3_BUCKET",S3Key="$AWS_S3_KEY"

echo "updating elastic beanstalk environment ${AWS_EB_ENV} with the version $IMAGE."
# assumes beanstalk app for this service has already been created and configured
aws elasticbeanstalk --profile $AWS_PROFILE update-environment --environment-name $SERVICE-$ENV --version-label $IMAGE

