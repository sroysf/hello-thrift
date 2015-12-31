#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}

thrift -gen java:beans,private-members,fullcamel -out ../java sample.thrift