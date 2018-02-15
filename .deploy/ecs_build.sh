#!/bin/bash
ENV=$1
if [[ -z "$ENV" ]] ; then
  echo "Environment should be set on startup with one of the below values"
  echo "ENV must be one of - DEV, QA, PROD or LOCAL"
  exit
fi

echo "$ENV before case conversion"

AWS_REGION=$(eval "echo \$${ENV}_AWS_REGION")
AWS_ACCESS_KEY_ID=$(eval "echo \$${ENV}_AWS_ACCESS_KEY_ID")
AWS_SECRET_ACCESS_KEY=$(eval "echo \$${ENV}_AWS_SECRET_ACCESS_KEY")
AWS_ACCOUNT_ID=$(eval "echo \$${ENV}_AWS_ACCOUNT_ID")
AWS_REPOSITORY=$(eval "echo \$${ENV}_AWS_REPOSITORY")
AWS_ECS_CLUSTER=$(eval "echo \$${ENV}_AWS_ECS_CLUSTER")
AWS_ECS_SERVICE=$(eval "echo \$${ENV}_AWS_ECS_SERVICE")
family=$(eval "echo \$${ENV}_AWS_ECS_TASK_FAMILY")
AWS_ECS_CONTAINER_NAME=$(eval "echo \$${ENV}_AWS_ECS_CONTAINER_NAME")
AUTH_DOMAIN=$(eval "echo \$${ENV}_AUTH_DOMAIN")
#APP_NAME

JQ="jq --raw-output --exit-status"

# Define script variables
DEPLOY_DIR="$( cd "$( dirname "$0" )" && pwd )"
WORKSPACE=$PWD


cd $DEPLOY_DIR/docker

echo "Copying deployment files to docker folder"
cp $WORKSPACE/target/terms-microservice*.jar terms-microservice.jar
cp $WORKSPACE/src/main/resources/terms-service.yaml terms-service.yaml

echo "Logging into docker"
echo "############################"
docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASSWD

#Converting environment varibale as lower case for build purpose
#ENV=`echo "$ENV" | tr '[:upper:]' '[:lower:]'`
#echo "$ENV after case conversion"

configure_aws_cli() {
  aws --version
  aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
  aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
  aws configure set default.region $AWS_REGION
  aws configure set default.output json
  echo "Configured AWS CLI."
}

build_ecr_image() {
  eval $(aws ecr get-login  --region $AWS_REGION)
  #eval $(aws ecr get-login)
  # Builds Docker image of the app.
  #$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$AWS_REPOSITORY:$CIRCLE_SHA1
  TAG=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$AWS_REPOSITORY:$CIRCLE_SHA1
  docker build -t $TAG .
}

push_ecr_image() {
  echo "Pushing Docker Image...."
  eval $(aws ecr get-login --region $AWS_REGION --no-include-email)
  echo $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$AWS_REPOSITORY:$TAG
  docker push $TAG
  echo "Docker Image published."
}

