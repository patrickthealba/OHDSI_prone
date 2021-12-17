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
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.descriptors.TypeDescriptionBuilder;
import gov.va.vinci.vent.types.Section;
import gov.va.vinci.vent.types.SectionHeader;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This annotator finds relevant sections
 */
public class RegexBasedSectionizer extends LeoBaseAnnotator {

    int maxSectionSize = 3000;

    /**
     * NOTE: Change on 2018/08/30 - include header into the section
     *
     * @param aJCas
     * @throws AnalysisEngineProcessException
     */
    @Override
    public void annotate(JCas aJCas) throws AnalysisEngineProcessException {
    /* Processing steps:                                               */
      /* 1. Identify if Header annotation exists in the document        */
        String documentText = aJCas.getDocumentText();

        FSIterator iter = aJCas.getAnnotationIndex(SectionHeader.type).iterator();
        int sectionStart = 0;

        int prevSecEnd = 0;
        SectionHeader prevSecHeader = null;
        boolean lastSectionFlag = false;
        ArrayList<Annotation> anList = new ArrayList();
        try {
            anList.addAll((Collection<? extends Annotation>) AnnotationLibrarian.getAllAnnotationsOfType(aJCas, SectionHeader.class.getCanonicalName(), false));
        } catch (CASException e) {
            e.printStackTrace();
        }
        if (anList.size() > 0) {
            for (Annotation ann : anList) {
                // Step 1. Go through all SectionHeaders
                SectionHeader currSecHeader = (SectionHeader) ann;
                // Trim the header so that it does not interfere with blank lines
                if (StringUtils.isNotBlank(currSecHeader.getCoveredText())) {
                    AnnotationLibrarian.trimAnnotation(currSecHeader, false);
                }
                // Step 2. Use prevSecEnd variable to keep track of the end of the section before the current header
                prevSecEnd = currSecHeader.getEnd() + 1;  //INFO: Changed on 2018/08/30
                //prevSecEnd = currSecHeader.getBegin() - 1 ;

                // check if there was already currSecHeader header
                // create currSecHeader new section
                // Use prevSecHeader annotation to keep track of the annotation for the  previous section
                if (prevSecHeader != null) {
                    // At this time there are two section headers in play - prevSecHeader and currSecHeader
                    // Check to make sure that the section is longer than 3 characters
                    if (prevSecEnd - prevSecHeader.getEnd() + 1 > 3) {
                        int newSectionStart = prevSecHeader.getBegin();// + 1;
                        int newSectionEnd = prevSecEnd  + 1; // This end is either the beginning of the new header or up to maxSectionSize
                        // Check if the section is longer than 100
                        if (newSectionEnd - newSectionStart >= maxSectionSize) {
                            newSectionEnd = newSectionStart + maxSectionSize;
                        }
                        // Create new section annotation
                        Section newSec = (Section) this.addOutputAnnotation("gov.va.vinci.vent.types.Section",
                                aJCas, newSectionStart, newSectionEnd);
                        newSec.setSectionHeader(prevSecHeader);
                        newSec.setSectionHeaderText(prevSecHeader.getConcept());
                        if (StringUtils.isNotBlank(newSec.getCoveredText())) {
                            AnnotationLibrarian.trimAnnotation(newSec);
                        }
                    }
                }

                // set the previous section header to the new section header
                prevSecHeader = currSecHeader; // if currSecHeader is the first section header, it simply skips to the next section header
            }
            // Step 3. When all headers are done, this is the last section,
            //  which is the case when prevSecHeader is not null and the end is the end of the document
            if (prevSecHeader != null) {

                if (prevSecHeader.getEnd() + 1 < documentText.length() - 1) {
                    int newSectionStart = prevSecHeader.getBegin() -3; // Note to PRA: "-3" was causing out of bounds error. If you have to do that, always check for string boundaries. Substring start or end cannot be negative or larger than the text length.
                    //int newSectionStart = prevSecHeader.getEnd();// + 1;  //INFO: OVP Changed on 2018/08/30
                    int newSectionEnd = documentText.length();
                    // Check if the section is longer than 100
                    if (newSectionEnd - newSectionStart >= maxSectionSize) {
                        newSectionEnd = newSectionStart + maxSectionSize;
                    }
                    if(newSectionStart < 0) newSectionStart=0;  // INFO: OVP added on 2018/09/04

                    Section newSec = (Section) this.addOutputAnnotation(
                            "gov.va.vinci.vent.types.Section", aJCas, newSectionStart, newSectionEnd);
                    newSec.setSectionHeader(prevSecHeader);
                    newSec.setSectionHeaderText(prevSecHeader.getConcept());
                    if (!newSec.getSectionHeaderText().equalsIgnoreCase("separator")) {
                        if (StringUtils.isNotBlank(newSec.getCoveredText())) {
                            try {
                                AnnotationLibrarian.trimAnnotation(newSec);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
         /**/
    }


    @Override
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        LeoTypeSystemDescription types = new LeoTypeSystemDescription();

        try {

            types.addType(TypeDescriptionBuilder.create("gov.va.vinci.vent.types.Section",
                    "Section Type", "uima.tcas.Annotation")
                    .addFeature("SectionHeader", "Anchor annotation around which the section was created", "uima.tcas.Annotation")
                    .addFeature("SectionHeaderText", "text of the header", "uima.cas.String")
                    .addFeature("Pneumonia_Term", "ContextTerm", "uima.cas.String")
                    .addFeature("Experiencer", "text", "uima.cas.String")
                    .addFeature("Negation", "tex", "uima.cas.String")
                    .addFeature("Temporality", "text", "uima.cas.String")
                    .getTypeDescription());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return types;
    }


}
