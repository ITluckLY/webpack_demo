@echo off

rem Which java to use
IF ["%JAVA_HOME%"] EQU [""] (
	set JAVA=java
) ELSE (
	set JAVA="%JAVA_HOME%/bin/java"
)

rem Using pushd popd to set BASE_DIR to the absolute path
pushd %~dp0
set BASE_DIR=%CD%
popd

setlocal enabledelayedexpansion
set jars=%BASE_DIR%\classes
for /R %BASE_DIR%\lib %%s in (*.jar) do (
	rem echo %%s 
	set jars=!jars!;%%s
) 
echo %jars%
title nameNode
call %JAVA% -classpath "%jars%" com.dcfs.esb.ftp.server.namenode.StartFileServer
pause