package gov.va.vinci.listeners;

/*
 * #%L
 * NLP System for proning therapy
 * %%
 * Copyright (C) 2010 - 2021 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import gov.va.vinci.leo.listener.BaseDatabaseListener;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;

import java.util.*;

public class LogDatabaseListener extends BaseDatabaseListener {
    private static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());
    protected ArrayList<ArrayList<String>> fieldList;
    // table name for this listeners
    protected String outTableName;
    // database name for this listeners
    protected String dbsName;

    /**
     * Base gov.va.vinci.leo.listeners.
     *
     * @param databaseConnectionInformation information on the database to connect to
     * @param preparedStatementSQL          the prepared statement to use for inserts. For example: insert into myTable ( col1, col2, col2) values (?, ?, ?)
     * @param batchSize                     the size of batches to do in each insert. Typically 1000 is a good batch size.
     * @param validateConnectionEachBatch   if true, the databaseConnectionInformation.validationQuery will be run before each batch in addition to making sure the connection
     */

    public LogDatabaseListener(DatabaseConnectionInformation databaseConnectionInformation,
                                     String preparedStatementSQL,
                                     int batchSize,
                                     boolean validateConnectionEachBatch,
                                     ArrayList<ArrayList<String>> fieldList,
                                     String dbsName,
                                     String tableName

    ) {

        super(databaseConnectionInformation, preparedStatementSQL);//, batchSize, validateConnectionEachBatch);
        this.fieldList = fieldList;
        this.dbsName = dbsName;
        this.outTableName = tableName;
        setBatchSize(batchSize);
        setValidateConnectionEachBatch(validateConnectionEachBatch);
    }

    public static LogDatabaseListener createNewListener(
            String driver, String url, String dbUser, String dbPwd,
            String dbsName, String tableName, int batchSize) {
        DatabaseConnectionInformation databaseConnectionInformation = new DatabaseConnectionInformation(
                driver, url, dbUser, dbPwd);
        ArrayList<ArrayList<String>> fieldList = new ArrayList<>();
        fieldList.add(new ArrayList<>(Arrays.asList("DocID","0","bigint")));
        fieldList.add(new ArrayList<>(Arrays.asList("Timestamp","-1","datetime")));

        String preparedStatement = createPreparedStatementSQL(dbsName, tableName, fieldList);
        boolean validateConnectionEachBatch = true;
        return new LogDatabaseListener(databaseConnectionInformation,
                preparedStatement,
                batchSize,
                validateConnectionEachBatch,
                fieldList,
                dbsName,
                tableName
        );
    }

    /**
     * TODO: Move this static method to BaseDatabaseListener. Serves to create insert statement based on the database
     * and table name
     *
     * @param dbsName
     * @param tableName
     * @param fieldList
     * @return
     */

    public static String createPreparedStatementSQL(String dbsName,
                                                    String tableName,
                                                    ArrayList<ArrayList<String>> fieldList) {
        String statement = "INSERT INTO " + dbsName + "." + tableName + " ( ";
        String values = "";
        for (ArrayList<String> entry : fieldList) {
            statement = statement + entry.get(0) + ", ";
            values = values + " ?,";
        }
        statement = statement.substring(0, statement.length() - 2)
                + " ) VALUES ( " + values.substring(0, values.length() - 1)
                + " ) ;";
        log.info("PreparedStatement : " + statement);
        return statement;
    }

    /**
     * For each input annotation type
     *
     * @param aCas
     * @return
     */

    @Override
    protected List<Object[]> getRows(CAS aCas) {
        ArrayList rowList = new ArrayList<Object[]>();
        Object[] arr = {docInfo.getID(), new java.sql.Timestamp(System.currentTimeMillis())};
        rowList.add(arr);
        return rowList;
    }

    /**
     * Creates the table structure.
     *
     * @param dropFirst If drop, the table is dropped before creation.
     * @param dbsName   The database name (needed by some databases for the create statement)
     * @param tableName The resulting table name
     */

    public void createTable(boolean dropFirst,
                            String dbsName,
                            String tableName
    ) {
        String createStatement = createCreateStatement(dbsName, tableName, fieldList);
        try {
            this.createTable(createStatement, dropFirst, tableName);
        } catch (Exception e) {
            log.error("Creating a table failed \r\n" + e.getMessage());
        }
        log.info("Created a new table: \r\n" + createStatement);
    }//createTable method

    /**
     * @param dropFirst
     */

    public void createTable(boolean dropFirst) {
        if (StringUtils.isNotBlank(this.dbsName)
                && StringUtils.isNotBlank(this.outTableName)
                && this.fieldList.size() > 0) {
            this.createTable(dropFirst, this.dbsName, this.outTableName);
        } else {
            log.error("Creating table failed because table name or database name or fields are not populated.");
        }
    }

    /**
     * TODO: propose to add this method to BaseDatabaseListener
     * Protected method to build a create table statement.
     *
     * @param dbsName   the database name if needed.
     * @param tableName the table name.
     * @param fieldList A map of column names (key) and types (value) for example "id", "varchar(20)"
     * @return The sql create statement.
     */

    public String createCreateStatement(String dbsName,
                                        String tableName,
                                        ArrayList<ArrayList<String>> fieldList) {
        String statement = "CREATE TABLE " + dbsName + "." + tableName + " ( ";
        for (ArrayList<String> entry : fieldList) {
            statement = statement + entry.get(0) + " " + entry.get(2) + ", ";
        }
        statement = statement.substring(0, (statement.length() - 2)) + " ) ;";
        return statement;
    }
}
