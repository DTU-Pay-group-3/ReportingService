#!/bin/bash
set -e

# Build and install the libraries
# abstracting away from using the
# RabbitMq message queue
pushd messaging-utilities-3.4
./build.sh
popd 

# Build the services
pushd logging-service
./build.sh
popd 

pushd reporting-service
./build.sh
popd 
