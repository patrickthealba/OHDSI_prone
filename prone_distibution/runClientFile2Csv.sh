java -Dlog4j.configurationFile=config/log4j.properties -Djava.library.path="lib/" -cp "config/*:lib/*:src/*:Prone-v1.0_20210603.jar"  gov.va.vinci.ProneLeoClient -clientConfigFile=config/ProneClientConfig.groovy -readerConfigFile=config/readers/FileCollectionReaderConfig.groovy -listenerConfigFile=config/listeners/CsvListenerConfig.groovy