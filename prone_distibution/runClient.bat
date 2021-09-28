@echo off
setlocal enabledelayedexpansion

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
@call java -Dlog4j.configurationFile=config/log4j.properties -Djava.library.path="lib/" -cp "config/*;lib/*;src/*;Prone-v1.0_20210603.jar"  gov.va.vinci.ProneLeoClient -clientConfigFile=config/ProneClientConfig.groovy -readerConfigFile=config/readers/FileCollectionReaderConfig.groovy -listenerConfigFile=config/listeners/SimpleXmiListenerConfig.groovy

:RUN_2
@call %JAVA_HOME%/bin/java -Dlog4j.configurationFile=config/log4j.properties -Djava.library.path="lib/" -cp "config/*;lib/*;src/*;Prone-v1.0_20210603.jar"   gov.va.vinci.ProneLeoClient -clientConfigFile=config/ProneClientConfig.groovy -readerConfigFile=config/readers/FileCollectionReaderConfig.groovy -listenerConfigFile=config/listeners/SimpleXmiListenerConfig.groovy
pause
