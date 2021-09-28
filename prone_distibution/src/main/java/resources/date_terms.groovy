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
package resources

import gov.va.vinci.leo.regex.types.RegularExpressionType

name = "RegexAnnotator"

configuration { /* All configuration for this annotator. */
    defaults {
        /* Global for all configrations below if a property specified here is not overridden in a section below. */
        outputType = RegularExpressionType.class.canonicalName
        groupFeatureName = "group"
        case_sensitive = false
        matchedPatternFeatureName = "pattern"
        concept_feature_name = "concept"
    }

    /*
    Dates found as single instance, or in a range as Date + middle_term + date

     */


    "Date" {
        expressions = [

                //Common Date formats
                //07-19-89
                '\\d(\\d)?-\\d(\\d)?-\\d(\\d)?'
                //07/19/89
                ,'\\d(\\d)?/\\d(\\d)?/\\d(\\d)?'
                //7\19\89
                ,'\\d(\\d)?\\\\\\d(\\d)?\\\\\\d(\\d)?'
                //07/16/2006'
                ,'\\d(\\d)?(\\|-|/)\\d(\\d)?(\\|-|/)\\d\\d\\d\\d'
                //07/2006'
                ,'\\d\\d(\\|-|/)\\d\\d\\d\\d'
                //1-9/\\d\\d'
                ,'\\b0?[1-9](\\|-|/)\\d\\d?\\d?\\d?'
                ,'\\b\\d\\d(\\|-|/)0?[1-9]\\b'
                //11/18 Digits in teens'
                ,'\\b1\\d((\\|-|/))1\\d\\b'
                //12-07'
                ,'\\b[10-12](\\|-|/)\\d\\d\\b'
                ,'\\b\\d\\d(\\|-|/)[10-12]\\b'
                //in 07/26'
                ,'in\\s*\\d\\d(\\|-|/)\\d\\d'



        ]
        outputType = "gov.va.vinci.vent.types.Vent_Date"
        concept_feature_value = "date"
    }

    "Date_middle" {
        expressions = [


                '-',
                '-?-\\s*>',
                'to'


        ]
        outputType = "gov.va.vinci.vent.types.Vent_Date_Middle"
        concept_feature_value = "date"
    }

}
