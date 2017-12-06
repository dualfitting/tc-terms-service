# IP to which the docker container ports are mapped
export IP="cockpit.cloud.topcoder.com"
export DOCKER_IP="$IP"

export AUTH_DOMAIN="topcoder-dev.com"
# Please update this field by asking in forum
export TC_JWT_KEY="secret"
export DW_PW="1nf0rm1x"
export DW_USER="informix"
export DW_URL="jdbc:informix-sqli://$DOCKER_IP:2021/topcoder_dw:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;"

export TCS_DW_PW="1nf0rm1x"
export TCS_DW_USER="informix"
export TCS_DW_URL="jdbc:informix-sqli://$DOCKER_IP:2021/tcs_dw:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;"



export OLTP_PW="1nf0rm1x"
export OLTP_USER="informix"
export OLTP_URL="jdbc:informix-sqli://$DOCKER_IP:2021/tcs_catalog:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;"
export TIME_PW="1nf0rm1x"
export TIME_USER="informix"
export TIME_URL="jdbc:informix-sqli://$DOCKER_IP:2021/time_oltp:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;"

java -Ddw.authDomain="$AUTH_DOMAIN" -Ddw.databases[0].user="$OLTP_USER" -Ddw.databases[0].password="$OLTP_PW" -Ddw.databases[0].url="$OLTP_URL" -jar ../target/terms-microservice-*.jar server ../src/main/resources/terms-service.yaml
