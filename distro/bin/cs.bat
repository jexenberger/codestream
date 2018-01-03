@echo off

REM  Codestream version
set VERSION=1.0-SNAPSHOT

REM  Root path of Codestream installation folder
set CS_HOME_DIR=""

REM Java executable path
set JAVA=""

REM Java properties to pass
set JAVA_PROPS=""

REM Executable
set JAR_FILE=distro
set EXECUTABLE=%JAR_FILE%-%VERSION%.jar

REM  Setup Java path
if DEFINED JAVA_HOME (
    set JAVA=%JAVA_HOME%\bin\java
) ELSE (
    set JAVA=java
)

REM  Setup codestream home
if DEFINED CS_HOME (
    set CS_HOME_DIR=%CS_HOME%
) ELSE (
    exit 2
)

REM  Set codestream working home
set JAVA_PROPS=-Dcs.installation.folder=%CS_HOME_DIR%

REM  Set executable JAR path
set EXECUTABLE_PATH=%CS_HOME_DIR%/lib/%EXECUTABLE%

REM  setup arguments
set ARGS=""
IF [%1] EQU [] (
	set ARGS=--help
) ELSE (
    set ARGS=%*
)

CMD /k ""%JAVA%"" %JAVA_PROPS% -jar %EXECUTABLE_PATH% %ARGS%