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


    "Intubation_terms" {

        expressions = [

                'intub'
                ,'Intubated'
                //iter6 Levenshtein additions
                ,'intubation'
                ,'intubated'
                ,'intubation'
                ,'intubations'
                ,'intubating'
                ,'intubtion'
                ,'entubation'
                ,'intuabtion'
                ,'intibation'
                ,'intubatation'
                ,'intbation'
                ,'intubatin'
                ,'intubaiton'
                ,'intabation'
                ,'intubaton'
                ,'inutbation'
                ,'tubation'
                ,'inbutation'
                ,'intuabation'
                ,'intubaion'
                ,'intbuation'
                ,'itubation'
                //intubated edit distance terms
                ,'intubated'
                ,'intubed'
                ,'intubation'
                ,'entubated'
                ,'intuabted'
                ,'intabated'
                ,'inubated'
                ,'intubted'
                ,'intibated'
                ,'intubatd'
                ,'inutbated'
                ,'intubaed'
                ,'itubated'
                ,'intbuated'



        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "intubation_term"
    }

    "reintubation_terms" {
        //This list consists of terms form the paper "Summarizing Complex ICU data in Natural Language"
        expressions = [

                //optional space, dash or neither, one or two g's, one or two t's, optional s for plural
                '\\bre(-|\\s*)?intub',
                'Traumatic\\s*re(-|\\s*)?intub',
                '\\bre(-|\\s*)?Intubated',
                'Intubated\\s*again',
                'Intubated\\s*once\\s*again',


        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "reintubation_term"
    }




    "Extubation_terms" {

        expressions = [


                'extub',
                'extubated',
                'extubation',
                'extubating',
                //Levenshtein expansion
                'Extubatedb',
                'extubate',
                'extubed',
                'extubates',
                'extubted',
                'extuabated',
                'exutbated',
                'exubate',
                'extubatd',
                'extuabed',
                'extubaed',
                'extibated',


        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "extubation_term"
    }



    "Ventilator_terms" {
        //This list consists of terms form the paper "Summarizing Complex ICU data in Natural Language"
        expressions = [

                //option space, dash or neither, one or two g's, one or two t's, optional s for plural
                'ventil',
                'ventilator',
                'Ventilator',
                'Ventilatory',
                'ventilation',
                'mechanical\\s*ventil',
                // Vent Specific Type terms, not yet included as precision not yet assessesed.


        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "ventilator_term"
    }
/*
    "weaning_terms" {
        //This list consists of terms form the paper "Summarizing Complex ICU data in Natural Language"
        expressions = [

                //option space, dash or neither, one or two g's, one or two t's, optional s for plural
                'weaning',
                'wean',



        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "wean_term"
    }
*/

    /*
    "ARDS" {
        expressions = [

                ' ards ',
                'respiratory\\s*failure',
                'acute\\s*respirator',
                'Hypoxemic hypercarbic respiratory failure',
                //
                'acute lung injury',
                ' ali ',
                'ali/ards',



        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "ARDS_Term"
    }
    */


    "Endotracheal_tube" {
        expressions = [

                ' ETT tube',
                ' ETT ',
                ' Et Tube',
                'endotracheal',
                'endotracheal tube',
                'orotracheal',
                'nasoendotracheal',
                'trachostomy',
                'endotrachial\\s*tube'


        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "endotracheal_tube"
    }

    "emerse_vent" {
        expressions = [


                'artificial\\s*respiration'
                ,'artificial\\s*respirators'
                ,'artificial\\s*respirator'
                ,'artificial\\s*ventilation'
                ,'artificial\\s*ventilators'
                ,'artificial\\s*ventilator'
                ,'artificially\\s*ventilating'
                ,'artificially\\s*ventilated'
                ,'artificially\\s*ventilates'
                ,'artificially\\s*ventilate'
                ,'breathing\\s*machines'
                ,'breathing\\s*machine'
                ,'mechanically\\s*ventilated'
                ,'mechanical\\s*ventilation'
                ,'mechanical\\s*ventilators'
                ,'mechanical\\s*ventilator'
                ,'mechanical\\s*vents'
                ,'mechanical\\s*vent'
                ,'ventilations'
                ,'ventilation'
                ,'ventilators'
                ,'ventilator'
                ,'ventilating'
                ,'ventilated'
                ,'ventilates'
                ,'ventilate'
                ,'vented'
                //,'on\\s*a\\s*ventilator'      //redundant Logic incorpated here
                //,'on\\s*a\\s*vent'
                //,'on\\s*ventilator'
                //,'on\\s*vent'
                ,'\bvents?'         //Required boundaries that weren't in emerse set, otherwise too imprecise
                ,'ventilatin'
                ,'ventillation'
                ,'ventailation'
                ,'ventialltion'
                ,'ventalation'
                ,'ventliation'
                ,'ventilater'
                ,'ventalator'
                ,'ventelator'
                ,'ventillator'
                ,'ventalotor'
                ,'ventialtion'
                ,'venntilat'
                ,'ventilato'
                ,'ventilatory'
                ,'venilation'
                ,'ventilaiton'
                ,'ventilaor'
                ,'ventilatio'
                ,'ventilatioin'
                ,'ventilat'
                ,'vetilator'
                ,'mechanical\\s*ventillation'
                ,'mechnical\\s*ventilation'
                ,'ventilaton'
                ,'ventialtor'
                ,'ventilacion'
                ,'ventilantion'
                ,'ventalated'
                ,'ventilarot'
                ,'ventilationa'
                ,'venitlator'
                ,'ventilaotr'
                ,'ventilatro'
                ,'ventiltaor'
                ,'ventliator'
                ,'vnetilator'
                ,'veantilator'
                ,'veintilator'
                ,'venatilator'
                ,'venetilator'
                ,'venitilator'
                ,'ventailator'
                ,'ventiilator'
                ,'ventilaator'
                ,'ventilaotor'
                ,'ventilataor'
                ,'ventilateor'
                ,'ventilatior'
                ,'ventilatoar'
                ,'ventilatoer'
                ,'ventilatoir'
                ,'ventilatoor'
                ,'ventilatore'
                ,'ventilatorr'
                ,'ventilatoru'
                ,'ventilatr'
                ,'ventilattor'
                ,'ventiliator'
                ,'ventiloator'
                ,'ventiltor'
                ,'ventiolator'
                ,'ventiulator'
                ,'ventlator'
                ,'venttilator'
                ,'ventuilator'
                ,'ventyilator'
                ,'vewntilator'
                ,'vntilator'
                ,'vventilator'
                ,'vwentilator'
                ,'fentilator'
                ,'vantilator'
                ,'ventilador'
                ,'ventilatir'
                ,'ventilitor'
                ,'ventilltor'
                ,'ventilotor'
                ,'ventllator'
                ,'ventolator'
                ,'ventulator'
                ,'evntilator'
                ,'vetnilator'
                ,'ventiator'
                ,'nventilator'
                ,'ventiltator'
                ,'venthilator'
                ,'ventialator'
                ,'ventilartor'
                ,'entilator'
                ,'verntilator'
                ,'ventilaltor'
                ,'ventlilator'
                ,'ventilatlor'
                ,'rventilator'
                ,'ventinlator'
                ,'venilator'
                ,'ventilatord'
                ,'ventilatort'
                ,'aventilator'
                ,'eventilator'
                //,'oventilator' --removed for precision, hypovent-
                ,'ventilatorl'
                ,'vlentilator'
                ,'ventrilator'
                ,'ventilatorx'
                ,'ventitlator'
                ,'vetntilator'
                ,'vemntilator'
                ,'pventilator'
                ,'ventilatot'
                ,'ventilatos'
                ,'ventilztor'
                ,'vemtilator'
                ,'wentilator'
                ,'vertilator'
                ,'hentilator'
                ,'ventilaror'
                ,'ventilatoe'
                ,'ventilatoy'
                ,'bentilator'
                ,'ventrlator'
                ,'veitilator'
                ,'vebtilator'
                ,'ventilatpr'
                ,'ventitator'
                ,'ventirator'
                ,'centilator'
                ,'venrilator'
                ,'venitlation'
                ,'vnetilation'
                ,'veentilation'
                ,'venatilation'
                ,'venetilation'
                ,'venitilation'
                ,'venntilation'
                ,'ventiilation'
                ,'ventilaation'
                ,'ventilaition'
                ,'ventilation1'
                ,'ventilationi'
                ,'ventilationo'
                ,'ventilationw'
                ,'ventiliation'
                ,'ventiltion'
                ,'ventiolation'
                ,'ventiulation'
                ,'ventlation'
                ,'venttilation'
                ,'ventuilation'
                ,'vntilation'
                ,'vventilation'
                ,'fentilation'
                ,'vantilation'
                ,'ventelation'
                ,'ventilition'
                ,'ventllation'
                ,'ventolation'
                ,'ventulation'
                ,'ventilatino'
                ,'ventilatoin'
                ,'ventiltaion'
                ,'ventiation'
                ,'venticlation'
                ,'ventiklation'
                ,'ventilaion'
                ,'venbtilation'
                ,'ventialation'
                ,'ventiltation'
                ,'vbentilation'
                ,'ventilartion'
                ,'vdentilation'
                ,'velntilation'
                ,'ventilaltion'
                ,'ventlilation'
                ,'ventilatgion'
                //,'rventilation' removed for precision, always hyperventilation
                ,'vetilation'
                ,'vemntilation'
                ,'mventilation'
                ,'venrtilation'
                ,'ventrilation'
                ,'ventilataion'
                ,'ventilatiion'
                ,'ventilatiojn'
                ,'ventilatioon'
                ,'ventilatiuon'
                ,'ventilatoion'
                ,'ventilattion'
                ,'ventilatuion'
                ,'ventilatyion'
                ,'ventilatrion'
                ,'ventinlation'
                ,'ventilastion'
                ,'ventilaztion'
                ,'vevntilation'
                ,'verntilation'
                ,'vrentilation'
                ,'aventilation'
                ,'eventilation'
                //,'oventilation'  Removed for precision, always 'hypoventilation'
                ,'ventilationz'
                ,'ventilationm'
                ,'ventilationl'
                ,'ventilationg'
                ,'venmtilation'
                ,'vcentilation'
                ,'ventitlation'
                ,'entilation'
                //,'mentilation'
                ,'ventilaaion'
                ,'vertilation'
                ,'ventitation'
                ,'vdntilation'
               // ,'gentilation'
                ,'centilation'
                ,'vemtilation'
                ,'ventilatiom'
                ,'ventilasion'
                ,'ventilstion'
                ,'ventilarion'
                ,'ventication'
                ,'wentilation'
                ,'venitlated'
                ,'ventiltaed'
                ,'ventliated'
                ,'vnetilated'
                ,'venitilated'
                ,'ventailated'
                ,'ventiilated'
                ,'ventilataed'
                ,'ventilatedt'
                ,'ventilatied'
                ,'ventilatoed'
                ,'ventilhated'
                ,'ventiliated'
                ,'ventillated'
                ,'ventilted'
                ,'ventlated'
                ,'venttilated'
                ,'vewntilated'
                ,'fentilated'
                ,'vantilated'
                ,'ventelated'
                ,'ventillted'
                ,'ventolated'
                ,'ventulated'
                ,'ventialted'
                ,'vetnilated'
                ,'ventiated'
                ,'ventiltated'
                ,'ventilateds'
                ,'ventilatedl'
                ,'ventialated'
                ,'entilated'
                ,'vrentilated'
                //,'rventilated' Removed for precision, always hyperventilated in VA set
                ,'venilated'
                ,'eventilated'
                //,'oventilated' Removed for precision, always hypoventilated in VA set
                ,'ventilaed'
                ,'ventilatd'
                ,'ventrilated'
                ,'ventitlated'
                ,'vetilated'
                ,'venmtilated'
                ,'ventilatec'
                ,'vestilated'
                ,'vemtilated'
                ,'vertilated'
                ,'ventilared'
                //,'vnt' imprecise
                //,'vnet',


        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "emerse_vent_terms"
    }


    "VA_edit_distance" {
        expressions = [


                'ventilator'
                ,'ventilation'
                ,'ventilatory'
                ,'ventilate'
                ,'ventilated'
                ,'ventilating'
                ,'ventillator'
                ,'ventillation'
                ,'ventilators'
                ,'__ventilator'
                ,'ventilations'
                ,'ventilates'
                ,'ventlator'
                //,'estimator'
                ,'___ventilator'
                ,'ventilaton'
                ,'ventilative'
                ,'ventalator'
                ,'ventillatory'
                ,'ventillated'
                ,'ventialtor'
                ,'ventalation'
                ,'ventilater'
                ,'ventillate'
                //,'ventilator-na'
                ,'venilation'
                ,'vetilator'
                ,'ventiliation'
                ,'venilator'
                ,'rt-ventilator'
                ,'ventialtory'
                ,'ventialation'
                ,'ventiltor'
                ,'venitlator'
                ,'ventilaor'
                ,'denticator'
                ,'ventilatio'
                ,'ventilatior'
                ,'ventalatory'
                ,'vetilation'
                ,'ventilary'
                ,'ventilaiton'
                ,'ventilatoris'
                //,'venodilators'
                ,'ventilatiion'
                //,'mutilator'
                ,'ventilatroy'
                ,'ventlation'
                ,'ventalated'
                ,'ventiation'
                ,'ventilat'
                ,'ventolator'
                ,'ventilor'
                ,'venitilator'
                ,'ventalate'
                ,'ventilator\'s'
                ,'ventilaion'
                ,'vnetilator'
                ,'ventilato'
                ,'vetilatory'
                ,'ventelator'
                ,'venilatory'
                ,'ventiator'
                ,'ventilaotr'
                ,'ventiltion'
                ,'entilator'
                //,'venodilator'
                ,'venator'
                ,'ventila'
                ,'ventilador'
                ,'venitlatory'


        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "va_edit_distance_terms"
    }

/*

    "ECMO" {
        expressions = [


                'ECMO',
                'Extracorporeal membrane oxygenation',

        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "Ecmo_terms"
    }


    "Vent_settings" {
        expressions = [


                'PEEP',
                'fio2'


        ]
        outputType = "gov.va.vinci.vent.types.Vent_Term"
        concept_feature_value = "Ecmo_terms"
    }
*/

}
