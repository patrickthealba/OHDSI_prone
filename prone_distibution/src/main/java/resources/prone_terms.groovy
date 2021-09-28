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

    "Prone" {
        expressions = [


                'prone',
                'proneing',
                'proning',
                'prone\\s*position(ing)?',
                'face\\s*down',
                //'pronation', //Always irrelevant/related to physical therapy (wrist/ankle pronation)


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Term"
        concept_feature_value = "prone"
    }
    "Self_Proning" {
        expressions = [


                'self-?pron(ed|ing|eing|e)',
                'self pron(ed|ing|eing|e)',
                'awake(-|\\s*)pron(ed|ing|eing|e)',
                'awake\\s*self(-|\\s*)pron(ed|ing|eing|e)'

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Term"
        concept_feature_value = "self_prone"
    }

    "Prone_to_supine" {
        expressions = [


                'prone\\s*to\\s*supine',
                'supine\\s*to\\s*prone',
                'PRONING\\s*TO\\s*SUPINE',


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Term"
        concept_feature_value = "prone_to_supine"
    }

    "deprone" {
        expressions = [


                'deprone',
                'unprone'


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Term"
        concept_feature_value = "deprone"
    }

    "Proned" {
        expressions = [
                 //'proned' in the past tense of a verb behaves differently
                'proned',

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Term"
        concept_feature_value = "proned"
    }

    "semi_prone" {
        expressions = [


                'semi-?proned?',

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Term"
        concept_feature_value = "semi_prone"
    }

    "roto_prone" {
        expressions = [



                'rot[ao]prone',
                'rot[ao]bed',
                'ROTO PRONE BED',
                'ROTOPRONE BED',
                'rota\\s*prone\\s*bed'

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Term"
        concept_feature_value = "roto_prone"
    }

    "Exclude:Irrelevant" {
        //This removes some of the most frequent obviously irrelevant cases
        expressions = [


                'accident(-|\\s*)prone',
                'stress(-|\\s*)prone',
                'infection(-|\\s*)prone',
                'moisture(-|\\s*)prone',
                'prone\\s*(arm|hip|leg|wrist|ankle)\\s*extension',


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Exclude"
        concept_feature_value = "Exclude:Irrelevant"
    }
/*
    "Not_prone" {
        expressions = [

            //antonyms ultimately not required
                'decumbent',
                'recumbent',
                'supine'


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Term"
        concept_feature_value = "not_prone_ventilation"
    }

*/
}
