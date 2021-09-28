package listeners

import gov.va.vinci.listeners.BasicCsvListener;

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

fieldList = [
        ["DocID", "-1", "bigint"],
       /* ["Pattern", "-1", "varchar(210)"],
        ["Snippets", "-1", "varchar(1000)"],
        ["Concept", "-1", "varchar(50)"]
        */
        //["TIUDocumentSID", "0", "bigint"],
        ["Term", "-1", "varchar(150)"],
        ["Term_Type", "-1", "varchar(5000)"],
       // ["Section", "-1", "varchar(500)"],
        ["Term_Modifier", "-1", "varchar(500)"],
        ["Phrase_Modifier", "-1", "varchar(500)"],
        ["Modifier_Term", "-1", "varchar(500)"],
        ["Temporality_Modifier", "-1", "varchar(500)"],
        ["Date_1", "-1", "varchar(500)"],
        ["Date_2", "-1", "varchar(500)"],
        ["Anchored_Sentence", "-1", "varchar(5000)"],
        ["SpanStart", "-1", "int"],
        ["SpanEnd", "-1", "int"],
        ["Snippets", "-1", "varchar(5000)"]
]

File filePath = new File("src//test//resources//output//csv_out.csv");
listener = new BasicCsvListener(filePath, true , fieldList, "gov.va.vinci.vent.types.Prone_Pattern");
listener.writeHeaders()
