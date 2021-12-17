package gov.va.vinci.pipeline;

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


/*
 *
 *Initial pipeline to identify patients being placed in a prone position
 *A somewhat standard pattern annotator, with prone specific logic for 'to' phrases
 */

import gov.va.vinci.TypeSystem;
import gov.va.vinci.ae.ProneLogic;
import gov.va.vinci.ae.RegexBasedSectionizer;
import gov.va.vinci.leo.annotationpattern.ae.AnnotationPatternAnnotator;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.filter.ae.FilterAnnotator;
import gov.va.vinci.leo.merger.ae.MergeAnnotator;
import gov.va.vinci.leo.regex.ae.RegexAnnotator;
import gov.va.vinci.leo.sentence.ae.AnchoredSentenceAnnotator;
import gov.va.vinci.leo.context.ae.ContextAnnotator;
import gov.va.vinci.leo.window.ae.WindowAnnotator;

import java.util.ArrayList;
import java.util.HashMap;


public class Prone_Pipeline extends BasePipeline {

    private String TYPE_PRONE_TERM = "gov.va.vinci.vent.types.Prone_Term";
    private String TYPE_PRONE_MODIFIER = "gov.va.vinci.vent.types.Prone_Modifier";
    private String TYPE_PRONE_MODIFIER_RIGHT = "gov.va.vinci.vent.types.Prone_Modifier_Right";
    private String TYPE_PRONE_PATTERN = "gov.va.vinci.vent.types.Prone_Pattern";
    private String TYPE_PRONE_EXCLUDE = "gov.va.vinci.vent.types.Prone_Exclude";
    private String TYPE_VENT_TERM = "gov.va.vinci.vent.types.Vent_Term";
    private String TYPE_TEMPORALITY = "gov.va.vinci.vent.types.Temporality";
    private String TYPE_DATE_MIDDLE = "gov.va.vinci.vent.types.Vent_Date_Middle";
    private String TYPE_DATE_PATTERN = "gov.va.vinci.vent.types.Date_Pattern";
    private String RESOURCE_PATH = "src/main/java/resources/";
    private String RESOURCE_PRONE = "Prone_Terms.groovy";
    private String RESOURCE_TEMPORALITY = "temporality_terms.groovy";
    private String RESOURCE_VENT = "Vent_Terms.groovy";
    private String RESOURCE_PRONE_MODIFIER = "treatment_modifier_terms.groovy";
    private String RESOURCE_PRONE_MODIFIER_RIGHT = "treatment_modifier_right_terms.groovy";
    private String RESOURCE_TEMPORALITY_PATTERN = "date_pattern.pattern";
    private String TYPE_SECTION_HEADER = "gov.va.vinci.vent.types.SectionHeader";
    private String TYPE_SECTION = "gov.va.vinci.vent.types.Section";
    private String RESOURCE_PRONE_PATTERN = "prone_pattern.pattern";
    private String RESOURCE_SECTION_HEADER = "sectionHeaders.groovy";
    private String TYPE_EXCLUDE_WINDOW= "gov.va.vinci.vent.types.Exclude_Window";
    private String TYPE_WINDOW_FEATURE = "Anchor";
    private String SENTENCE_TYPE = "gov.va.vinci.leo.sentence.types.AnchoredSentence";





    /**
     * Constructors
     *
     * @throws Exception
     */
    public Prone_Pipeline(HashMap args) throws Exception {
        /* List for holding our annotators. This list, and the order of the list created the annotator pipeline. */
        ArrayList<LeoAEDescriptor> annotators = new ArrayList<LeoAEDescriptor>();


        //1.0 Anchor Concept Regex annotation
        annotators.add(new RegexAnnotator()
                .setGroovyConfigFile(RESOURCE_PATH + RESOURCE_PRONE)
                .setName("Concept Regex")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
        //1.1 remove most common overlapped exclusion types
        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{TYPE_PRONE_EXCLUDE})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
        annotators.add(new FilterAnnotator()
                .setTypesToKeep(new String[]{TYPE_PRONE_EXCLUDE})
                .setTypesToDelete(new String[]{TYPE_PRONE_TERM})
                .setRemoveOverlapping(true)
                .setName("ModifierOverlapFilter")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));

        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{TYPE_PRONE_TERM})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));





/* Temporarilly removing sectionizer altogether,  might not need at all
        //2. Section Regex annotation
        annotators.add(new RegexAnnotator()
                .setGroovyConfigFile(RESOURCE_PATH + RESOURCE_SECTION_HEADER)
                .setName("SectionHeader Regex")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
        annotators.add(new FilterAnnotator()
                .setTypesToKeep(new String[]{TYPE_SECTION_HEADER})
                .setRemoveOverlapping(true)
                .setName("OverlapFilter")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
        annotators.add(new RegexBasedSectionizer()
                .setName("SectionAnnotator")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{TYPE_SECTION})
                .setFeaturesToMatch(new String[]{"SectionHeaderText"})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));

        //3. Section Logic
        annotators.add(new RegexBasedSectionizer()
                .setName("SectionAnnotator")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
*/


        //2. Sentence Logic surrounding the prone term
        annotators.add(new AnchoredSentenceAnnotator()
                .setSpanSize(50)
                .setAnchorFeature(TYPE_WINDOW_FEATURE)
                .setOutputType(SENTENCE_TYPE)
                .setInputTypes(TYPE_PRONE_TERM)
                .setName("TermSentenceAnnotator")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));


//2.5 Merge overlapping Sentences
        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{SENTENCE_TYPE})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));

