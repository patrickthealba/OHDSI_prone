setwd("/ohdsi/workdir/COVID_PRONE/ProneDeploy_09_29/")
#load("Prone_09_29_1316.RData" -- replace with most recent run. 

bqDriverPath = "~/workdir/BQ_driver/"
work_project_id = 'som-nero-nigam-starr'
work_dataset_id = "eminty_explore"
jsonPath = "~/workdir/application_default_credentials.json"
cdm_project_id = "som-rit-phi-starr-prod"
cdm_dataset_id = "starr_omop_cdm5_deid_1pcent_lite_latest"
cdmDatabaseSchema <- "`som-rit-phi-starr-prod.starr_omop_cdm5_deid_1pcent_lite_latest`"
cohortDatabaseSchema <- '`som-nero-nigam-starr.eminty_explore`'


install.packages("devtools")
library(devtools)
devtools::install_github("jdposada/BQJdbcConnectionStringR")

library(BQJdbcConnectionStringR)

connectionString <-  BQJdbcConnectionStringR::createBQConnectionString(projectId = work_project_id,
                                                                       defaultDataset = work_dataset_id,
                                                                       authType = 2,
                                                                       jsonCredentialsPath = jsonPath)

connectionDetails <- DatabaseConnector::createConnectionDetails(dbms="bigquery",
                                                                connectionString=connectionString,
                                                                user="",
                                                                password='',
                                                                pathToDriver = bqDriverPath)

connection <- DatabaseConnector::connect(connectionDetails = connectionDetails) 

SQL = "SELECT * FROM `som-nero-nigam-starr.prone_nlp.all_prone_notes`"

all_prone_notes = DatabaseConnector::querySql(connection = connection, sql = SQL)
# DatabaseConnector::disconnect(connection)

getwd()

write_notes <- function(x, output) {
  folder ="/ohdsi/workdir/COVID_PRONE/ProneDeploy_09_29/ProneNotes/"
  note_id <- x['NOTE_ID']
  note_text <- x['NOTE_TEXT']
  fileName <- paste0(folder, note_id, ".txt")
  cat(note_text, file=fileName , append = F, fill = F)
}

write_notes <- function(x, output) {
  folder ="/ohdsi/workdir/COVID_PRONE/ProneDeploy_09_29/ProneNotes/"
  note_id <- x['NOTE_ID']
  note_text <- x['NOTE_TEXT']
  fileName <- paste0(folder, note_id, ".txt")
  cat(note_text, file=fileName , append = F, fill = F)
}
