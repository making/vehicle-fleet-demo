#!/bin/sh

cred='{"uri":"http://fleet-demo-config-server.apps.pcfdemo.ik.am"}'
cf create-user-provided-service fleet-demo-configserver -p $cred
