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
db_engine = ""
db_name = ""

String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
String url = "jdbc:sqlserver://"+db_engine+":1433;databasename="+db_name+";integratedSecurity=true"
String dbUser = ""
String dbPwd = ""

String query =  ""

Integer[] offset = [0,  1000]  //
batchsize = 30000
reader = new gov.va.vinci.leo.cr.SQLServerPagedDatabaseCollectionReader(
        driver,
        url,
        dbUser, dbPwd,
        query,
        idColumn.toLowerCase(),
        noteColumn.toLowerCase(),
        batchsize);
//      batchsize, offset[0], offset[1]) ;