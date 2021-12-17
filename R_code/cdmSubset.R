# TODO: 
# 1. Fix paths for relative paths for Study package. Solvable with something like this
#     csvFile <- system.file("settings", "Table1Specs.csv", package = "LegendT2dm")
# 2. Explicitly mention that NOTE_NLP, location, care_site, Provider, payer_plan_period, cost, subsetting is not supported

subsetByPersonId <- function(cdmTable, cohortId, cohortTable, cdmDatabaseSchema, 
                       resultDatabaseSchema, connectionDetails) {
  
  "
  Subset CDM tables by only using person_id
  "
  
  renderedSql <- SqlRender::render(SqlRender::readSql("inst/sql/sql_server/byPersonId.sql"),
                                   resultDatabaseSchema=resultDatabaseSchema,
                                   cdmDatabaseSchema=cdmDatabaseSchema,
                                   cdmTable=cdmTable,
                                   cohortTable=cohortTable,
                                   cohortId=cohortId,
                                   warnOnMissingParameters = TRUE)
  
  translatedSql <- SqlRender::translate(sql=renderedSql,
                                        targetDialect = connectionDetails$dbms,
                                        tempEmulationSchema = target_database_schema)
  
  print(translatedSql)
  # DatabaseConnector::executeSql(connection=DatabaseConnector::connect(connectionDetails),
  #                               sql=translatedSql)
}


getDateColumn <- function(cdmTable){
  "
  Return the proper date column to use per each table
  "
  dateColumnsDf <- read.csv("inst/settings/dateTables.csv")
  dateColumn <- dateColumnsDf$dateColumn[dateColumnsDf$cdmTable == cdmTable]
  return(dateColumn)
}

getDateTables <-  function(){
  "
  Return the tables to be subset by dates and person_id
  "
  dateTablesDf <- read.csv("inst/settings/dateTables.csv")
  dateTables <- data.frame(dateTablesDf$cdmTable)
  return(dateTables)
}

getPersonIdTables <-  function(){
  "
  Return the tables to be subset by only person_id
  "
  personIdTablesDf <- read.csv("inst/settings/personIdTables.csv")
  personIdTables <- data.frame(personIdTablesDf$cdmTable)
  return(personIdTables)
}

subsetByPersonIdAndDate <- function(cdmTable, cohortId, cohortTable, cdmDatabaseSchema, 
                                  resultDatabaseSchema, connectionDetails) {
  
  "
  Subset the CDM table by using person_id and cohort start and end date
  "
  
  # get the appropriate date column
  dateColumn <- getDateColumn(cdmTable = cdmTable)
  
  renderedSql <- SqlRender::render(SqlRender::readSql("inst/sql/sql_server/byPersonAndDate.sql"),
                                   resultDatabaseSchema=resultDatabaseSchema,
                                   cdmDatabaseSchema=cdmDatabaseSchema,
                                   cdmTable=cdmTable,
                                   cohortTable=cohortTable,
                                   cohortId=cohortId,
                                   dateColumn=dateColumn,
                                   warnOnMissingParameters = TRUE)
  
  translatedSql <- SqlRender::translate(sql=renderedSql,
                                        targetDialect = connectionDetails$dbms,
                                        tempEmulationSchema = target_database_schema)
  
  # DatabaseConnector::executeSql(connection=DatabaseConnector::connect(connectionDetails),
  #                               sql=translatedSql)
  print(translatedSql)
}


subsetCDM <- function(cohortId, cohortTable, cdmDatabaseSchema, 
                      resultDatabaseSchema, connectionDetails) {
  
  "
  Subset the CDM based on a cohort from ATLAS
  "
  
  # Subset by person_id
  personIdTables <- getPersonIdTables()
  apply(personIdTables, 1, subsetByPersonId,  
        cohortId=cohortId,
        cohortTable=cohortTable, 
        cdmDatabaseSchema=cdmDatabaseSchema, 
        resultDatabaseSchema=resultDatabaseSchema, 
        connectionDetails=connectionDetails)
  
  # Subset by person_id and dates
  dateTables <- getDateTables()
  apply(dateTables, 1, subsetByPersonIdAndDate,  
        cohortId=cohortId,
        cohortTable=cohortTable, 
        cdmDatabaseSchema=cdmDatabaseSchema, 
        resultDatabaseSchema=resultDatabaseSchema, 
        connectionDetails=connectionDetails)
}
