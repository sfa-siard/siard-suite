#start SQL Server, start the script to create the DB and import the data
# the tail -f command just keeps the container alive. see https://github.com/twright-msft/mssql-node-docker-demo-app/issues/12
/opt/mssql/bin/sqlservr & /usr/src/init-db.sh & tail -f /dev/null