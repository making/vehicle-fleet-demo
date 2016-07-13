#!/bin/sh

if ! [ -e "fleet.json" ]; then
	wget http://assets.springone2gx2015.s3.amazonaws.com/fleet/fleet.json
fi

if ! [ -e "serviceLocations.json" ]; then
	wget http://assets.springone2gx2015.s3.amazonaws.com/fleet/serviceLocations.json
fi

#url_s=localhost:9001
#url_f=localhost:9000

#url_s=http://fleet-demo-service-location-service.52.68.100.51.xip.io
#url_f=http://fleet-demo-fleet-location-service.52.68.100.51.xip.io

#url_s=http://fleet-demo-service-location-service.local.pcfdev.io
#url_f=http://fleet-demo-fleet-location-service.local.pcfdev.io

url_s=http://fleet-demo-service-location-service.cfapps.io
url_f=http://fleet-demo-fleet-location-service.cfapps.io

echo ""
echo "====="
echo "Insert ServiceLocations"
curl -XPOST -H "Content-Type: application/json" ${url_s}/purge
curl -H "Content-Type: application/json" ${url_s}/bulk/serviceLocations -d @serviceLocations.json

echo ""
echo "====="
echo "Insert Fleets"
curl -XPOST -H "Content-Type: application/json" ${url_f}/purge
curl -H "Content-Type: application/json" ${url_f}/fleet -d @fleet.json

#curl -H "Content-Type: application/json" http://fleet-demo-service-location-service.cfapps.io/bulk/serviceLocations -d @serviceLocations.json
