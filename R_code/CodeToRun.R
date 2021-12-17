# TODO: Write code to iterate over the dataframe of retrieved notes to write each individual file
# likely I could use apply

working_directory <- "/workdir/workdir"
setwd(working_directory)
r_env_cache_folder <- "/workdir/renv_cache"
renv_package_version <- '0.13.2'
renv_vesion <- "v5"
r_version <- "R-4.0"
linux_version <- "x86_64-pc-linux-gnu"
jsonPath <- "/workdir/gcloud/application_default_credentials.json"
bqDriverPath <- "/workdir/workdir/bq_jdbc"
project_id <- "allennlp"
dataset_id <- "jposada_explore"
cdm_database_schema <- "`bigquery-public-data.cms_synthetic_patient_data_omop`"
vocabulary_database_schema <- cdm_database_schema
target_database_schema <- "`allennlp.jposada_explore`"
target_cohort_table <- "cohort"
target_cohort_id <- "141"
notes_folder ="/workdir/workdir/ProneNotes/"

renv_final_path <- paste(r_env_cache_folder,
                         renv_vesion,
                         r_version,
                         linux_version,
                         sep="/")


.libPaths(renv_final_path)


connectionString <-  BQJdbcConnectionStringR::createBQConnectionString(projectId = project_id,
                                              defaultDataset = dataset_id,
                                              authType = 2,
                                              jsonCredentialsPath = jsonPath)

connectionDetails <- DatabaseConnector::createConnectionDetails(dbms="bigquery",
                                                                connectionString=connectionString,
                                                                user="",
                                                                password='',
                                                                pathToDriver = bqDriverPath)

# COVID Hospitalized Patients cohort

# Create Cohort Table
renderedSql <- SqlRender::render(SqlRender::readSql("inst/sql/sql_server/CohortTable.sql"),
                                 cdmDatabaseSchema=target_database_schema,
                                 warnOnMissingParameters = TRUE)

translatedSql <- SqlRender::translate(sql=renderedSql,
                                      targetDialect = connectionDetails$dbms,
                                      tempEmulationSchema = target_database_schema)

# DatabaseConnector::executeSql(connection=DatabaseConnector::connect(connectionDetails),
#                             sql=translatedSql)

# Run the Charybdis Cohort
renderedSql <- SqlRender::render(SqlRender::readSql("inst/sql/sql_server/ID139_V1.sql"),
                                 cdm_database_schema=cdm_database_schema,
                                 vocabulary_database_schema=vocabulary_database_schema,
                                 target_database_schema=target_database_schema,
                                 target_cohort_table=target_cohort_table,
                                 target_cohort_id=target_cohort_id,
                                 warnOnMissingParameters = TRUE)

translatedSql <- SqlRender::translate(sql=renderedSql,
                                      targetDialect = connectionDetails$dbms,
                                      tempEmulationSchema = target_database_schema)

# DatabaseConnector::executeSql(connection=DatabaseConnector::connect(connectionDetails),
#                             sql=translatedSql)


# Subset the CDM

subsetCDM(cohortId=target_cohort_id,
          cohortTable=target_cohort_table,
          cdmDatabaseSchema=cdm_database_schema, 
          resultDatabaseSchema=target_database_schema,
          connectionDetails=connectionDetails)


# Download the clinical notes from the subset

renderedSql <- SqlRender::render("SELECT * FROM @resultDatabaseSchema.note",
                                 resultDatabaseSchema=resultDatabaseSchema,
                                 warnOnMissingParameters = TRUE)

translatedSql <- SqlRender::translate(sql=renderedSql,
                                      targetDialect = connectionDetails$dbms,
                                      tempEmulationSchema = target_database_schema)

all_prone_notes = DatabaseConnector::querySql(connection = connection, sql = SQL)

write_notes <- function(x, notes_folder) {
  note_id <- x['NOTE_ID']
  person_id <- x['PERSON_ID']
  note_text <- x['NOTE_TEXT']
  fileName <- paste0(folder, note_id, ".txt")
  cat(note_text, file=fileName , append = F, fill = F)
}


