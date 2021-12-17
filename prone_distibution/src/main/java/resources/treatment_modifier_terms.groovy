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




    "Treated:Start" {
        expressions = [

                //'He was', this should be caught in the pattern on line 55
               // 'this\\s*morning',
               // 'this\\s*am',
                'today',
                'cooperative with',
                //Patient with prone term and no other, requires boundary for pt if the more complete patterns aren't identified
                // Patient prone vs... patient is, patient is now, patient laying, patient is laying, patient is now laying etc..
                '(\\bpt\\.?|vet|veteran|patient|patinent)',
                '\\bin\\b', //in prone position
                '\\bin\\s*a',
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(is|were|have\\s*been|was|has\\s*been|is\\s*now|now|are)',// possible pattern annotator if needed
                '(is|were|have\\s*been|was|has\\s*been|is\\s*now|now|are)\\s*doing', //doing self-proning
                '(placed|put|laying|lying)\\s*(into\\s*a|into|on|in\\s*a|in\\s*the|in)',//placed in a
                //pt in
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(into\\s*a|into|on|in\\s*a|in\\s*the|in)',
                //pt placed in
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(placed|put|laying|lying)\\s*(into\\s*a|into|on|in\\s*a|in\\s*the|in)',
                //pt laying
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(placed|put|laying|lying)',
                //pt is laying
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(is|were|have\\s*been|was|has\\s*been|is\\s*now|now|are)\\s*(placed|put|laying|lying)',// possible pattern annotator if needed
                //pt is in
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(is|were|have\\s*been|was|has\\s*been|is\\s*now|now|are)\\s*(into\\s*a|into|on|in\\s*a|in\\s*the|in)',// possible pattern annotator if needed
                //pt is laying in
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(is|were|have\\s*been|was|has\\s*been|is\\s*now|now|are)\\s*(placed|put|laying|lying)\\s*(into\\s*a|into|on|in\\s*a|in\\s*the|in)',// possible pattern annotator if needed
                'able\\s*to(\\s*be)?',
                'recently',
                'since',
                '(and\\s*)?on room air',
                'got\\s*into',
                '\\bon',
                'complete',  //complete proning
                'post-?',  //postproning
                'Unable\\s*to\\s*(fully\\s*)?assess\\s*(due\\s*to|d/t)',
                'uta\\s*(due\\s*to|d/t)',
                'indication:',
                'initiated',
                'was',
                'was\\s*(laying|lying)',
                'is\\s*(laying|lying)',
                'getting',
                'back\\s*to',
                'manual', //manual proning
                'thera', //proning therapy
                'therapy', //proning therapy
                '(was|is)\\s*positioned',
                'placed',
                'turned\\s*(in|to)',
                'turned\\s*(in|to)\\s*a',
                'called\\s*for',
                'began',
                'requir(ed|ing)',
                'while',
                'begun',
                'starting',
                'began',
                'start',
                'staretd',
                'strted',
                'startd',
                'starts',
                'satrted',
                'statrted',
                'statred',
                'started',
                'began',
                'starting',
                'beginning',
                'given',
                'bagan',
                'began',
                'begin',
                'begining',
                'beginning',
                'begun',
                'commenced',
                'initiatied',
                'initiation',
                'inititated',
                'inititation',
                'instilled',
                'intiated',
                'intiation',
                'intitiation',
                'staretd',
                'start',
                'startd',
                'started',
                'startign',
                'startin',
                'starting',
                'starts',
                'statred',
                'statrted',
                'strated',
                'strating',
                'strted',
                'strting',
                'switched',



        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Treated:Start"
    }

    "Treated:Restarted" {
        expressions = [

                '(switched|changed|repositioned)\\s*(from|to|into)?',
                're-?started',
                're-?initiated',
                'restarted',
                'resumed',
                'back\\s*in(\\s*a)?',
                're-?initiated',
                're-?started',
                'reinitiate',
                'reinitiated',
                'reinitiating',
                'reinitiation',
                'reinstitute',
                'reinstituted',
                'reinstituting',
                'reinstitution',
                'reintroduction',
                'restared',
                'restart',
                'restarting',
                'restrated',
                'resume',
                'resumed',
                'resuming',
                '(repeat|rpt)',        //repeat proning


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Treated:Restarted"
    }

    "Treated:Continuation" {
        expressions = [


                'to\\s*leave',
                'remains',
                'no\\s*issue', //proning no issue
                'is\\s*lying',
                'position verified as',
                'continues\\s*to',
                'continues\\s*to\\s*be',
                'Continues\\s*to\\s*tolerate',
                'continued\\s*to\\s*(intermittently\\S*)?',
                'continu(e|es|ing|ed)',
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*occasionally',
                //Tolerating
                'tollerating',
                'tolerated',
                'as\\s*tolerated',
                'toelrating',
                'tolorating',
                'tolerateing',
                'tolerated\\s*\\d\\s(hrs|hours)\\s*of',
                'will\\s*(keep|resume)',
                'without\\s*issue',
                //in/on/while
                'when',
                'has\\s*been',
                'is\\s*being',
                'assisted\\s*with',
                'compliant\\s*with',
                // Bulk Modifier update
                'already',
                'ccontinue',
                'cntinue',
                'commenced',
                'conintue',
                'coninue',
                'conitinue',
                'conitnue',
                'conitue',
                'conntinue',
                'cont',
                'cont',
                'con\'t',
                'contd',
                'cont\'d',
                'conti',
                'contiinue',
                'contine',
                'contineu',
                'continie',
                'continiue',
                'continnue',
                'continu',
                'Continue(\\s*with)?',
                'continued',
                'continues',
                'Continues\\s*as',
                'continuie',
                'continuing',
                'continune',
                'continuue',
                'contiue',
                'contiune',
                'contninue',
                'contniue',
                'contnue',
                'contunue',
                'coontinue',
                'cotinue',
                'cotninue',
                'currently',
                'curently',
                'curerntly',
                'currenlty',
                'currenly',
                'currentl',
                'currentlly',
                'curretnly',
                'currnetly',
                'currrently',
                'cuurently',
                'dependant',
                'dependent',
                'given',
                'now',
                'ocntinue',
                'ontinue',
                'presently',
                'reamains',
                'reamins',
                'remain',
                'remained',
                'remaines',
                'remais',
                'remaisn',
                'remans',
                'remian',
                'remianed',
                'remians',
                'remins',
                'resume',
                'resumed',
                'resumes',
                'resuming',
                'staretd',
                'start',
                'startd',
                'started',
                'starting',
                'starts',
                'statred',
                'statrted',
                'stays',
                'still',
                'strted',
                'toelrating',
                'tolerated',
                'tolerateing',
                'tolerating',
                'tollerating',
                'tolorating',
                'toelrated',
                'toelrating',
                'toerated',
                'toerating',
                'tolarated',
                'tolarating',
                'tolearating',
                'tolearted',
                'tolearting',
                'toleated',
                'toleating',
                'toleraing',
                'toleraitng',
                'toleratd',
                'tolerate',
                'tolerated',
                'tolerateing',
                'tolerates',
                'toleratig',
                'toleratin',
                'toleration',
                'tolerationg',
                'tolering',
                'tolertaed',
                'tolertaing',
                'tolertating',
                'tolerted',
                'tolerting',
                'tollerated',
                'tollerating',
                'tolorated',
                'tolorates',
                'tolorating',
                'tolrated',
                'tolrating'


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Treated:Continuation"
    }

    "Treated:TemplatedNote" {
        expressions = [

                //Possibly generalizable
                'PATIENT POSITION:',
                'POSITION:',
                'POSITION is',
                'Body\\s*positioning:',
                //Likely VA Specific
                'Positioning Interventions \\(Breathing/Oxygenation/Airway Clearance\\):?',
                'Position\\s*interventions\\s*\\(Breathing/Oxygenation/Airway Clearance\\):?'

                //Most frequent template here is done in the pattern annotator for date and time extraction
                //Set to pattern annotator or extracted dates
                //'Body\\s*Positioning\\s*\\(\\d\\d?/\\d\\d?\\s*\\d\\d?:\\d\\d?\\s*(a|p)M\\s*\\)\\s*--',
                //'Patient Positioning Body Positioning (1/27 6:00 AM )--'


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Treated:TemplatedNote"
    }

    "Treated:Condition" {
        expressions = [

                 //Condition changes after/as aresult of
                'desaturat(ed|ing)\\s*(after|with|while|in\\s*the)',
                'improv(ed|ing)\\s*(after|with|while|in\\s*the)',
                'improv(ed|ing)\\s*oxygenation\\s*(after|with|while|in\\s*the)',
                'to\\s*improve\\s*(oxygenation|o2)',
                'with\\s*improvement\\s*of\\s*(oxygenation|o2)'

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Treated:Condition"
    }


    "Treated:SupplementalO2" {
        expressions = [


                '(and\\s*|and\\s*remains\\s*)(with|on)\\s*(HFNC|high\\s*flow|hiflow|nc|nasal\\s*cann?ula|supplemental\\s*O2|c-?pap|bi-?pap|nrb|non-?rebreather|mask)',
                '(HFNC|high\\s*flow|hiflow|nc|nasal\\s*cann?ula|supplemental\\s*O2|c-?pap|bi-?pap|nrb|non-?rebreather|mask)\\s*(and|&)',
                '(HFNC|high\\s*flow|hiflow|nc|nasal\\s*cann?ula|supplemental\\s*O2|c-?pap|bi-?pap|nrb|non-?rebreather|mask),',
                '(HFNC|high\\s*flow|hiflow|nc|nasal\\s*cann?ula|supplemental\\s*O2|c-?pap|bi-?pap|nrb|non-?rebreather|mask)\\s*(w/|with)',

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Treated:SupplementalO2"
    }

    "Treated:MechanicalVentilation" {
        expressions = [


                '/mech\\s*ventilated', //proned/mech ventilated
                //he was paralyzed and
                'on\\s*vent',
                '(paralyzed|intubated|sedated|ventilated)\\s*(and|,)?',


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Treated:MechanicalVentilation"
    }

    "Treated:Duration" {
        expressions = [


                '\\d\\s*days\\s*of',    //4 days of proning
                'x\\s*\\d\\d?\\s*(hours|hrs)', //Proned x 5 last, proned x4 days
                'x\\s*\\d\\d?(h|hours|hrs|mins?|minutes)', //
                '\\d\\d?\\s*hrs/day', //pt being proned 16hrs/day.
                'at\\s*least\\s*\\d\\d?\\s*hrs/day', //pt being proned 16hrs/day.
                '(for\\s)?(approx|approximately|about|at\\s*least|close\\s*to)\\s*\\d\\d?\\s*(mins?|minutes|hrs|hours)',//proned for 5 hours
                '24h/day',
                'close\\s*to\\s*24h/day',


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Treated:Duration"
    }

    "Treated:Stopped" {
        expressions = [

                'changing\\s*from',
                'moving\\s*from',
                'transitioning\\s*from',
                '(is\\s*)?no\\s*longer\\s*(needed|required|indicated)',
                'No\\s*longer',
                'S/p',
                'S/p\\s*intermittent',
                'require(d|s)',
                'require(d|s)\\s*continuous',
                'abated',
                'ceased',
                'ceases',
                'ceasing',
                'dcd',
                'dc\'d',
                'dced',
                'dc\'ed',
                'dcing',
                'dc\'ing',
                'dicontinue',
                'dicontinued',
                'disconitnued',
                'discont',
                'discontine',
                'discontinuation',
                'discontinue',
                'discontinuing',
                'finished',
                'halted',
                'quits',
                'sopped',
                'stop',
                'stope',
                'stoped',
                'stopeed',
                'stoping',
                'stoppage',
                'stoppe',
                'stopped',
                'stopping',
                'stoppped',
                'stoppping',
                'stops',
                'stpped',
                //'s/p\\s*(\\d\\s*)?',     //s/p 5 proning, s/p prone
                'underwent',


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Treated:Stopped"
    }

    "Intent:Discussed" {
        expressions = [


                'is\\s*unlikely',
                'consider(ed)?',
                'possible',
                'candidate\\s*for', //candidate for prone
                 'plan\\s*to\\s*', //plan to prone
                 'consider\\s*paralysis,\\s*',
                 'consideration\\s*of',
                 'potential',
                '(will\\s*)?(continue|cont)\\s*to\\s*encourage',
                'to\\s*encourage',
                 'encouraged',
                 'encourage',
                'encouraged to',
                'encouraged?\\s*to',
                'encouraged?\\s*to\\s*try',
                'encouraged?\\s*to(lay\\s*|lie\\s*)(in\\s*a\\s*)?',
                //Possibly pattern expansion
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(recommend|recommended|encouraged?|advised)\\s*to',
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(recommend|recommended|encouraged?|advised)\\s*to\\s*(lay\\s*|lie)?(in\\s*a\\s*)?',
                '(recommend|recommended|encouraged?|advised|educated?)\\s*(pt\\.?|vet|veteran|patient|patinent|he|she|they)(\\s*to)?(lay\\s*|lie\\s*)?(in\\s*a\\s*)?',
                '(recommend|recommended|encouraged?|advised|educated?)\\s*(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*to\\s*try',
                '(recommend|recommended|encouraged?|advised|educated?)\\s*(placing|putting)\\s*(the\\s*)?(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(into|on|in)(\\s*a)?',// possible pattern annotator if needed
                'motivated\\s*to(\\s*try)?',
                'will', //will prone
                'will\\s*encourage',
                'will\\s*trial',
                'will\\s*try',
                //education
                'educated\\s*on',
                'educated\\s*(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(on|in)',
                'Educated\\s*(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(on|in)\\s*the\\s*benefits\\s*of',
                'Educated\\s*(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(on|in)\\s*the\\s*need\\s*for'

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Intent:Discussed"
    }

    "Intent:Hypothetical" {
        expressions = [


                '(could|would|might|will|may)\\s*(require\\s*|be\\s*|need\\s*)(to\\s*)?',
                '(could|would|might|will|may)\\s+\\w+',
                '(could|would|might|will|may)\\s*attempt(\\s*to)?',
                'if\\s*(pt\\.?|vet|veteran|patient|patinent)',



        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Intent:Hypothetical"
    }

    "NotTreated:Attempt" {
        expressions = [

                //Need to create separate type for treatment negation with these examples

                'hasn\'t\\s*tolerated',
                'has\\s*not\\s*tolerated',
                'not\\s*able\\s*to\\s*tolerate',
                '(can\'t|cant|cannot|can\\s*note)\\s*tolerate',
                'not\\s*been\\s*able\\s*to\\s*tolerate',
                '(not\\s*going|unable|unlikely)\\s*to\\s*tolerate',
                'didn\'t tolerate',
                'not\\s*tolerated',
                'not\\s*tolerating',
                'not\\s*tolerate',
                'avoid', //avoid proning
                'attempted',
                'attempting',
                'not\\s*able\\s*to',
                'not\\s*able\\s*to\\s*do',
                'not be able to',
                'unable\\s*to(\\s*be)?'


        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "NotTreated:NotTolerated"
    }

    "NotTreated:Refused" {
        expressions = [

                //Possible pattern and window expansion
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(refused|refuses|declined|declines|refusing)\\s*(to)?',
                'declined',
                'declines',
                'declinied',
                'delcined',
                'delined',
                'refued',
                'refues',
                'refuesed',
                'refused',
                'refusd',
                'refuse',
                'refuseed',
                'refuses',
                'refusing',
                'refussed',
                'refusses',
                'rfused',

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "NotTreated:Refused"
    }

    "NotTreated:Negated" {
        expressions = [
                //Need to create separate type for treatment negation with these examples

                '(was|has)\\s*not\\s*(required|needed)',
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(can|has|will|have|is|was)\\s*not',
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*(is|was)\\s*not\\s*able\\s*to',
                '(pt\\.?|vet|veteran|patient|patinent|he|she|they)\\s*has\\s*not\\s*(been\\s*)?able\\s*to',
                'hold\\s*off\\s*on',
                'avoid', //avoid proning
                'not\\s*a\\s*candidate\\s*for', //not a candidate for prone
                'don\'t\\s*plan\\s*to' ,//don't plan to prone
                'Decision made not to',
                'not to',
                'will not\\s*',
                'not able to',
                'not be able to',
                'unable\\s*to(\\s*be)?',
                'hold\\s*off\\s*(on\\s*)?',
                'no\\s+',//Requires boundary case
                'was\\s*not'

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "NotTreated:Negated"
    }



    "Exclude:Surgery" {
        expressions = [



                //Patient positioned prone on radiology table. etc.

                '(on|onto)\\s*(the\\s*|a\\s*|an\\s*)?(Jackson|procedure|operative|operating|or|wilson|flouroscop|open\\s*frame|radiology)',
                'surgery\\s*position:',
                'Surgery Position\\(s\\):',
                'procedure\\s*position:',
                'operation\\s*position:',
                'Intraoperative positioning Type of position:?',
                'for\\s*(procedure|operation)'

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Modifier"
        concept_feature_value = "Surgery"
    }



    "Prone_middle" {
        expressions = [

                //Space after with the merge annotator allows multiple middlestuff annotation to merge
                'and\\s*',
                //'patient\\s*',
                'with\\s*',
                'at',
                'intermittent',
                'intermittant',
                'intermittently',
                'intermittantly',
                'occasional',
                'periodic',
                'recurring',
                'trials?',

                //'started' //proning started 3/4

        ]
        outputType = "gov.va.vinci.vent.types.Prone_Middle"
        concept_feature_value = "middle_stuff"
    }


}
