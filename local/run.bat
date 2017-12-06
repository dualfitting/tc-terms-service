# IP to which the docker container ports are mapped
set IP=cockpit.cloud.topcoder.com
set DOCKER_IP=%IP%

set AUTH_DOMAIN=topcoder-dev.com
# Please update this field by asking in forum
set TC_JWT_KEY=secret
set DW_PW=1nf0rm1x
set DW_USER=informix
set DW_URL=jdbc:informix-sqli://%DOCKER_IP%:2021/tcs_catalog:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;
set TCS_DW_PW=1nf0rm1x
set TCS_DW_USER=informix
set TCS_DW_URL=jdbc:informix-sqli://$DOCKER_IP:2021/tcs_dw:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;
set OLTP_PW=1nf0rm1x
set OLTP_USER=informix
set OLTP_URL=jdbc:informix-sqli://%DOCKER_IP%:2021/tcs_catalog:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;
set TIME_PW=1nf0rm1x
set TIME_USER=informix
set TIME_URL=jdbc:informix-sqli://%DOCKER_IP%:2021/time_oltp:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;

java -Ddw.authDomain=%AUTH_DOMAIN% -Ddw.databases[0].user=%OLTP_USER% -Ddw.databases[0].password=%OLTP_PW% -Ddw.databases[0].url=%OLTP_URL% -Ddw.databases[1].user=%DW_USER% -Ddw.databases[1].password=%DW_PW% -Ddw.databases[1].url=%DW_URL% -Ddw.databases[2].user=%TIME_USER% -Ddw.databases[2].password=%TIME_PW% -Ddw.databases[2].url=%TIME_URL% -jar ../target/terms-microservice-1.0.0.jar server ../src/main/resources/terms-service.yaml
