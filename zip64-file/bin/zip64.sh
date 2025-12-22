#!/bin/bash
#=======================================================================
# zip64 executes ch.enterag.zip.zip64 in lib/zip64.jar. 
# Application: Siard2
# Platform   : LINUX/UNIX
#-----------------------------------------------------------------------
# Copyright  : Enter AG, Zurich, Switzerland, 2018
# Created    : 13.07.2018, Hartwig Thomas
#=======================================================================

# return code (0 = OK, 4 = Warning, 8 = Error, 12 = fatal error
rc=12
# minimum JAVA version
minjavaversion="1.8"
# jar file relative to script location
reljar=lib/zip64.jar
# logging properties relative to script location
rellogprop=etc/logging.properties
# class with main() to be run
class=ch.enterag.zip.zip64

#-----------------------------------------------------------------------
# javafind searches for java executable. If one is found, 1 is returned
# and the variable java is set to the full path. Otherwise 0 is returned.
#-----------------------------------------------------------------------
javafind()
{
  found=0
  java=$(type -p java)
  if [ -n "$java" ] 
  then
    found=1
  elif [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]
  then
    $java="$JAVA_HOME/bin/java"
    found=1
  fi  
  return $found 
} # javafind

#-----------------------------------------------------------------------
# javackeck returns 1, if $java exists and has major version 
# $minjavaversion or higher.
#-----------------------------------------------------------------------
javacheck()
{
  ok=0
  # N.B.: After "1.8..." comes "9..." - the leading "1." was dropped!
  # drop "1." from minjavaversion
  minjavaversion=${minjavaversion#1.}
  # execute java -version with small memory requirement
  version=`$java -Xms32M -Xmx32M -version 2>&1`
  # output must start with something like 'openjdk version "1.8.0_144"' or 'java version "10.0.1"'
  reQuoted='version "([^"]+)"'
  if [[ $version =~ $reQuoted ]]
  then
    version=${BASH_REMATCH[1]}
    # drop "1." from version
    version=${version#1.}
    # drop everything after the first "." (for numerical comparison)
    version=${version%%.*}
    # numeric comparison
    if [ $version -ge $minjavaversion ]
    then
      ok=1
    fi  
  fi
  return $ok
} # javacheck

#-----------------------------------------------------------------------
# help displays usage information
#-----------------------------------------------------------------------
help()
{
  echo "Calling syntax"
  echo "  siardfromdb.sh [-h] | <args>"
  echo "executes $class in $reljar using $rellogprop for logging."
  echo ""
  echo "Parameters"
  echo "  -h          displays usage information"
  echo "  <args>      see documentation of SiardFromDb"
  echo ""
  echo "JavaHome:"
  echo "  In order to find a suitable java executable, the PATH is first"
  echo " searched for \"java\", then JAVA_HOME."
  echo ""
  echo "JavaOpts:"
  echo "  The environment variable JAVA_OPTS is used as a"
  echo "  source for additional JAVA options."
  echo "  E.g. -Xmx1000m or"
  echo "       -DproxyHost=www-proxy.admin.ch -DproxyPort=8080 or"
  echo "       -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true"
  echo ""
  return 4
}

#-----------------------------------------------------------------------
# display error message about java executable
#-----------------------------------------------------------------------
error()
{
  echo "No valid java executable with version equal or greater than $minjavaversion could be found!"
  echo "Install the JAVA JRE or indicate correct location using JAVA_HOME!"
  return 8
} # error

#-----------------------------------------------------------------------
# executes $class in $reljar.
#-----------------------------------------------------------------------
execute()
{
  execdir="$0"
  execdir=${execdir%/*}
  opts="-Xmx1024m -Djava.util.logging.config.file=\"$execdir/$rellogprop\" $JAVA_OPTS"
  #echo "$java" $opts -cp "$execdir/$reljar"  "$class" "$args"
  if [ ${#args} -eq 0 ];
  then
    "$java" $opts -cp "$execdir/$reljar"  "$class"
  else
    "$java" $opts -cp "$execdir/$reljar"  "$class" $args
  fi
  return $?
} # execute

#-----------------------------------------------------------------------
# main
#-----------------------------------------------------------------------
if [ "$1" != "-h" ];
then
  args="$@"
  javafind
  ok=$?
  if [ $ok -ne 0 ];
  then
    javacheck
    ok=$?
    # if a suitable java executable was found then execute it
    if [ $ok -ne 0 ];
    then
      execute
    else
      error
    fi
  else
    error
  fi  
else
  help
fi
rc=$?
exit $rc
