package gov.va.vinci;

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


import gov.va.vinci.leo.annotationpattern.ae.AnnotationPatternAnnotator;
import gov.va.vinci.leo.context.ae.ContextAnnotator;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.descriptors.TypeDescriptionBuilder;
import gov.va.vinci.leo.merger.ae.MergeAnnotator;
import gov.va.vinci.leo.regex.ae.RegexAnnotator;
import gov.va.vinci.leo.whitespace.ae.WhitespaceTokenizer;
import gov.va.vinci.leo.sentence.ae.AnchoredSentenceAnnotator;
import gov.va.vinci.leo.sentence.ae.SentenceAnnotator;
import gov.va.vinci.leo.types.TypeLibrarian;
import gov.va.vinci.leo.window.ae.WindowAnnotator;
//import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.io.File;

public class TypeSystem {
    //TYPE_SIMPLE_CONCEPT_TERM
    private static String TYPE_VENT_TERM = "gov.va.vinci.vent.types.Vent_Term";
    private static String TYPE_PRONE_TERM = "gov.va.vinci.vent.types.Prone_Term";
    private static String TYPE_PRONE_MIDDLE = "gov.va.vinci.vent.types.Prone_Middle";
    private static String TYPE_VENT_DATE = "gov.va.vinci.vent.types.Vent_Date";
    private static String TYPE_VENT_DATE_MIDDLE = "gov.va.vinci.vent.types.Vent_Date_Middle";
    private static String TYPE_TEMPORALITY = "gov.va.vinci.vent.types.Temporality";
    private static String TYPE_PRONE_MODIFIER = "gov.va.vinci.vent.types.Prone_Modifier";
    private static String TYPE_PRONE_MODIFIER_RIGHT = "gov.va.vinci.vent.types.Prone_Modifier_Right";
    private static String TYPE_PRONE_MODIFIER_LEFT = "gov.va.vinci.vent.types.Prone_Modifier_Left";
    private static String TYPE_PRONE_EXCLUDE = "gov.va.vinci.vent.types.Prone_Exclude";


    private static String TYPE_SECTION_HEADER = "gov.va.vinci.vent.types.SectionHeader";
    private static String TYPE_SECTION = "gov.va.vinci.vent.types.Section";

    //Pattern Types
    private static String TYPE_DATE_PATTERN = "gov.va.vinci.vent.types.Date_Pattern";
    private static String TYPE_PRONE_PATTERN = "gov.va.vinci.vent.types.Prone_Pattern";

    //Window and Context
    private static String TYPE_EXCLUDE_WINDOW = "gov.va.vinci.vent.types.Exclude_Window";
    private static String TYPE_SECTION_WINDOW = "gov.va.vinci.vent.types.Section_Window";
    private static String TYPE_INPUT_WINDOW = "gov.va.vinci.types.Input_Window";
    private static String TYPE_CONTEXT = "gov.va.vinci.leo.context.types.TermContext";


    public static void main(String[] args) {
        try {
            LeoTypeSystemDescription types = new LeoTypeSystemDescription();
            types.addTypeSystemDescription(getLeoTypeSystemDescription());

            File srcDir = new File("generated-types/src");
            srcDir.mkdirs();

            File classesDir = new File("generated-types/classes");
            classesDir.mkdirs();

            types.jCasGen(srcDir.getCanonicalPath(), classesDir.getCanonicalPath());

            File resDir = new File("generated-types/resources/types");
            resDir.mkdirs();

            types.toXML(resDir.getCanonicalPath() + "/TypeSystem.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LeoTypeSystemDescription getLeoTypeSystemDescription() {
        LeoTypeSystemDescription types = new LeoTypeSystemDescription();
        /* Leo Bones **/
        types.addType(TypeLibrarian.getCSITypeSystemDescription());
        types.addTypeSystemDescription(new WindowAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new RegexAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new AnnotationPatternAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new AnchoredSentenceAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new SentenceAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new ContextAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new MergeAnnotator().getLeoTypeSystemDescription());
        types.addTypeSystemDescription(new WhitespaceTokenizer().getLeoTypeSystemDescription());


        types.addTypeSystemDescription(getSpecificTypeSystemDescription());


        return types;
    }

    public static LeoTypeSystemDescription getSpecificTypeSystemDescription() {
        LeoTypeSystemDescription types = new LeoTypeSystemDescription();

        try {
            //Simple Concept Extraction
            types.addType(TYPE_CONTEXT, "", PARENT_CLASS.CONTEXT.type);
            types.addType(TYPE_SECTION_HEADER, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_SECTION_WINDOW, "", PARENT_CLASS.TYPE_WINDOW.type);
            types.addType(TYPE_EXCLUDE_WINDOW, "", PARENT_CLASS.TYPE_WINDOW.type);
            types.addType(TYPE_VENT_TERM, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_PRONE_TERM, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_PRONE_MIDDLE, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_VENT_DATE, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_VENT_DATE_MIDDLE, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_TEMPORALITY, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_PRONE_MODIFIER, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_PRONE_MODIFIER_RIGHT, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_PRONE_MODIFIER_LEFT, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TYPE_PRONE_EXCLUDE, "", PARENT_CLASS.TYPE_REGEX.type);
            types.addType(TypeDescriptionBuilder.create(TYPE_PRONE_PATTERN, "", PARENT_CLASS.TYPE_PATTERN.type)
                    .addFeature("Term", "", "uima.cas.String")
                    .addFeature("Term_Type", "", "uima.cas.String")
                    .addFeature("Section", "", "uima.cas.String")
                    .addFeature("Term_Modifier", "", "uima.cas.String")
                    .addFeature("Phrase_Modifier", "", "uima.cas.String")
                    .addFeature("Modifier_Term", "", "uima.cas.String")
                    .addFeature("Date_1", "", "uima.cas.String")
                    .addFeature("Date_2", "", "uima.cas.String")
                    .addFeature("Temporality_Modifier", "", "uima.cas.String")
                    .addFeature("Anchored_Sentence", "", "uima.cas.String")
                    .getTypeDescription());
            types.addType(TypeDescriptionBuilder.create(TYPE_DATE_PATTERN, "", PARENT_CLASS.TYPE_PATTERN.type)
                    .addFeature("Term", "", "uima.cas.String")
                    .addFeature("Value1", "", "uima.cas.String")
                    .addFeature("Value2", "", "uima.cas.String")
                    .getTypeDescription());



            types.addType(TypeDescriptionBuilder.create(TYPE_SECTION, "Section Type", "uima.tcas.Annotation")
                    .addFeature("SectionHeader", "Anchor annotation around which the section was created", "uima.tcas.Annotation")
                    .addFeature("SectionHeaderText", "text of the header", "uima.cas.String")
                    .addFeature("Term", "text of the header", "uima.cas.String")
                    .addFeature("Snippet", "text", "uima.cas.String")
                    .getTypeDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return types;
    }



    public enum PARENT_CLASS {
        TYPE_WINDOW("gov.va.vinci.leo.window.types.Window"),
        TYPE_REGEX("gov.va.vinci.leo.regex.types.RegularExpressionType"),
        TYPE_PATTERN("gov.va.vinci.leo.annotationpattern.types.AnnotationPatternType"),
        CONTEXT("gov.va.vinci.leo.context.types.Context"),
        LINK("gov.va.vinci.leo.conceptlink.types.Link");

        public String type;

        private PARENT_CLASS(String type) {

            this.type = type;
        }

        public String getType() {

            return this.type;
        }

    }

}
