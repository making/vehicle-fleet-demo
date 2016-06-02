#!/bin/sh

cred='{"uri":"http://fleet-demo-eureka-server.cfapps.io"}'
cf create-user-provided-service fleet-demo-eureka -p $cred