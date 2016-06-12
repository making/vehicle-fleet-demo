#!/bin/sh

cred='{"uri":"http://fleet-demo-eureka-server.52.68.100.51.xip.io"}'
cf create-user-provided-service fleet-demo-eureka -p $cred