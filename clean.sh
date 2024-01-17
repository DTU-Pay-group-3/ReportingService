#!/bin/bash
set -e

pushd messaging-utilities-3.4
mvn clean
popd 

pushd logging-service
mvn clean
popd