//3. Annotate additional concepts found in a prone sentence/window
            //Mechanical ventilation terms
        annotators.add(new RegexAnnotator()
                .setGroovyConfigFile(RESOURCE_PATH + RESOURCE_VENT)
                .setInputTypes(SENTENCE_TYPE)
                .setName("Vent Concept Regex")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));

        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{TYPE_VENT_TERM})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));

        //Prone Modifier terms
        annotators.add(new RegexAnnotator()
                .setGroovyConfigFile(RESOURCE_PATH + RESOURCE_PRONE_MODIFIER)
                .setInputTypes(SENTENCE_TYPE)
                .setName("Modifier Regex")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));

        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{TYPE_PRONE_MODIFIER})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));



        //Remove modifiers that are overlapped by anchor terms

        annotators.add(new FilterAnnotator()
                .setTypesToKeep(new String[]{TYPE_PRONE_TERM})
                .setTypesToDelete(new String[]{TYPE_PRONE_MODIFIER})
                .setRemoveOverlapping(true)
                .setName("ModifierOverlapFilter")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));



//4 Annotate dates found in sentences with Key terms -
        // A greedy regular expressions that looks for every number, so it is only triggered when anchor terms are present
        //annotated vent_date, and temporality types, which behave differently in the pattern and logic annotators

        annotators.add(new RegexAnnotator()
                .setGroovyConfigFile(RESOURCE_PATH + RESOURCE_TEMPORALITY)
                .setInputTypes(SENTENCE_TYPE)
                .setName("Temporality Regex")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{TYPE_TEMPORALITY})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));


//5. Date Pattern
        annotators.add(new AnnotationPatternAnnotator()
                .setResource(RESOURCE_PATH + RESOURCE_TEMPORALITY_PATTERN)
                .setOutputType(TYPE_DATE_PATTERN)
                .getLeoAEDescriptor().setName("Concept Pattern Annotator")
                .setTypeSystemDescription(getLeoTypeSystemDescription()));

        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{TYPE_DATE_PATTERN})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
        //complete date ranges use a different logic here, so remove any temporality types that are overlapped when a complete date pattern also exists
        annotators.add(new FilterAnnotator()
                .setTypesToKeep(new String[]{TYPE_DATE_PATTERN})
                .setTypesToDelete(new String[]{TYPE_TEMPORALITY})
                .setRemoveOverlapping(true)
                .setName("ModifierOverlapFilter")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));


//6. Annotate right sided modifiers
        annotators.add(new RegexAnnotator()
                .setGroovyConfigFile(RESOURCE_PATH + RESOURCE_PRONE_MODIFIER_RIGHT)
                .setInputTypes(SENTENCE_TYPE)
                .setName("Temporality Regex")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{TYPE_PRONE_MODIFIER_RIGHT})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));

//7. Delete most reight sided annotations if overlapped by others
        // remove 'to' phrases overlapped by date patterns
        annotators.add(new FilterAnnotator()
                .setTypesToKeep(new String[]{TYPE_DATE_PATTERN})
                .setTypesToDelete(new String[]{TYPE_PRONE_MODIFIER_RIGHT})
                .setRemoveOverlapping(true)
                .setName("ModifierOverlapFilter")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));

        //remove right sided modifiers covered by any other term or modifier
        annotators.add(new FilterAnnotator()
                .setTypesToKeep(new String[]{TYPE_PRONE_TERM})
                .setTypesToDelete(new String[]{TYPE_PRONE_MODIFIER_RIGHT})
                .setRemoveOverlapping(true)
                .setName("ModifierOverlapFilter")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
        annotators.add(new FilterAnnotator()
                .setTypesToKeep(new String[]{TYPE_PRONE_MODIFIER})
                .setTypesToDelete(new String[]{TYPE_PRONE_MODIFIER_RIGHT})
                .setRemoveOverlapping(true)
                .setName("ModifierOverlapFilter")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));

//8. Prone Pattern Annotator
        annotators.add(new AnnotationPatternAnnotator()
                .setResource(RESOURCE_PATH + RESOURCE_PRONE_PATTERN)
                .setOutputType(TYPE_PRONE_PATTERN)
                .getLeoAEDescriptor().setName("Concept Pattern Annotator")
                .setTypeSystemDescription(getLeoTypeSystemDescription()));

        annotators.add(new MergeAnnotator()
                .setTypesToMerge(new String[]{TYPE_PRONE_PATTERN})
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));


        //Not needed for now, strict negation patterns working
        // may be needed pending more validation of negation Left sided window for additional negation and hypothetical exclusions


        /*
        annotators.add(new WindowAnnotator()
                .setLtWindowSize(0)
                .setRtWindowSize(15)
                .setOutputType(TYPE_EXCLUDE_WINDOW)
                .setInputTypes(TYPE_PRONE_MODIFIER)
                .setName("LeftWindowAnnotator")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
       */

//9.  Apply Modifier and section as feature
        annotators.add(new ProneLogic()
                .setName("Prone Logic Annotator")
                .getLeoAEDescriptor().setTypeSystemDescription(getLeoTypeSystemDescription()));
/*
*
*
* Done
*
*
*/
        this.pipeline = new LeoAEDescriptor(annotators);
        this.pipeline.setTypeSystemDescription(getLeoTypeSystemDescription());
    }


    protected LeoTypeSystemDescription defineTypeSystem() throws Exception {
        LeoTypeSystemDescription description = TypeSystem.getLeoTypeSystemDescription();
        return description;
    }
}
