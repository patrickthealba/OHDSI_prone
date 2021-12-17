# PRONE NLP

Identifying prone position for Hospitalized patients with COVID using Natural Language Processing

## Pre-requisites

- Docker install
    - https://docs.docker.com/engine/install/ubuntu/ 
- R basic install
```
apt-get update
apt-get install r-base r-base-dev
```

## Steps for GCloud
Repository for files and code related to the OHDSI Prone Incident study 

- Create the gcloud directory in your computer if it does not exists 
    - `mkdir $HOME/.config/gcloud`
- Get the application default credentials file inside your computer 
    - `docker run -ti -v $HOME/.config/gcloud:/root/.config/gcloud google/cloud-sdk gcloud auth application-default login`

## Steps for General RUN
- Clone the repo
    - `docker run -ti --rm -v $HOME/Documents/github_repos:/git alpine/git clone https://github.com/patrickthealba/OHDSI_prone.git && sudo chmod -R 777 $HOME/Documents/github_repos/OHDSI_prone`
- Download the BQ JDBC drivers from [here](https://cloud.google.com/bigquery/docs/reference/odbc-jdbc-drivers) and uncompress them on `$HOME/Documents/github_repos/OHDSI_prone/bq_jdbc`
    - `unzip SimbaJDBCDriverforGoogleBigQuery42_1.2.21.1025.zip -d $HOME/Documents/github_repos/OHDSI_prone/bq_jdbc`  
- Run RStudio
    - Run `Rscript $HOME/Documents/github_repos/OHDSI_prone/run_docker.R`
    - Open a browser and type `localhost:9001`
    - Type username and password. The default for both is `ohdsi`
    - Navigate and make `/workdir` your current working directory
    - Fix permissions on workdir folder
        - `sudo chmod -R 777 workdir/`
    - Source `R_code/testRun.R` to verify everything is ok on your connection and packages


## Installation and Download of UIMA-AS and LEO 

found in prone_distribution\dist_README
