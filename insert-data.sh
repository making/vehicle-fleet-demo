#!/bin/sh

if ! [ -e "fleet.json" ]; then
	wget http://assets.springone2gx2015.s3.amazonaws.com/fleet/fleet.json
fi

if ! [ -e "serviceLocations.json" ]; then
	wget http://assets.springone2gx2015.s3.amazonaws.com/fleet/serviceLocations.json
fi

#curl -H "Content-Type: application/json" localhost:9000/fleet -d @fleet.json
#curl -H "Content-Type: application/json" localhost:9001/bulk/serviceLocations -d @serviceLocations.json

curl -H "Content-Type: application/json" http://fleet-demo-fleet-location-service.cfapps.io/fleet -d @fleet.json
curl -H "Content-Type: application/json" http://fleet-demo-service-location-service.cfapps.io/bulk/serviceLocations -d @serviceLocations.json