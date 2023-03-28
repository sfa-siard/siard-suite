@echo off
rem ====================================================================
rem  zip64 executes ch.enterag.zip.zip64 in lib/zip46.jar. 
rem  Application: ZIP Utilities
rem  Platform   : Windows
rem --------------------------------------------------------------------
rem Copyright  : Enter AG, Zurich, Switzerland, 2018
rem Created    : 13.07.2018, Hartwig Thomas
rem ====================================================================
chcp 1252
if "%~1"=="-?" goto help
if "%~1"=="/?" goto help
if "%~1"=="-h" goto help
if "%~1"=="/h" goto help
set jar=lib\zip64.jar
set logprop=etc\logging.properties
set class=ch.enterag.zip.zip64
goto start

:regquery
rem --------------------------------------------------------------------
rem query the registry
rem input: %reg%, %key% and %value%
rem output: %result%
rem --------------------------------------------------------------------
rem @echo +regquery(key: %key%, value: %value%)
set result=
rem skip to line 4 and get all tokens from 3 (each line has %%f=%VALUE% %%g=<type> %%h=<data>) 
for /f "skip=4 tokens=1,2,*" %%f in ('%reg% query "%key%" /v %value%') do set result=%%~h
if not "%result%"=="" goto result
rem skip to line 4 and get all tokens from 3 (each line has %%f=%VALUE% %%g=<type> %%h=<data>) 
for /f "skip=2 tokens=1,2,*" %%f in ('%reg% query "%key%" /v %value%') do set result=%%~h
:result
rem @echo -regquery: %result%
goto exit

:getmajor
rem --------------------------------------------------------------------
rem extract the major version from version string %1
rem output: %major%
rem see https://ss64.com/nt/syntax-replace.html
rem --------------------------------------------------------------------
rem @echo +getmajor(%1)
set major=%1
rem drop leading "1." from %major%
if not "%major:~0,2%"=="1." goto maj
set major=%major:~2%
:maj
set tail=%major:*.=%
call set major=%%major:.%tail%=%%
rem @echo -getmajor: '%major%'
goto exit

:regjava
rem --------------------------------------------------------------------
rem check registry HKLM\SOFTWARE\JavaSoft\Java Runtime Environment\xxx
rem for JavaHome\bin\%executable%
rem input: %reg%, %softkey%, %minversion% and %executable%
rem output: %jh%
rem --------------------------------------------------------------------
rem @echo +regjava(reg: %reg%, softkey: %softkey%, executable: %executable%, minversion: %minversion%)
set jh=
rem for JAVA 9 they changed the registry location from "Java Runtime Environment" to "JDK" or "JRE"!
set key=%softkey%\JavaSoft\JDK
set value=CurrentVersion
call :regquery
if not "%result%"=="" goto current
set key=%softkey%\JavaSoft\JRE
set value=CurrentVersion
call :regquery
if not "%result%"=="" goto current
set key=%softkey%\JavaSoft\Java Runtime Environment
set value=CurrentVersion
call :regquery
:current
rem @echo current version: %result%
call :getmajor %result%
set version=%major%
rem @echo comparing %version% and %minversion%
if %version% LSS %minversion% goto error
if "%result%"=="" goto jh
set cv=%result%
if "%cv%"=="" goto jh
set key=%key%\%cv%
set value=JavaHome
call :regquery
if "%result%"=="" goto jh
set jh=%result%
rem @echo JAVA_HOME: %jh%
:jh
rem @echo -regjava: '%jh%'
goto exit

:start
rem --------------------------------------------------------------------
rem use C:\Windows\system32\reg.exe if it exists
rem --------------------------------------------------------------------
set reg=reg.exe
set sysreg=%SystemRoot%\system32\%reg%
if exist %sysreg% set reg=%sysreg%

rem --------------------------------------------------------------------
rem execution directory from which cmd is called
rem --------------------------------------------------------------------
set execdir=%~dp0
rem detach the trailing backslash
set execdir=%execdir:~0,-1%

rem --------------------------------------------------------------------
rem local variables
rem --------------------------------------------------------------------
set executable=java.exe
set java=
set args=%*
set softkey=HKLM\Software
set minversion=1.8
call :getmajor %minversion%
set minversion=%major%

:regcheck
rem @echo check registry for JAVA_HOME
rem --------------------------------------------------------------------
rem search for JavaHome in registry
rem --------------------------------------------------------------------
call :regjava
if "%jh%"=="" goto javacheck
set java=%jh%\bin\%executable%
if exist "%java%" goto execute
rem @echo File "%java%" could not be found!
goto javacheck

:javacheck
rem --------------------------------------------------------------------
rem check environment variable JAVA_HOME for %executable%
rem --------------------------------------------------------------------
rem @echo checking JAVA_HOME
if "%JAVA_HOME%"=="" goto error
call :getmajor %JAVA_HOME:*jdk=%
set version=%major%
rem @echo comparing %version% and %minversion%
if %version% LSS %minversion% goto error
set java=%JAVA_HOME%\bin\%executable%
if exist "%java%" goto execute
rem @echo File "%java%" could not be found!
goto error

:execute
rem --------------------------------------------------------------------
rem execute %class% in "%jar%"
rem --------------------------------------------------------------------
set opts="-Djava.util.logging.config.file=%execdir%\%logprop%" %JAVA_OPTS%
"%java%" %opts% -cp "%execdir%\%jar%" %class% %args%
goto exit

:error
rem --------------------------------------------------------------------
rem error message for missing %executable%
rem --------------------------------------------------------------------
@echo No valid javaw.exe with version greater or equal %minversion% could be found.
@echo "Install the JAVA JRE or indicate correct path on the command line.  "

:help
rem --------------------------------------------------------------------
rem help for calling syntax
rem --------------------------------------------------------------------
rem we need the quotes for protecting the angular brackets
@echo "Calling syntax"
@echo "  zip64.cmd [-h] | <args>                                           "
@echo "executes %class% in %jar%.       "
@echo "                                                                    "
@echo "Parameters:                                                         "
@echo "  -h        displays usage information                              "
@echo "  <args>    see documentation of %class%                            "
@echo "                                                                    "
@echo "Javahome:                                                           "
@echo "  First the registry under HKLM\SOFTWARE\JavaSoft                   "
@echo "  is searched for CurrentVersion and for JavaHome                   "
@echo "  for locating the %executable%.                                    "
@echo "                                                                    "
@echo "  If that fails and an environment variable JAVA_HOME exists,       "
@echo "  that is used for locating the %executable%.                       "
@echo "                                                                    "
@echo "Javaopts:                                                           "
@echo "  The environment variable JAVA_OPTS is used as a                   "
@echo "  source for additional JAVA options.                               "
@echo "  E.g. -Xmx1000m or                                                 "
@echo "       -DproxyHost=www-proxy.admin.ch -DproxyPort=8080 or           "
@echo "       -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true"
@echo "                                                                    "

rem --------------------------------------------------------------------
rem exit
rem --------------------------------------------------------------------
:exit
exit /b %ERRORLEVEL%
