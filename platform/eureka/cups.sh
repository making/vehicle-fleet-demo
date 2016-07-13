#!/bin/sh

cred='{"uri":"http://fleet-demo-eureka-server.local.pcfdev.io"}'
cf create-user-provided-service fleet-demo-eureka -p $cred
