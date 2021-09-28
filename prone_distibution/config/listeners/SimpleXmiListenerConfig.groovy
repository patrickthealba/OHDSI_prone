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
package listeners

import gov.va.vinci.leo.listener.SimpleXmiListener
import gov.va.vinci.leo.tools.LeoUtils

String timeStamp = LeoUtils.getTimestampDateDotTime().replaceAll("[.]", "_")
String xmiDir =  "src//test//resources//output"

if(!(new File(xmiDir)).exists()) (new File(xmiDir)).mkdirs()

listener = new SimpleXmiListener(new File(xmiDir))
listener.setTypeSystemDescriptor(new File("generated-types/resources/types/TypeSystem.xml"))
listener.setLaunchAnnotationViewer(true)

