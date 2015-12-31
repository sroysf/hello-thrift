#!/bin/bash

thrift -gen java:beans,private-members,fullcamel -out ../java sample.thrift