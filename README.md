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
- Run RStudio
    - `code $HOME/Documents/github_repos/OHDSI_prone/OHDSI_prone.code-workspace`

## Installation and Download of UIMA-AS and LEO 

found in prone_distribution\dist_README
