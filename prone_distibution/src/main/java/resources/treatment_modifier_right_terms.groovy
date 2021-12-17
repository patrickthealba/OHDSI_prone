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


    "Exclude:Irrelevant_Right" {
        expressions = [



                //PT is prone to.....
                '\\bto\\s+\\w+',
                'to\\s*falling',
                'to\\s*urinary',
                'to\\s*bleed',
                'to\\*get\\s*a\\s*uti'


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier_Right"
        concept_feature_value = "Exclude:Irrelevant"
    }

    "Treated:Continuous" {
        expressions = [


                'as\\s*able'


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier_Right"
        concept_feature_value = "Treated:Continuation"
    }

}
