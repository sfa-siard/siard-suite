#=======================================================================
# zip64 executes ch.enterag.zip.zip64 in lib/zip64.jar. 
# Application: ZIP Utilities
# Platform   : Windows
#-----------------------------------------------------------------------
# Copyright  : Enter AG, Zurich, Switzerland, 2018
# Created    : 13.07.2018, Hartwig Thomas
#=======================================================================
param (
	[switch]$h = $false
)

$EXECUTABLE='java.exe'
$MIN_JAVA_VERSION = [Version]'1.8'
# logging properties relative to script location
$REL_LOGGING_PROPERTIES = 'etc\logging.properties'
# jar file relative to script location
$REL_JAR_FILE = 'lib\zip64.jar'
# class with main() to be run
$CLASS = 'ch.enterag.zip.zip64'

#-----------------------------------------------------------------------
# help displays usage information
#-----------------------------------------------------------------------
Function help()
{
	Write-Host "Calling syntax"
	Write-Host "  zip64.ps1 [-h] | <args>"
  Write-Host "executes $CLASS in $REL_JAR_FILE using $REL_LOGGING_PROPERTIES for logging."
  Write-Host ""
  Write-Host "Parameters:"
  Write-Host "  -h        displays usage information"
  Write-Host "  <args>    see documentation of zip64"
  Write-Host ""
  Write-Host "JavaHome:"
  Write-Host "  First the registry under HKLM\SOFTWARE\JavaSoft"
  Write-Host "  is searched for CurrentVersion and for JavaHome"
  Write-Host "  for locating the $EXECUTABLE."
  Write-Host ""
  Write-Host "  If that fails and an environment variable JAVA_HOME exists,"
  Write-Host "  That is used for locating the $EXECUTABLE."
  Write-Host ""
  Write-Host "JavaOpts:"
  Write-Host "  The environment variable JAVA_OPTS is used as a"
  Write-Host "  source for additional JAVA options."
  Write-Host "  E.g. -Xmx1000m or"
  Write-Host "       -DproxyHost=www-proxy.admin.ch -DproxyPort=8080 or"
  Write-Host "       -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true"
  Write-Host ""
} # help 

#-----------------------------------------------------------------------
# normalize a JAVA version number by dropping the major version, if it is 1,
# in order to prepare it for comparison.
# (Starting with JAVA 9 the leading '1.' was dropped from JAVA versions.)
# @param $version input version
# @return normalized version
#-----------------------------------------------------------------------
Function NormalizeJavaVersion([Version] $version)
{
	#Write-Host ">> NormalizeJavaVersion($version)"
	if ($($version.Major) -eq 1)
	{
	  # append '.0', because .NET cannot handle versions with a single component
	  $version = [Version]("$version".SubString(2) + ".0")
	}
	#Write-Host "<< NormalizeJavaVersion($version)"
	return $version
} # NormalizeJavaVersion

#-----------------------------------------------------------------------
# find installed Version of Java
#-----------------------------------------------------------------------
Function FindJava()
{
  #Write-Host '>> FindJava'
  try
  {
    $MIN_JAVA_VERSION = NormalizeJavaVersion $MIN_JAVA_VERSION
    $keyBase = 'HKLM:\SOFTWARE\JavaSoft\'
    #Write-Host 'check HKLM:\Software\JavaSoft\JDK'
    $key = ($keyBase+'JDK')
    $regLocation = Get-ItemProperty -Path $key -ErrorAction SilentlyContinue
    if ($regLocation -eq $null)
    {
      #Write-Host 'check HKLM:\Software\JavaSoft\JRE'
	    $key = ($keyBase+'JRE')
	    $regLocation = Get-ItemProperty -Path $key -ErrorAction SilentlyContinue
      if ($regLocation -eq $null)
      {
        #Write-Host 'check HKLM:\Software\JavaSoft\Java Runtime Environment'
        # This was the single location before JAVA 9 
		    $key = ($keyBase+'Java Runtime Environment')
		    $regLocation = Get-ItemProperty -Path $key -ErrorAction Stop
      }
    }
    #Write-Host 'regLocation: '+ $regLocation
    $value = 'CurrentVersion'
    $currentVersion = $regLocation.$value
    #Write-Host 'currentVersion: '+ $currentVersion
    $currentVersion = NormalizeJavaVersion $currentVersion 
    if ([Version]$currentVersion -ge $MIN_JAVA_VERSION)
    {
      $key = Join-Path $key $currentVersion
      $regLocation = Get-ItemProperty -Path $key -ErrorAction Stop
      #Write-Host 'regLocation: '+ $regLocation
      $value = 'JavaHome'
      $javaHome = $RegLocation.$Value
      #Write-Host 'javaHome: '+$javaHome
      if ($javaHome)
      {
        $tryJavaExe = [io.path]::combine($javaHome,'bin',$EXECUTABLE)
      }
    }
    else
    {
      Write-Host "Current JAVA version $currentVersion is too low!"
    }
  }
  catch 
  {
    #Write-Host 'HKLM:\Software\JavaSoft not found!'
  }
  if ($tryJavaExe -and (Test-Path $tryJavaExe))
  {
    $javaExe = $tryJavaExe
  }
  else
  {
    #Write-Host 'check JAVA_HOME'
    $javaHome = $env:JAVA_HOME
    if ($javaHome)
    {
      $tryJavaExe = [io.path]::combine($javaHome,'bin',$EXECUTABLE)
      if (Test-Path $tryJavaExe)
      {
        $javaExe = $tryJavaExe
      }
    }
  }
  #Write-Host '<< FindJava '+$javaExe
  return $javaExe
} # FindJava

#-----------------------------------------------------------------------
if ($h)
{
	Help
} 
else 
{
  $javaExe = FindJava
  if ($javaExe)
  {
    # logging properties and siardgui jar
    # are relative to script location (not to working directory!)
    $scriptName = $MyInvocation.InvocationName
    #Write-Host 'scriptName: '+$scriptName
    $scriptName = (Resolve-Path -Path $scriptName)
    #Write-Host 'scriptName: '+$scriptName
    $execDir = (Split-Path -Path $scriptName)
    #Write-Host 'execDir: '+$execDir
    $logProp = (Join-Path $execDir $REL_LOGGING_PROPERTIES)
    #Write-Host 'logProp: '+$logProp
    $opts = $('-Djava.util.logging.config.file=' + $logprop)
    #Write-Host 'opts: '+$opts
    $jarFile = (Join-Path $execDir $REL_JAR_FILE)
    #Write-Host 'jarFile: '+$jarFile
    #Write-Host "running: $javaExe $opts $env:JAVA_OPTS -cp $jarFile $CLASS $args"
    & $javaExe $opts $env:JAVA_OPTS -cp $jarFile $CLASS $args
  }
  else
  {
    Write-Host "No valid $EXECUTABLE could be found." 
    return 8
  }
}
