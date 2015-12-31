# Prerequisite

This requires that you have the [thrift compiler](https://thrift.apache.org/download) properly installed on your system.

# Building

    $ src/main/thrift/gencode.sh
    $ mvn clean install
    
# Running

    $ cd target/
    $ java -cp dependency/*:./testartifact-1.0-SNAPSHOT.jar com.force.Main
