FROM mcr.microsoft.com/mssql/server:2019-latest

# Create app directory
RUN mkdir -p /usr/src
WORKDIR /usr/src

# Bundle app source
COPY . /usr/src

# Grant permissions for the import-data script to be executable

USER root
RUN chmod +x /usr/src/init-db.sh
# Switch back to mssql user and run the entrypoint script
USER mssql

EXPOSE 1433


ENTRYPOINT /bin/bash ./entrypoint.sh