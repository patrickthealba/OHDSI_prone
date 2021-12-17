package gov.va.vinci.listeners;

/*
 * #%L
 * CADR (Colonoscopy adenoma detection rate)	System
 * %%
 * Copyright (C) 2010 - 2016 VDepartment of Veterans Affairs
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
//import gov.va.vinci.leo.sherlock.tools.SherlockVector;
import gov.va.vinci.leo.sherlock.tools.SherlockVector;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Patterson
 *         <Description>
 *         <p/>
 *         User: thomasginter
 *         Date: 6/7/13
 *         Time: 2:17 PM
 */

public class BinaryLearningListener extends gov.va.vinci.leo.sherlock.listeners.LearningListener {

    // INFO: A list of documents that are used for training
    //protected List<String> trainFileList = null;
    protected String vectorType = null;
    protected String keysFeature = null;
    protected String valuesFeature = null;
    protected String predictionType = null;
    protected String predictionFeature = null;
    protected String[] predictionFeatureValue = null;
    boolean DEBUG = true;

    public int numVectors = 0;

    /**
     * Logging object of output
     */
    private static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

    /**
     * @param vectorType
     * @param keysFeature
     * @param valuesFeature
     * @param predictionType         -- the type to use as a label indicator
     * @param predictionFeature
     * @param predictionFeatureValue
     * @param isExitOnError
     * @param filters
     */

    public BinaryLearningListener (
            String vectorType,
            String keysFeature,
            String valuesFeature,
            String predictionType,
            String predictionFeature,
            String[] predictionFeatureValue,
            boolean isExitOnError,
            String... filters) {

        super("{\"1\":\"1.0\", \"-1\":\"-1.0\"}", isExitOnError, filters);
        this.vectorType = vectorType;
        this.keysFeature = keysFeature;
        this.valuesFeature = valuesFeature;
        this.predictionType = predictionType;
        this.predictionFeature = predictionFeature;
        this.predictionFeatureValue = predictionFeatureValue;
    }
    /***/

    @Override
    public List<SherlockVector> getVectorList(JCas jcas) throws CASException {
        // log.info("GetVectorList");
        String docId = this.docInfo.getID();

        ArrayList<SherlockVector> vectors = new ArrayList<SherlockVector>();
        if (jcas == null)
            return vectors;

        log.debug("Document used for training: " + docId);

        Type vectorTypeObj = jcas.getRequiredType(vectorType);
        Feature keysFeatureObj = vectorTypeObj.getFeatureByBaseName(keysFeature);
        Feature valuesFeatureObj = vectorTypeObj.getFeatureByBaseName(valuesFeature);

        Type predictionTypeObj = jcas.getRequiredType(predictionType);

        Collection<Annotation> predictionAnnotationList = AnnotationLibrarian.getAllAnnotationsOfType(jcas, predictionTypeObj, false);
        Collection<Annotation> validationList = AnnotationLibrarian.getAllAnnotationsOfType(jcas, vectorTypeObj, false);

        String label = "-1";

        if (validationList.size() > 0) {
            // INFO: There are two cases - the label indicator is a presence of annotations of a specific type in a cas
            // or a presence of a specific value of a feature of in annotations of specific type.
            if (StringUtils.isNotBlank(predictionFeature)) {
                /**
                 *  If the feature name is provided, look if it exists.
                 *  Look at the feature value.
                 *  If the value is equal to the provided value, label is 1.
                 */
                Feature predictionFeatureObj = predictionTypeObj.getFeatureByBaseName(predictionFeature);
                if (predictionAnnotationList.size() > 0) {
                    for (Annotation p : predictionAnnotationList) {
                        if (p.getFeatureValueAsString(predictionFeatureObj) != null) {
                            for (String fv : predictionFeatureValue) {
                                if (p.getFeatureValueAsString(predictionFeatureObj).equalsIgnoreCase(fv)) {
                                    label = "1";
                                }
                            }
                        }
                    }
                }
            } // end if prediction feature is not blank
            else {
                /**
                 * If the feature name is not provided, then simply look whether there are any annotations of the prediction type.
                 */
                if (predictionAnnotationList.size() > 0) {
                    label = "1";
                }
            }  // Label is assigned


            StringArray keys = null;
            StringArray values = null;
            /**
             * For every vector in the list, assign the label
             */
            for (Annotation v : validationList) {

                //Get the keys and values string arrays from the validation annotation
                keys = (StringArray) v.getFeatureValue(keysFeatureObj);
                values = (StringArray) v.getFeatureValue(valuesFeatureObj);

                String[][] vector = new String[2][keys.size()];
                keys.copyToArray(0, vector[0], 0, keys.size());
                values.copyToArray(0, vector[1], 0, values.size());

                //Add the vector to the list
                vectors.add(new SherlockVector(label, vector[0], vector[1]));

            } // end for
        } // if validation list size > 0

        numVectors += vectors.size();
        if(DEBUG) {
            for (SherlockVector v : vectors) {
                System.out.print(docId + " == " + v.getPrediction() + " : ");
                for (int i = 0; i < v.getKeys().length; i++) {
                    System.out.print(v.getKeys()[i] + "=" + v.getValues()[i] + ";");
                }
                System.out.println(" ");
            }
        }
        return vectors;

    }
  /**/
}//BinaryLearningListener class