make_task_def(){
task_template=$(cat <<-END
{  
  "cpu": "100",
  "containerDefinitions": [
    {
    "name": "%s",
    "image": "%s.dkr.ecr.%s.amazonaws.com/%s:%s",
    "essential": true,
    "memory": 500,    
    "environment": [
        {
          "name": "AUTH_DOMAIN",
          "value": "%s"
        },
        {
          "name": "DOCUSIGN_INTEGRATOR_KEY",
          "value": "%s"
        },
        {
          "name": "DOCUSIGN_NDA_TEMPLATE_ID",
          "value": "%s"
        },
        {
          "name": "DOCUSIGN_PASSWORD",
          "value": "%s"
        },
        {
          "name": "DOCUSIGN_RETURN_URL",
          "value": "%s"
        },
        {
          "name": "DOCUSIGN_SERVER_URL",
          "value": "%s"
        },
        {
          "name": "DOCUSIGN_USERNAME",
          "value": "%s"
        },
        {
          "name": "OLTP_PW",
          "value": "%s"
        },
        {
          "name": "OLTP_URL",
          "value": "%s"
        },
        {
          "name": "OLTP_USER",
          "value": "%s"
        },
        {
          "name": "SMTP_HOST",
          "value": "%s"
        },
        {
          "name": "SMTP_PASSWORD",
          "value": "%s"
        },
        {
          "name": "SMTP_SENDER",
          "value": "%s"
        },
        {
          "name": "SMTP_USERNAME",
          "value": "%s"
        },
        {
          "name": "TC_JWT_KEY",
          "value": "%s"
        }
      ],
    "portMappings": [
        {
          "hostPort": 8080,
          "protocol": "tcp",
          "containerPort": 8080
        },
        {
          "hostPort": 8081,
          "protocol": "tcp",
          "containerPort": 8081
        }
      ],
    "logConfiguration": {
      "logDriver": "awslogs",
        "options": {
              "awslogs-group": "/ecs/%s",
              "awslogs-region": "%s",
              "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ],
"family": "%s",
"requiresCompatibilities": ["FARGATE"],
"networkMode": "awsvpc"
}
END
)
  echo "-------- family param value:"
  echo $family
  family_val= $family | $($JQ '.taskDefinition.taskDefinitionArn')
  echo $family_val
  echo "-------- vars to inject into template:"
  echo $AWS_ECS_CONTAINER_NAME $AWS_ACCOUNT_ID $AWS_REGION $AWS_REPOSITORY $TAG "$AUTH_DOMAIN" $DOCUSIGN_INTEGRATOR_KEY $DOCUSIGN_NDA_TEMPLATE_ID $DOCUSIGN_PASSWORD $DOCUSIGN_RETURN_URL $DOCUSIGN_SERVER_URL $DOCUSIGN_USERNAME $OLTP_PW $OLTP_URL $OLTP_USER $SMTP_HOST $SMTP_PASSWORD $SMTP_SENDER $SMTP_USERNAME $TC_JWT_KEY $AWS_ECS_CLUSTER $AWS_REGION $family_val
  echo "-------- template:"
  echo $task_template

  task_def=$(printf "$task_template" $AWS_ECS_CONTAINER_NAME $AWS_ACCOUNT_ID $AWS_REGION $AWS_REPOSITORY $TAG "$AUTH_DOMAIN" $DOCUSIGN_INTEGRATOR_KEY $DOCUSIGN_NDA_TEMPLATE_ID $DOCUSIGN_PASSWORD $DOCUSIGN_RETURN_URL $DOCUSIGN_SERVER_URL $DOCUSIGN_USERNAME $OLTP_PW $OLTP_URL $OLTP_USER $SMTP_HOST $SMTP_PASSWORD $SMTP_SENDER $SMTP_USERNAME $TC_JWT_KEY $AWS_ECS_CLUSTER $AWS_REGION $family_val)
  echo "-------- task def after injection:"
  echo $task_def
  echo $task_def > config.json
}


register_definition() {
    echo "register definition"
    echo aws ecs register-task-definition --cli-input-json file://config.json --family $family
    if revision=$(aws ecs register-task-definition --execution-role-arn "arn:aws:iam::811668436784:role/EcsTaskExecutionRole" --task-role-arn "arn:aws:iam::811668436784:role/EcsTaskExecutionRole" --cli-input-json file://config.json --family $family | $JQ '.taskDefinition.taskDefinitionArn'); then
        echo "Revision: $revision"
    else
        echo "Failed to register task definition"
        return 1
    fi

}

check_service_status() {
        counter=0
  sleep 60
        servicestatus=`aws ecs describe-services --service $AWS_ECS_SERVICE --cluster $AWS_ECS_CLUSTER | $JQ '.services[].events[0].message'`
        while [[ $servicestatus != *"steady state"* ]]
        do
           echo "Current event message : $servicestatus"
           echo "Waiting for 30 sec to check the service status...."
           sleep 30
           servicestatus=`aws ecs describe-services --service $AWS_ECS_SERVICE --cluster $AWS_ECS_CLUSTER | $JQ '.services[].events[0].message'`
           counter=`expr $counter + 1`
           if [[ $counter -gt $COUNTER_LIMIT ]] ; then
                echo "Service does not reach steady state with in 10 minutes. Please check"
                exit 1
           fi
        done
        echo "$servicestatus"
}

deploy_cluster() {

    make_task_def
    register_definition
    echo $revision
    update_result=$(aws ecs update-service --cluster $AWS_ECS_CLUSTER --service $AWS_ECS_SERVICE --task-definition $revision )
    result=$(echo $update_result | $JQ '.service.taskDefinition' )
    echo $result
    if [[ $result != $revision ]]; then
        echo "Error updating service."
        return 1
    fi

    echo "Update service intialised successfully for deployment"
    return 0
}


configure_aws_cli
build_ecr_image
push_ecr_image
deploy_cluster
#check_service_status
exit $?
