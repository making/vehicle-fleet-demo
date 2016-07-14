#!/bin/sh

cred='{"uri":"http://fleet-demo-eureka-server.apps.pcfdemo.ik.am"}'
cf create-user-provided-service fleet-demo-eureka -p $cred
