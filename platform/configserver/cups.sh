#!/bin/sh

cred='{"uri":"http://fleet-demo-config-server.52.68.100.51.xip.io"}'
cf create-user-provided-service fleet-demo-configserver -p $cred