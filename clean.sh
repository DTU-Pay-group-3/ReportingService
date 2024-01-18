#!/bin/bash
set -e

pushd messaging-utilities-3.4
mvn clean
popd 

pushd reporting-service
mvn clean
popd