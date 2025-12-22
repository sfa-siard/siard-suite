#!/bin/bash

# add db2 binaries to the path - for convinience reasons
export PATH=/database/config/db2inst1/sqllib/bin/:$PATH

# add additional user for tests (as root) - must match the values defined in build.properties for db2
useradd -g db2iadm1 testdb2
echo "testdb2:testdb2pwd" | chpasswd

# need to work as user db2inst1
su - db2inst1

# connect to testdb (see docker-compose.yml) - username and password have to match
# actually - we do nothing here for now. just keep it for documentation on how to setup a db2 with docker and docker compose
db2 connect to TESTDB user db2inst1 using mypasswd

db2 GRANT DBADM ON DATABASE TO testdb2;
