package ca.concordia.cs.aseg.sbson.experiment.icse;

import ca.concordia.cs.aseg.sbson.core.Utils;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodDeclaration;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodInvocation;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode.AnnotationScanner;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode.BytecodeReader;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode.JarParser;
import gr.uom.java.xmi.*;
import gr.uom.java.xmi.diff.UMLAttributeDiff;
import gr.uom.java.xmi.diff.UMLClassDiff;
import gr.uom.java.xmi.diff.UMLModelDiff;
import gr.uom.java.xmi.diff.UMLOperationDiff;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import java.io.*;
import java.nio.Buffer;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

public class Experiment {

    private static List<String> clientsNotUsingASM;
    private static List<String> dependenciesNotUsingASM;

    public void getBCD(String[][] apis) throws IOException {
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
                /*
                 * Method - Removal, Visibility Loss, Return type change (e.g.,
                 * boolean to void), Parameter list change, Exception list
                 * change
                 *
                 * Addition, Visibility gain (e.g., from private to public or
                 * protected), Deprecation (e.g., deprecated method removal)
                 */

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
    }

    public void usageExperiment(String dataFile, String jarLoc, String outputLocation, int usageExperimentMode) throws IOException {
        //read file, for each entry perform usage test.
        FileReader fileReader = new FileReader(new File(dataFile));
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = "";
        int count = 0;
        clientsNotUsingASM = new ArrayList<>();
        dependenciesNotUsingASM = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            if (count % 10000 == 0) {
                System.out.println("processed " + count);
            }
            String[] dataEntry = line.split(",");
            if (usageExperimentMode == 0) {
                directUsageExperiment(dataEntry, jarLoc, outputLocation);
            } else {
                indirectUsageExperiment(dataEntry, jarLoc, outputLocation);
            }
            count++;

        }
        System.out.println("processed " + count);
    }

    public void directUsageExperiment(String[] dataEntry, String jarsLoc, String outputLocation) throws IOException {
        String client, dependency, asm1, asm2;
        client = dataEntry[0].trim();
        asm1 = dataEntry[1].trim();
        dependency = dataEntry[2].trim();
        asm2 = dataEntry[3].trim();

        if (clientsNotUsingASM.contains(client) || dependenciesNotUsingASM.contains(dependency)) {
            return;
        }

        //if asm1 and asm2 are in same category or if dependency is an asm library, exit, exit
        if (isSameASMCategory(asm1, asm2) || isASMLibrary(dependency)) {
            return;
        }

        //get and parse client and dependency jars
        File clientfile = new File(Init.getJarLoc(client, jarsLoc));
        File dependencyfile = new File(Init.getJarLoc(dependency, jarsLoc));
        if (clientfile == null || dependencyfile == null) return;
        JarFile clientJarFile = null;
        JarFile dependencyJarFile = null;
        try {
            clientJarFile = new JarFile(clientfile);
            dependencyJarFile = new JarFile(dependencyfile);
        } catch (ZipException e) {
            return;
        }


        UMLModel clientUMLModel = new BytecodeReader(clientJarFile).getUmlModel();
        UMLModel dependencyUMLModel = new BytecodeReader(dependencyJarFile).getUmlModel();
        List<UMLClass> clientClassesUsingASM = getClassesUsingAPI(clientUMLModel, "org.objectweb.asm.ClassVisitor");
        List<UMLClass> dependencyClassesUsingASM = getClassesUsingAPI(dependencyUMLModel, "org.objectweb.asm.ClassVisitor");

//        System.out.println(clientUMLModel.getClassList().size());
//        System.out.println(dependencyUMLModel.getClassList().size());
//        System.out.println(clientClassesUsingASM.size());
//        System.out.println(dependencyClassesUsingASM.size());

        if (clientClassesUsingASM.size() == 0) {
            clientsNotUsingASM.add(client);
        }
        if (dependencyClassesUsingASM.size() == 0) {
            dependenciesNotUsingASM.add(dependency);
        }
        if (clientClassesUsingASM.size() > 0 && dependencyClassesUsingASM.size() > 0) {
            log(outputLocation, client + "\t\t" + asm1 + "\t\t" + dependency + "\t\t" + asm2, true);

            JarParser clientJarParser = new JarParser(clientfile);
            JarParser dependencyJarParser = new JarParser(dependencyfile);
            HashMap<MethodDeclaration, List<MethodInvocation>> clientMethodMap = clientJarParser.getMethodCalls();
            HashMap<MethodDeclaration, List<MethodInvocation>> dependencyMethodMap = dependencyJarParser.getMethodCalls();

            //get initial API dependency on respective asm ClassVisitor API in client and dependency
            Set<MethodDeclaration> initialClientAPIList = new TreeSet<>();
            for (UMLClass umlClass : clientClassesUsingASM) {
                initialClientAPIList.addAll(JarParser.getMethodDeclaration(clientMethodMap, umlClass.getName()));
            }

            Set<MethodDeclaration> initialDependencyAPIList = new TreeSet<>();
            for (UMLClass umlClass : dependencyClassesUsingASM) {
                initialDependencyAPIList.addAll(JarParser.getMethodDeclaration(dependencyMethodMap, umlClass.getName()));
            }

            System.out.println(initialClientAPIList.size());
            System.out.println(initialDependencyAPIList.size());

            //get transitive API dependencies
            Set<MethodDeclaration> filteredClientAPIList = filterCallGraph(clientMethodMap, initialClientAPIList);
            Set<MethodDeclaration> filteredDependencyAPIList = filterCallGraph(dependencyMethodMap, initialDependencyAPIList);
            System.out.println(filteredClientAPIList.size());
            System.out.println(filteredDependencyAPIList.size());

            //check if any API in client list calls API in dependency list
            for (MethodDeclaration methodDeclaration : filteredClientAPIList) {
                for (MethodInvocation methodInvocation : clientMethodMap.get(methodDeclaration)) {

                    for (MethodDeclaration methodDeclaration1 : filteredDependencyAPIList) {
                        //System.out.println(methodDeclaration1.getClassName());
                        if (methodDeclaration1.getClassName().equals(methodInvocation.getOwner())) {
                            log(outputLocation, "\t\t" + methodDeclaration.convertToString() + " uses " + methodDeclaration1.convertToString(), true);
                        }
                    }
                }
            }
        }
        //garbage collection for saving memory
    }

    public void indirectUsageExperiment(String[] dataEntry, String jarsLoc, String outputLocation) throws IOException {
        String client, dependency1, dependency2, asm1, asm2;
        client = dataEntry[0].trim();
        asm1 = dataEntry[1].trim();
        dependency1 = dataEntry[2].trim();
        dependency2 = dataEntry[3].trim();
        asm2 = dataEntry[4].trim();

        if (clientsNotUsingASM.contains(client) || dependenciesNotUsingASM.contains(dependency1) || dependenciesNotUsingASM.contains(dependency2)) {
            return;
        }

        //if asm1 and asm2 are in same category or if dependency is an asm library, exit, exit
        if (isSameASMCategory(asm1, asm2) || isASMLibrary(dependency1) || isASMLibrary(dependency2)) {
            return;
        }

        //get and parse client and dependency jars
        File clientfile = new File(Init.getJarLoc(client, jarsLoc));
        File dependencyfile1 = new File(Init.getJarLoc(dependency1, jarsLoc));
        File dependencyfile2 = new File(Init.getJarLoc(dependency2, jarsLoc));
        if (clientfile == null || dependencyfile1 == null || dependencyfile2 == null) return;
        JarFile clientJarFile = null;
        JarFile dependencyJarFile1 = null;
        JarFile dependencyJarFile2 = null;
        try {
            clientJarFile = new JarFile(clientfile);
            dependencyJarFile1 = new JarFile(dependencyfile1);
            dependencyJarFile2 = new JarFile(dependencyfile2);
        } catch (ZipException e) {
            return;
        }


        UMLModel clientUMLModel = new BytecodeReader(clientJarFile).getUmlModel();
        UMLModel dependencyUMLModel1 = new BytecodeReader(dependencyJarFile1).getUmlModel();
        UMLModel dependencyUMLModel2 = new BytecodeReader(dependencyJarFile2).getUmlModel();
        List<UMLClass> clientClassesUsingASM = getClassesUsingAPI(clientUMLModel, "org.objectweb.asm.ClassVisitor");
        List<UMLClass> dependencyClassesUsingASM2 = getClassesUsingAPI(dependencyUMLModel2, "org.objectweb.asm.ClassVisitor");

//        System.out.println(clientUMLModel.getClassList().size());
//        System.out.println(dependencyUMLModel.getClassList().size());
//        System.out.println(clientClassesUsingASM.size());
//        System.out.println(dependencyClassesUsingASM.size());

        if (clientClassesUsingASM.size() == 0) {
            clientsNotUsingASM.add(client);
        }
        if (dependencyClassesUsingASM2.size() == 0) {
            dependenciesNotUsingASM.add(dependency2);
        }
        if (clientClassesUsingASM.size() > 0 && dependencyClassesUsingASM2.size() > 0) {
            log(outputLocation, client + "\t\t" + asm1 + "\t\t" + dependency1 + "\t\t" + dependency2 + "\t\t" + asm2, true);

            JarParser clientJarParser = new JarParser(clientfile);
            JarParser dependencyJarParser1 = new JarParser(dependencyfile1);
            JarParser dependencyJarParser2 = new JarParser(dependencyfile2);
            HashMap<MethodDeclaration, List<MethodInvocation>> clientMethodMap = clientJarParser.getMethodCalls();
            HashMap<MethodDeclaration, List<MethodInvocation>> dependencyMethodMap1 = dependencyJarParser1.getMethodCalls();
            HashMap<MethodDeclaration, List<MethodInvocation>> dependencyMethodMap2 = dependencyJarParser2.getMethodCalls();

            //get initial API dependency on respective asm ClassVisitor API in client and dependency
            Set<MethodDeclaration> initialClientAPIList = new TreeSet<>();
            for (UMLClass umlClass : clientClassesUsingASM) {
                initialClientAPIList.addAll(JarParser.getMethodDeclaration(clientMethodMap, umlClass.getName()));
            }

            Set<MethodDeclaration> initialDependencyAPIList2 = new TreeSet<>();
            for (UMLClass umlClass : dependencyClassesUsingASM2) {
                initialDependencyAPIList2.addAll(JarParser.getMethodDeclaration(dependencyMethodMap2, umlClass.getName()));
            }

            System.out.println(initialClientAPIList.size());
            System.out.println(initialDependencyAPIList2.size());

            //get transitive API dependencies
            Set<MethodDeclaration> filteredClientAPIList = filterCallGraph(clientMethodMap, initialClientAPIList);
            Set<MethodDeclaration> filteredDependencyAPIList2 = filterCallGraph(dependencyMethodMap2, initialDependencyAPIList2);
            System.out.println(filteredClientAPIList.size());
            System.out.println(filteredDependencyAPIList2.size());

            //check if any API in client list calls API in dependency1 list
            for (MethodDeclaration methodDeclaration : filteredClientAPIList) {
                Set<MethodDeclaration> initialDependencyAPIList1 = new TreeSet<>();
                for (MethodInvocation methodInvocation : clientMethodMap.get(methodDeclaration)) {

                    for (MethodDeclaration methodDeclaration1 : dependencyMethodMap1.keySet()) {
                        //System.out.println(methodDeclaration1.getClassName());
                        if (methodDeclaration1.getClassName().equals(methodInvocation.getOwner())) {
                            initialDependencyAPIList1.add(methodDeclaration1);
                        }
                    }
                }

                Set<MethodDeclaration> filteredDependencyAPIList1 = filterCallGraph(dependencyMethodMap1, initialDependencyAPIList1);

                //check if any filtered API in dependency1 list calls API in dependency2 list
                for (MethodDeclaration methodDeclaration1 : filteredDependencyAPIList1) {
                    for (MethodInvocation methodInvocation1 : dependencyMethodMap1.get(methodDeclaration1)) {

                        for (MethodDeclaration methodDeclaration2 : filteredDependencyAPIList2) {
                            //System.out.println(methodDeclaration1.getClassName());
                            if (methodDeclaration2.getClassName().equals(methodInvocation1.getOwner())) {
                                log(outputLocation, "\t\t" + methodDeclaration.convertToString() + " uses " + methodDeclaration1.convertToString() + " uses " + methodDeclaration2.convertToString(), true);
                            }
                        }
                    }
                }
            }


        }

    }


    public static TreeSet<MethodDeclaration> filterCallGraph(
            HashMap<MethodDeclaration, List<MethodInvocation>> allProductMethodCalls, Set<MethodDeclaration> initailAPIList) {
        TreeSet<MethodDeclaration> impactMethodSet = new TreeSet<MethodDeclaration>();
        getMethodDependencies(allProductMethodCalls, initailAPIList, impactMethodSet);

        // System.out.println("Internal APIs\n" + initailAPIList);
        //System.out.println("Internal method dependencies\n" + impactMethodSet);
        impactMethodSet.addAll(initailAPIList);

        return impactMethodSet;

    }

    private static void getMethodDependencies(
            HashMap<MethodDeclaration, List<MethodInvocation>> allProductMethodCalls,
            Set<MethodDeclaration> apiList,
            Set<MethodDeclaration> allMatchesHolder) {

        Set<MethodDeclaration> matches = new TreeSet<MethodDeclaration>();
        for (MethodDeclaration apiDeclaration : apiList) {
            for (MethodDeclaration declaration : allProductMethodCalls.keySet()) {
                if (apiDeclaration.equals(declaration))
                    continue;
                for (MethodInvocation invocation : allProductMethodCalls
                        .get(declaration)) {
                    if (invocation.getName().equals(apiDeclaration.getName())
                            && invocation.getOwner().equals(
                            apiDeclaration.getClassName())) {
                        matches.add(declaration);
                    }
                }
            }
        }

        if (matches.size() > 0) {
            // try {
            Set<MethodDeclaration> refinedMatches = new TreeSet<MethodDeclaration>();
            for (MethodDeclaration method : matches) {
                if (!allMatchesHolder.contains(method)) {
                    refinedMatches.add(method);
                }
            }
            allMatchesHolder.addAll(refinedMatches);
            getMethodDependencies(allProductMethodCalls, refinedMatches,
                    allMatchesHolder);


        }
    }


    private static List<UMLClass> getClassesUsingAPI(UMLModel umlModel, String API) {
        List<UMLClass> classesUsingASM = new ArrayList<>();
        for (UMLGeneralization umlGeneralization : umlModel.getGeneralizationList()) {
            //System.out.println(umlGeneralization.getParent().toString());
            if (umlGeneralization.getParent().toString().equals(API)) {
                classesUsingASM.add(umlGeneralization.getChild());
            }
        }
        for (UMLRealization umlRealization : umlModel.getRealizationList()) {
            if (umlRealization.getSupplier().toString().equals(API)) {
                classesUsingASM.add(umlRealization.getClient());
            }
        }
        return classesUsingASM;
    }

    private boolean isSameASMCategory(String asm1, String asm2) {
        boolean result = false;
        String prefix1 = asm1.substring(0, asm1.indexOf(":"));
        String prefix2 = asm2.substring(0, asm1.indexOf(":"));
        if (prefix1.equals(prefix2)) {
            result = true;
        }
        return result;
    }

    private boolean isASMLibrary(String lib) {
        boolean result = false;
        if (lib.startsWith("asm:") || lib.startsWith("org.ow2.asm:")) {
            result = true;
        }
        return result;
    }

    public static void getDeprecatoinHistory(String jarLocation, String outputBaseLocation) {
        RemoteVirtuosoConnection remoteVirtuosoConnection = RemoteVirtuosoConnection.getInstance();

        String[] libs = new String[]{"commons-io:commons-io",
                "log4j:log4j",
                "org.slf4j:slf4j-api",
                "ch.qos.logback:logback-classic",
                "commons-lang:commons-lang",
                "commons-logging:commons-logging"};

        for (String lib : libs) {
            String libSaveName = lib.substring(lib.indexOf(":") + 1);
            String saveLocation = outputBaseLocation + libSaveName;
            Set<String> clientResults = new TreeSet<String>();
            try {

                String proj = "<http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#" + lib.trim() + ">";
                String query;
                TupleQueryResult resultSet;
                query = Queries.getProjectReleases(proj);

                resultSet = remoteVirtuosoConnection.executeQuery(query);

                while (resultSet.hasNext()) {
                    BindingSet bs = resultSet.next();
                    clientResults.add(bs.getBinding("release").getValue().stringValue());
                }


                for (String release : clientResults) {
                    String gav = Utils.shortenURI(release);
                    String[] url = Utils.createUrl(gav, "jar", ":");
                    String fileLocation = jarLocation + url[1];
                    File file = new File(fileLocation);
                    if (file.exists()) {
                        HashMap<String, List<MethodDeclaration>> classesWithDeprecatedMethods = getDeprecatedMethods(file);
                        for (String str : classesWithDeprecatedMethods.keySet()) {
                            List<MethodDeclaration> deprecatedMethods = classesWithDeprecatedMethods.get(str);
                            for (MethodDeclaration methodDeclaration : deprecatedMethods) {
                                log(saveLocation, file.getName() + ", " + str + ", " + methodDeclaration, true);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Exception: ");
            }
        }
    }

    public static void getDeprecatoinUsage(String releaseClientsFileLocation, String jarLocation, String outputBaseLocation) throws IOException {
        RemoteVirtuosoConnection remoteVirtuosoConnection = RemoteVirtuosoConnection.getInstance();

        String[] libs = new String[]{"commons-io:commons-io",
                "log4j:log4j",
                "org.slf4j:slf4j-api",
                "ch.qos.logback:logback-classic",
                "commons-lang:commons-lang",
                "commons-logging:commons-logging"};

        Map<String, Set<String>> map = parseDepractionUsageInputFile(releaseClientsFileLocation);

        for (String lib : libs) {
            String libSaveName = lib.substring(lib.indexOf(":") + 1);
            String saveLocation = outputBaseLocation + libSaveName;
            Set<String> clientResults = new TreeSet<String>();
            try {

                String proj = "<http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#" + lib.trim() + ">";
                String query;
                TupleQueryResult resultSet;
                query = Queries.getProjectReleases(proj);

                resultSet = remoteVirtuosoConnection.executeQuery(query);

                while (resultSet.hasNext()) {
                    BindingSet bs = resultSet.next();
                    clientResults.add(bs.getBinding("release").getValue().stringValue());
                }


                for (String release : clientResults) {
                    String gav = Utils.shortenURI(release);
                    String[] url = Utils.createUrl(gav, "jar", ":");
                    String fileLocation = jarLocation + url[1];
                    File file = new File(fileLocation);
                    if (file.exists()) {
                        HashMap<String, List<MethodDeclaration>> classesWithDeprecatedMethods = getDeprecatedMethods(file);
                        if (classesWithDeprecatedMethods.size() > 0) {
                            Set<String> releaseResults = map.get(gav);
                            if (releaseResults != null) {
                                if (releaseResults.size() > 0) {
                                    for (String client : releaseResults) {
                                        //get client jar, parse, get calls to deprecated release methods
                                        String gav2 = Utils.shortenURI(client);
                                        String[] url2 = Utils.createUrl(gav2, "jar", ":");
                                        String fileLocation2 = jarLocation + url2[1];

                                        File clientfile = new File(fileLocation2);
                                        if (clientfile.exists()) {
                                            JarParser clientJarParser = new JarParser(clientfile);
                                            HashMap<MethodDeclaration, List<MethodInvocation>> clientMethodMap = clientJarParser.getMethodCalls();
                                            checkDeprecationUSage(saveLocation, gav, gav2, classesWithDeprecatedMethods, clientMethodMap);
                                            //System.out.println(gav + "\t" + gav2 + "\t" + clientMethodMap.size());
                                        }
                                    }
                                }
                            }
                        }
                        /*for (String str : classesWithDeprecatedMethods.keySet()) {
                            List<MethodDeclaration> deprecatedMethods = classesWithDeprecatedMethods.get(str);
                            for (MethodDeclaration methodDeclaration : deprecatedMethods) {
                                log(saveLocation, file.getName() + ", " + str + ", " + methodDeclaration, true);
                            }
                        }*/
                    }
                }

            } catch (Exception e) {
                System.err.println("Exception: ");
            }
        }
    }

    public static HashMap<String, List<MethodDeclaration>> getDeprecatedMethods(File fileLocation) throws IOException {
        JarFile jarFile = new JarFile(fileLocation);
        HashMap<String, List<MethodDeclaration>> allClassMethodsInfo = new HashMap<String, List<MethodDeclaration>>();
        List<ClassReader> classReaders = new JarParser().getClasses(jarFile);
        for (ClassReader cr : classReaders) {
            AnnotationScanner annotationScanner = new AnnotationScanner(Opcodes.ASM5);
            cr.accept(annotationScanner, ClassReader.SKIP_DEBUG);
            HashMap<String, List<MethodDeclaration>> deprecatedMethods = annotationScanner.getClassMethodsInfo();
            for (String className : deprecatedMethods.keySet()) {
                if (deprecatedMethods.get(className).size() > 0) {
                    allClassMethodsInfo.put(className, deprecatedMethods.get(className));
                }
            }

        }

        return allClassMethodsInfo;

    }

    private static Map<String, Set<String>> parseDepractionUsageInputFile(String fileLocation) throws IOException {
        Map<String, Set<String>> map = new HashMap<>();
        FileReader fileReader = new FileReader(fileLocation);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            line = line.replaceAll("", "");
            String[] lineParts = line.split(",");
            String release = lineParts[0].trim();
            String client = lineParts[1];
            Set<String> clients = new TreeSet<>();
            if (map.containsKey(release)) {
                clients = map.get(release);
            }
            clients.add(client);
            map.put(release, clients);
        }
        return map;
    }

    private static void checkDeprecationUSage(String saveLocation, String release, String client, HashMap<String, List<MethodDeclaration>> classesWithDeprecatedMethods, HashMap<MethodDeclaration, List<MethodInvocation>> clientMethodMap) throws IOException {
        Set<MethodDeclaration> allDeprecatedMethods = new TreeSet<>();
        for (String str : classesWithDeprecatedMethods.keySet()) {
            allDeprecatedMethods.addAll(classesWithDeprecatedMethods.get(str));
        }
        for (MethodDeclaration clientMethod : clientMethodMap.keySet()) {
            List<MethodInvocation> clientMethodInvocations = clientMethodMap.get(clientMethod);
            for (MethodInvocation methodInvocation : clientMethodInvocations) {
                if (allDeprecatedMethods.contains(methodInvocation.getInvocationSource())) {
                    log(saveLocation, "Found usage: " + client + " (" + clientMethod + ") ==USES==> " + release + " (" + methodInvocation.getInvocationSource() + ")", true);
                }
            }
        }
    }

    public static void log(String file, String line, boolean append) throws IOException {
        FileWriter fileWriter = new FileWriter(new File(file), append);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        // System.out.println(line);
        bufferedWriter.write(line + "\n");
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    //org/apache/commons/io/IOUtils	name=toString	desc=([BLjava/lang/String;)Ljava/lang/String;
    public static void extraAnalysis1(String releaseClientsFileLocation, String jarLocation) throws IOException {
        RemoteVirtuosoConnection remoteVirtuosoConnection = RemoteVirtuosoConnection.getInstance();

        String[] libs = new String[]{"commons-io:commons-io"};

        Map<String, Set<String>> map = parseDepractionUsageInputFile(releaseClientsFileLocation);

        for (String lib : libs) {
            String libSaveName = lib.substring(lib.indexOf(":") + 1);
            Set<String> clientResults = new TreeSet<String>();
            try {

                String proj = "<http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#" + lib.trim() + ">";
                String query;
                TupleQueryResult resultSet;
                query = Queries.getProjectReleases(proj);

                resultSet = remoteVirtuosoConnection.executeQuery(query);

                while (resultSet.hasNext()) {
                    BindingSet bs = resultSet.next();
                    clientResults.add(bs.getBinding("release").getValue().stringValue());
                }


                for (String release : clientResults) {
                    String gav = Utils.shortenURI(release);
                    String[] url = Utils.createUrl(gav, "jar", ":");
                    String fileLocation = jarLocation + url[1];
                    File file = new File(fileLocation);
                    if (file.exists()) {
                        Set<String> releaseResults = map.get(gav);
                        if (releaseResults != null) {
                            if (releaseResults.size() > 0) {
                                for (String client : releaseResults) {
                                    //get client jar, parse, get calls to deprecated release methods
                                    String gav2 = Utils.shortenURI(client);
                                    String[] url2 = Utils.createUrl(gav2, "jar", ":");
                                    String fileLocation2 = jarLocation + url2[1];

                                    File clientfile = new File(fileLocation2);
                                    if (clientfile.exists()) {
                                        JarParser clientJarParser = new JarParser(clientfile);
                                        HashMap<MethodDeclaration, List<MethodInvocation>> clientMethodMap = clientJarParser.getMethodCalls();
                                        for (MethodDeclaration clientMethod : clientMethodMap.keySet()) {
                                            List<MethodInvocation> clientMethodInvocations = clientMethodMap.get(clientMethod);
                                            for (MethodInvocation methodInvocation : clientMethodInvocations) {
                                                if (methodInvocation.getInvocationSource().getClassName().equals("org/apache/commons/io/IOUtils")
                                                        && methodInvocation.getInvocationSource().getName().equals("toString")
                                                        && methodInvocation.getInvocationSource().getDesc().equals("([BLjava/lang/String;)Ljava/lang/String;")) {
                                                    log("D:\\ICSE\\output-csv\\deprecation\\usage\\extra1", "Found usage: " + gav2 + " (" + clientMethod + ") ==USES==> " + gav + " (" + methodInvocation.getInvocationSource() + ")", true);
                                                }
                                            }
                                        }
                                        //System.out.println(gav + "\t" + gav2 + "\t" + clientMethodMap.size());
                                    }
                                }
                            }

                        }
                        /*for (String str : classesWithDeprecatedMethods.keySet()) {
                            List<MethodDeclaration> deprecatedMethods = classesWithDeprecatedMethods.get(str);
                            for (MethodDeclaration methodDeclaration : deprecatedMethods) {
                                log(saveLocation, file.getName() + ", " + str + ", " + methodDeclaration, true);
                            }
                        }*/
                    }
                }

            } catch (Exception e) {
                System.err.println("Exception: ");
            }
        }
    }

    public static void extraAnalysis2(String releaseClientsFileLocation, String jarLocation) throws IOException {

        List<String[]> apis = new ArrayList<>();
        FileReader fileReader = new FileReader("D:\\ICSE\\output-csv\\deprecation\\usage\\extra2-input.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            apis.add(line.split(","));
        }

        RemoteVirtuosoConnection remoteVirtuosoConnection = RemoteVirtuosoConnection.getInstance();

        String[] libs = new String[]{"commons-io:commons-io"};

        Map<String, Set<String>> map = parseDepractionUsageInputFile(releaseClientsFileLocation);

        for (String lib : libs) {
            String libSaveName = lib.substring(lib.indexOf(":") + 1);
            Set<String> clientResults = new TreeSet<String>();
            try {

                String proj = "<http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#" + lib.trim() + ">";
                String query;
                TupleQueryResult resultSet;
                query = Queries.getProjectReleases(proj);

                resultSet = remoteVirtuosoConnection.executeQuery(query);

                while (resultSet.hasNext()) {
                    BindingSet bs = resultSet.next();
                    clientResults.add(bs.getBinding("release").getValue().stringValue());
                }


                for (String release : clientResults) {
                    String gav = Utils.shortenURI(release);
                    String[] url = Utils.createUrl(gav, "jar", ":");
                    String fileLocation = jarLocation + url[1];
                    File file = new File(fileLocation);
                    if (file.exists()) {
                        Set<String> releaseResults = map.get(gav);
                        if (releaseResults != null) {
                            if (releaseResults.size() > 0) {
                                for (String client : releaseResults) {
                                    //get client jar, parse, get calls to deprecated release methods
                                    String gav2 = Utils.shortenURI(client);
                                    String[] url2 = Utils.createUrl(gav2, "jar", ":");
                                    String fileLocation2 = jarLocation + url2[1];

                                    File clientfile = new File(fileLocation2);
                                    if (clientfile.exists()) {
                                        JarParser clientJarParser = new JarParser(clientfile);
                                        HashMap<MethodDeclaration, List<MethodInvocation>> clientMethodMap = clientJarParser.getMethodCalls();
                                        for (MethodDeclaration clientMethod : clientMethodMap.keySet()) {
                                            List<MethodInvocation> clientMethodInvocations = clientMethodMap.get(clientMethod);
                                            for (MethodInvocation methodInvocation : clientMethodInvocations) {
                                                for (String[] api : apis) {
                                                    if (methodInvocation.getInvocationSource().getClassName().equals(api[0])
                                                            && methodInvocation.getInvocationSource().getName().equals(api[1])
                                                            && methodInvocation.getInvocationSource().getDesc().equals(api[2])) {
                                                        log("D:\\ICSE\\output-csv\\deprecation\\usage\\extra2", "Found usage: " + gav2 + " (" + clientMethod + ") ==USES==> " + gav + " (" + methodInvocation.getInvocationSource() + ")", true);
                                                    }
                                                }
                                            }
                                        }
                                        //System.out.println(gav + "\t" + gav2 + "\t" + clientMethodMap.size());
                                    }
                                }
                            }

                        }
                        /*for (String str : classesWithDeprecatedMethods.keySet()) {
                            List<MethodDeclaration> deprecatedMethods = classesWithDeprecatedMethods.get(str);
                            for (MethodDeclaration methodDeclaration : deprecatedMethods) {
                                log(saveLocation, file.getName() + ", " + str + ", " + methodDeclaration, true);
                            }
                        }*/
                    }
                }

            } catch (Exception e) {
                System.err.println("Exception: ");
            }
        }
    }
}
