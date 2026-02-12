FROM ibmcom/db2:11.5.7.0

RUN mkdir /var/custom
COPY ./custom/01-initialize-db.sh /var/custom

# db2 does not have the concept of db users on the level of the database. All users must be on the system level.
# see: https://dba.stackexchange.com/questions/133837/db2-add-user-with-a-password
# create one and set the password
# make sure that you use the same user in build.properties
RUN useradd testuser
RUN echo "testuser:testuserpwd" | chpasswd

RUN chmod a+x /var/custom/01-initialize-db.sh