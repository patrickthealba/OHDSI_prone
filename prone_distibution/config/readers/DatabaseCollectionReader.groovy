db_engine = ""
db_name = ""

batches = [0, 100]
batchSize = 20000;
idColumn = ""
noteColumn = ""

query =" SELECT distinct [DocID], ReportText FROM [note].[note_table] "



/************************************************************/
/***** You should not need to change the code below *********/
/************************************************************/
String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
String url = "jdbc:sqlserver://"+db_engine+":1433;databasename="+db_name+";integratedSecurity=true"
String dbUser = ""
String dbPwd = ""
reader = new gov.va.vinci.leo.cr.BatchDatabaseCollectionReader(driver, url, dbUser, dbPwd, query, idColumn.toLowerCase(), noteColumn.toLowerCase(), batches[0], batches[1], batchSize)
