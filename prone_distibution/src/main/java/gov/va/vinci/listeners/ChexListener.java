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


import gov.va.vinci.leo.listener.ChexSimanDatabaseListener;
import gov.va.vinci.leo.model.ChexSimanDataSourceConfiguration;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.model.NameValue;
import gov.va.vinci.leo.tools.ChexSimanUtils;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by vhaslcalbap on 10/4/2017.
 */
public class ChexListener extends ChexSimanDatabaseListener {

    public static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

    public ChexListener(ChexSimanDataSourceConfiguration simanDataSourceConfiguration) throws SQLException {
        super(simanDataSourceConfiguration);
        LOG.info(" Initializing " + this.getClass().getCanonicalName() + "\r\n"
                + simanDataSourceConfiguration.getDocumentSelectAllSql());
        try {
            validateSchemaAndCreateIfNeeded(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ChexListener newChexListener(String driver, String url,
                                               String chexDocumentTextSelectQuery,
                                               String chexSchema,
                                               String chexSuffix,
                                               String chexColumnPrefix,
                                               String chexColumnSuffix,
                                               ArrayList<String> inputTypes,
                                               int batchSize, boolean chexOverwrite
    ) throws SQLException {

        String[] typeList = (String[]) inputTypes.toArray(new String[inputTypes.size()]);

        DatabaseConnectionInformation dci = new DatabaseConnectionInformation(driver, url, "", "");
        ChexSimanDataSourceConfiguration csdsc = new ChexSimanDataSourceConfiguration(dci,
                chexDocumentTextSelectQuery,
                chexSchema,
                chexSuffix,
                chexColumnPrefix,
                chexColumnSuffix);
        return new ChexListener(csdsc, typeList, batchSize, chexOverwrite);
    }

    public ChexListener(ChexSimanDataSourceConfiguration simanDataSourceConfiguration,
                        String[] inputType, int batchSize, boolean deleteIfExists) throws SQLException {
        super(simanDataSourceConfiguration, inputType, batchSize);

        try {
            validateSchemaAndCreateIfNeeded(deleteIfExists);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void createConnection() throws SQLException {
        super.createConnection();
        preparedDocumentXrefStatement = conn.prepareStatement("INSERT INTO " +
                oldManSimanDataSourceConfiguration.schema + ".document_xref_example"
                + oldManSimanDataSourceConfiguration.tableSuffix +
                " ( guid, version, patient_sid, tiu_document_sid ) VALUES " +
                " ( ?, ?, ? ,?)");
    }

    protected void validateSchemaAndCreateIfNeeded(boolean deleteIfExists) throws SQLException, IOException {
        try {
            Connection connection = oldManSimanDataSourceConfiguration.getDataSource().getConnection();
            connection.prepareStatement(oldManSimanDataSourceConfiguration.getDocumentSelectAllSql()).execute();
            if (deleteIfExists)
                createSchema();
        } catch (SQLException e) {
            System.out.println("Exception from document select, creating schema.");
			/**/
            createSchema();
			/* */
        }

    }

    protected void createSchema() throws SQLException, IOException {
        String dbsSimanCreate = ChexSimanUtils.getCreateTablesSQL(
                oldManSimanDataSourceConfiguration.schema,
                oldManSimanDataSourceConfiguration.tableSuffix,
                ChexSimanUtils.SchemaType.SQL_SERVER);

        log.info("\r\n" + dbsSimanCreate);
        oldManSimanDataSourceConfiguration.getDataSource()
                .getConnection()
                .prepareStatement(dbsSimanCreate).execute();
		/* */
        log.info("Creating Siman database schema in "
                + oldManSimanDataSourceConfiguration.schema + ".tables"
                + oldManSimanDataSourceConfiguration.tableSuffix);
    }

    @Override
    protected NameValue insertDocumentXref(CAS arg0) throws SQLException {
        String tiuDocumentSID = (this.docInfo == null) ? "" : this.docInfo.getID();
        String patientSID = (this.docInfo == null || this.docInfo.getRowData() == null) ? "" : this.docInfo
                .getRowData(2);
        String recordUUID = UUID.randomUUID().toString();
        PreparedStatement ps = conn
                .prepareStatement("INSERT INTO [" +
                        oldManSimanDataSourceConfiguration.schema + "].[document_xref_example"
                        + oldManSimanDataSourceConfiguration.tableSuffix + "] " +
                        " ( [guid],[version], patient_sid, [tiu_document_sid] ) VALUES " +
                        " ( ?, ?, ?,? )");

        ps.setString(1, recordUUID);
        ps.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
        ps.setString(3, patientSID);
        ps.setString(4, tiuDocumentSID);
        ps.execute();

        return new NameValue("[" + oldManSimanDataSourceConfiguration.schema + "].[document_xref_example"
                + oldManSimanDataSourceConfiguration.tableSuffix + "]", recordUUID);
    }

}