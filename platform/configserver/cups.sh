#!/bin/sh

cred='{"uri":"http://fleet-demo-config-server.cfapps.io"}'
cf create-user-provided-service fleet-demo-configserver -p $cred