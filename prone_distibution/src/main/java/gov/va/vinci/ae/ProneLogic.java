package gov.va.vinci.ae;

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


import gov.va.vinci.leo.AnnotationLibrarian;
import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.annotationpattern.AnnotationPattern;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.sentence.types.AnchoredSentence;
import gov.va.vinci.leo.window.types.Window;
import gov.va.vinci.vent.types.*;
import gov.va.vinci.listeners.ListenerLogic;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;

public class ProneLogic extends LeoBaseAnnotator {

    /*@Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
    }
*/
    @Override
    public void annotate(JCas aJCas) throws AnalysisEngineProcessException {
        FSIterator<Annotation> patternit = this.getAnnotationListForType(aJCas, gov.va.vinci.vent.types.Prone_Pattern.class.getCanonicalName());
        while (patternit.hasNext()) try {
            gov.va.vinci.vent.types.Prone_Pattern current_pattern = (gov.va.vinci.vent.types.Prone_Pattern) patternit.next();
            if (current_pattern != null) {
                ArrayList<Annotation> termList = new ArrayList<Annotation>();
                ArrayList<Annotation> sentenceList = new ArrayList<Annotation>();
                ArrayList<Annotation> sectionList = new ArrayList<Annotation>();
                ArrayList<Annotation> modifierList = new ArrayList<Annotation>();
                ArrayList<Annotation> modifierRightList = new ArrayList<Annotation>();
                ArrayList<Annotation> dateList = new ArrayList<Annotation>();
                ArrayList<Annotation> ventList = new ArrayList<Annotation>();
                //ArrayList<Annotation> excludewindowList = new ArrayList<Annotation>();




                termList.addAll(AnnotationLibrarian.getAllOverlappingAnnotationsOfType(current_pattern, gov.va.vinci.vent.types.Prone_Term.type, false));
                sentenceList.addAll(AnnotationLibrarian.getAllOverlappingAnnotationsOfType(current_pattern, AnchoredSentence.type, false));
                sectionList.addAll(AnnotationLibrarian.getAllOverlappingAnnotationsOfType(current_pattern, gov.va.vinci.vent.types.Section.type, false));
                modifierList.addAll(AnnotationLibrarian.getAllOverlappingAnnotationsOfType(current_pattern, gov.va.vinci.vent.types.Prone_Modifier.type, false));
                modifierRightList.addAll(AnnotationLibrarian.getAllOverlappingAnnotationsOfType(current_pattern, gov.va.vinci.vent.types.Prone_Modifier_Right.type, false));
                dateList.addAll(AnnotationLibrarian.getAllOverlappingAnnotationsOfType(current_pattern, gov.va.vinci.vent.types.Temporality.type, false));
                ventList.addAll(AnnotationLibrarian.getAllOverlappingAnnotationsOfType(current_pattern, gov.va.vinci.vent.types.Vent_Term.type, false));
                //excludewindowList.addAll(AnnotationLibrarian.getAllOverlappingAnnotationsOfType(current_pattern, gov.va.vinci.vent.types.Prone_Modifier.type, false));


                //1. Set term_category to the concept type of a term
                if (sectionList.size() > 0) {
                    Annotation sec = sectionList.get(0);
                    current_pattern.setSection(((gov.va.vinci.vent.types.Section) sec).getSectionHeaderText());
                    //Potentially delete instances found in certain sections of a document
                    // if( ((Section) sec).getSectionHeaderText().equalsIgnoreCase("Medications")){
                    // todelete.add(current_cont);
                    //}

                }
                //Explicit Modifiers
                if (modifierList.size() > 0) {
                    Annotation mod = modifierList.get(0);
                    current_pattern.setPhrase_Modifier(((gov.va.vinci.vent.types.Prone_Modifier) mod).getConcept());
                    current_pattern.setModifier_Term(mod.getCoveredText());
                }
                //If pattern include mechanical ventilation terms,  set modifer
                if (ventList.size() > 0) {
                    Annotation vent = ventList.get(0);
                    current_pattern.setPhrase_Modifier("Treated:MechanicalVentilation");
                    current_pattern.setModifier_Term(vent.getCoveredText());
                }
                //Right Sided only instances
                if (modifierRightList.size() > 0) {
                    Annotation mod = modifierRightList.get(0);
                    current_pattern.setPhrase_Modifier(((gov.va.vinci.vent.types.Prone_Modifier_Right) mod).getConcept());
                    current_pattern.setModifier_Term(mod.getCoveredText());
                }


                if (dateList.size() > 0) {
                    Annotation dt = dateList.get(0);
                    //Set the date
                    //current_pattern.setDate(((gov.va.vinci.vent.types.Temporality) dt).getCoveredText());
                    current_pattern.setTemporality_Modifier((dt).getCoveredText());
                    //But don't overrule the existing modifier
                    if (current_pattern.getPhrase_Modifier() == null) {
                        current_pattern.setPhrase_Modifier("Treated:Date");
                        //current_pattern.setDate(((gov.va.vinci.vent.types.Temporality) dt).getCoveredText());
                    }
                }

                //If there's an extracted date, set those dates here.  Anchor = first date, target = second
                if (current_pattern.getAnchor() !=null) {
                    Annotation date1 = current_pattern.getAnchor();
                    current_pattern.setDate_1(date1.getCoveredText());
                    if (current_pattern.getTarget() != null) {
                        Annotation date2 = current_pattern.getTarget();
                        current_pattern.setDate_2(date2.getCoveredText());
                    }
                    if (current_pattern.getPhrase_Modifier() == null) {
                        current_pattern.setPhrase_Modifier("Treated:Date");
                    }
                }



                if (termList.size() > 0) {
                    Annotation term = termList.get(0);
                    current_pattern.setTerm(term.getCoveredText());
                    current_pattern.setTerm_Type(((gov.va.vinci.vent.types.Prone_Term) term).getConcept());
                }


                if (sentenceList.size() > 0) {
                    Annotation sent = sentenceList.get(0);
                    current_pattern.setAnchored_Sentence(ListenerLogic.shortenText(sent.getCoveredText(), 500, true));

                }

                ///
                //Final Vent_status Logic
                //Sum up all of the logic above to create a single output variable  "Vent_Status"
                //




            } }   catch (CASException e) {
            logger.error(e.getStackTrace());

            //

        }
        //Sections to potentially Remove
       /* for(Annotation a:todelete){
            a.removeFromIndexes();
        } */

    }
    @Override
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        return new LeoTypeSystemDescription();
    }
}
