## Prone

## Environment Requirements for the prone system Jar.
- UIMA-AS:  This system requires uima 2.9
		https://archive.apache.org/dist/uima/uima-as-2.9.0/
- Jdk 8: Currently using and tested on Amazon Correto open JDK:
	https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/macos-install.html


## Install Java
Leo uses Java 8 and both the JRE and JDK are required.

### Java Runtime Environment (JRE)
You will need Java 8 installed on your system. It can be obtained here or through another preferred download method such as your package manager. Please choose the version appropriate for your operating system and processor.

### Java Development Kit (JDK)
You will also need the Java Development Kit installed on your system. It can be obtained here or through another preferred download method such as your package manager. Please choose the version appropriate for your operating system, processor, and JRE installation.

## Install the UIMA Framework
### Downloading and Extracting UIMA
Visit the Apache UIMA - Downloads Official Releases webpage.
Download the 2.9.0 binary version of the UIMA-AS Asynchronous Scale-out.
Extract the contents of the file you just downloaded to any permanent location you choose on your computer. Just don’t forget what folder you choose because you will need it later. A reasonable location on a Windows computer would be the root of the C: drive. For Linux or Mac users, you might choose /usr/local/ or /usr/share/.
### Setting the UIMA Environment Variable
You will need to set an environment variable called UIMA_HOME, the value of which should be the directory into which you just extracted the UIMA files. For example, on Windows, this might be C:\apache-uima-as-2.9.0

For help setting a the environment variable, see these examples for Windows, Mac, and Linux provided by Oracle here.



## Steps to run the system:
### The following steps run the initial file to xmi example

1. Start a UIMA-AS Broker.
	apache-uima-as-2.9.0/bin/startBroker.sh
2. Confirm that config\ServiceConfig.groovy points to the correct instance of running broker (brokerUrl)
3. Configure desired reader and listener configuration file
	(default is set to read in the example file and output an xmi)
4. Confirm that runClient.bat or .\runClient.sh points at desired reader and listener configuration file
5. Run Service

		.\runService.sh
		or    
		.\runService.bat   

   Note: Scaling up for larger datasets requires starting multiple services by repeating step 5 - running services in separate Windows PowerShell terminals

6. Run Client

		.\runClient.sh
		or
		.\runClient.bat

    Note: Scaling up for larger datasets requires starting multiple Clients. However, unlike with Service (just simply running it again), starting a new Client requires modification of the reader configuration file to ensure that the reader is accessing a different subset of records. Restarting a client without re-configuring a reader will lead to a simple duplication of processing.

### the .bat and .sh scripts need to be updated to point to the relevant readers and listeners when processing to/from a database. If the amount of documents to process is prohibitive, documents can first be pre-filtered to process only those containing one of the following terms. ['prone', 'proning', 'proneing', 'rotaprone', 'rotoprone', 'rotabed', 'rotobed']
