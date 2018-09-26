package ca.concordia.cs.aseg.sbson.experiment.icse;

import ca.concordia.cs.aseg.sbson.core.Utils;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode.BytecodeReader;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.UMLAttributeDiff;
import gr.uom.java.xmi.diff.UMLClassDiff;
import gr.uom.java.xmi.diff.UMLModelDiff;
import gr.uom.java.xmi.diff.UMLOperationDiff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarFile;

public class Experiment {

 /*   public void getBCD(String[][] apis) throws IOException {
        String prior, current;
        int priorBC, currentBC = 0, priorNBC, currentNBC = 0;
        UMLModel priorAPI, currentAPI = null;

        if (apis.length > 0) {
            for (int i = 0; i < apis.length; i++) {
                // get details for base api
                prior = apis[i][0];
                File priorfile = Init.getJarLocal(prior);
                if (priorfile == null) continue;
                JarFile priorJarFile = new JarFile(priorfile);
                priorAPI = new BytecodeReader(priorJarFile).getUmlModel();


                Set<String> breakingAPIs = new TreeSet<>();
                current = apis[i][1];
                File currentfile = Init.getJarLocal(current);
                if (currentfile == null) continue;
                JarFile currentJarFile = new JarFile(currentfile);
                currentAPI = new BytecodeReader(currentJarFile).getUmlModel();
                // System.out.println(current+", ");

                UMLModelDiff modelDiff = priorAPI.diff(currentAPI);
                List<UMLClassDiff> commonClassDiffList = modelDiff.getCommonClassDiffList();
                // Type - Removal, Visibility Loss, Super-type change
                // Field - Removal, Visibility Loss (e.g., public to private),
                // Type change (e.g., double to integer), Default value change
                *//*
                 * Method - Removal, Visibility Loss, Return type change (e.g.,
                 * boolean to void), Parameter list change, Exception list
                 * change
                 *
                 * Addition, Visibility gain (e.g., from private to public or
                 * protected), Deprecation (e.g., deprecated method removal)
                 *//*

                // Addition, Visibility gain (e.g., from private to public or
                // protected), Deprecation (e.g., deprecated method removal)

                // get breaking change count
                currentBC = currentBC + modelDiff.getRemovedAnonymousClasses().size()
                        + modelDiff.getRemovedClasses().size() + modelDiff.getRemovedGeneralizations().size()
                        + modelDiff.getRemovedRealizations().size() + modelDiff.getClassRenameDiffList().size()
                        + modelDiff.getClassMoveDiffList().size();

                currentNBC = currentNBC + modelDiff.getAddedClasses().size();
                if (commonClassDiffList != null)
                    for (UMLClassDiff classDiff : commonClassDiffList) {
                        currentBC = currentBC + classDiff.getRemovedAttributes().size()
                                + classDiff.getRemovedOperations().size();
                        currentNBC = currentNBC + classDiff.getAddedAttributes().size()
                                + classDiff.getAddedOperations().size();

                        List<UMLAttributeDiff> attributeDiffList = classDiff.getAttributeDiffList();
                        List<UMLOperationDiff> operationDiffList = classDiff.getOperationDiffList();

                        if (attributeDiffList != null)
                            for (UMLAttributeDiff attributeDiff : attributeDiffList) {
                                if (attributeDiff.isAttributeRenamed()) {
                                    currentBC++;
                                }
                                if (attributeDiff.isTypeChanged()) {
                                    currentBC++;
                                }
                                if (attributeDiff.isVisibilityChanged()) {
                                    currentBC++;
                                }
                            }

                        if (operationDiffList != null) {
                            for (UMLOperationDiff operationDiff : operationDiffList) {
                                if (operationDiff.isOperationRenamed()) {
                                    currentBC++;
                                    breakingAPIs.add(operationDiff.getRemovedOperation().toString());
                                }
                                if (operationDiff.isAbstractionChanged()) {
                                    currentBC++;
                                    breakingAPIs.add(operationDiff.getRemovedOperation().toString());
                                }
                                if (operationDiff.isReturnTypeChanged()) {
                                    currentBC++;
                                }
                                if (operationDiff.getParameterDiffList().size() > 0) {
                                    currentBC++;
                                }
                                if (operationDiff.isVisibilityChanged()) {
                                    // System.out.println(operationDiff.getRemovedOperation().getVisibility());
                                    // System.out.println(operationDiff.getAddedOperation().getVisibility());
                                    // currentBC++;
                                }
                            }
                        }
                    }
                // calculate BCD
                System.out.println(
                        current + ", " + currentBC + ", " + currentNBC + ", " + (double) currentBC / currentNBC);
//                prior = current;
//                priorBC = currentBC;
//                priorNBC = currentNBC;
//                priorAPI = currentAPI;

                //System.out.println(breakingAPIs);
            }
        }
    }*/

    protected void log(String file, String line, boolean append) throws IOException {
        FileWriter fileWriter = new FileWriter(new File(file), append);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        // System.out.println(line);
        bufferedWriter.write(line + "\n");
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    protected String getJarLoc(String gav, String baseLoc) {
        String jarFile = null;
        String[] jarURLs = Utils.createUrl(gav, "jar", ":");
        String fileSaveName = baseLoc + jarURLs[1].trim();
        return fileSaveName;
    }
}
