#!/bin/bash
set -e
# Define script variables
DEPLOY_DIR="$( cd "$( dirname "$0" )" && pwd )"
WORKSPACE=$PWD
ENV=$1
TAG_SUFFIX=$2
TAG="$ENV.$TAG_SUFFIX"

DOCKER_REPO=appiriodevops/tc-terms-service

cd $DEPLOY_DIR/docker

echo "Generate sumo config files"
cat sumo.conf.template | sed -e "s/@env@/${ENV}/g" > sumo.conf
cat sumo-sources.json.template | sed -e "s/@env@/${ENV}/g" > sumo-sources.json

echo "Copying deployment files to docker folder"
cp $WORKSPACE/target/terms-microservice*.jar terms-microservice.jar
cp $WORKSPACE/src/main/resources/terms-service.yaml terms-service.yaml

echo "Logging into docker"
echo "############################"
docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASSWD

echo "Building docker image $DOCKER_REPO:$TAG"
docker build -t $DOCKER_REPO:$TAG .

echo "Pushing image to docker $DOCKER_REPO:$TAG"
docker push $DOCKER_REPO:$TAG

