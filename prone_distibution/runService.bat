@echo off
setlocal enabledelayedexpansion

cd /D "%~dp0"

for /f tokens^=2-5^ delims^=.-^" %%j in ('java -fullversion 2^>^&1') do (
	set jver=%%k
)

if (%jver% LSS 8) goto USAGE_JAVA
goto RUN_1

:USAGE_JAVA
echo Running this application requires Java 8. 
set JAVA_HOME=C:/"Program Files"/Java/jdk1.8.0_221
goto RUN_2

:RUN_1
@call java -Dlog4j.configurationFile=config/log4j.properties -cp "*;config/*;lib/*;src/*;Prone_v1.0_20210603.jar" gov.va.vinci.ProneService  -serviceConfigFile=config/ProneServiceConfig.groovy -pipeline=gov.va.vinci.pipeline.Prone_Pipeline

:RUN_2
@call %JAVA_HOME%/bin/java -Dlog4j.configurationFile=config/log4j.properties -cp "*;config/*;lib/*;src/*;Prone_v1.0_20210603.jar" gov.va.vinci.ProneService   -serviceConfigFile=config/ProneServiceConfig.groovy -pipeline=gov.va.vinci.pipeline.Prone_Pipeline  
pause
:EXIT


