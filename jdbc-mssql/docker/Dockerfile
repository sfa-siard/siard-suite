FROM mcr.microsoft.com/mssql/server:2019-latest

# Install SQL Server command-line tools
USER root
RUN apt-get update && ACCEPT_EULA=Y apt-get install -y mssql-tools

# Create app directory
RUN mkdir -p /usr/src
WORKDIR /usr/src

# Bundle app source
COPY . /usr/src

# Grant permissions for the import-data script to be executable
RUN chmod +x /usr/src/init-db.sh

# Switch back to mssql user and run the entrypoint script
USER mssql

EXPOSE 1433

ENTRYPOINT /bin/bash ./entrypoint.sh