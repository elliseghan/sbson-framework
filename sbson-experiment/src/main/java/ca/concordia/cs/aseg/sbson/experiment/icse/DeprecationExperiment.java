package ca.concordia.cs.aseg.sbson.experiment.icse;

import ca.concordia.cs.aseg.sbson.core.Utils;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodDeclaration;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodInvocation;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode.AnnotationScanner;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode.JarParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

public class DeprecationExperiment extends Experiment {

   /* public static void main(String[] args) {
        //Init.getDeprecationLibClients("D:\\ICSE\\jars\\","D:\\ICSE\\output-csv\\deprecation-input.csv");
        // Experiment.getDeprecatoinHistory("D:\\ICSE\\jars\\","D:\\ICSE\\output-csv\\deprecation\\");
        //Experiment.getDeprecatoinUsage("D:\\ICSE\\output-csv\\deprecation\\deprecation-input.csv","D:\\ICSE\\jars\\","D:\\ICSE\\output-csv\\deprecation\\usage\\");
        // Experiment.extraAnalysis2("D:\\ICSE\\output-csv\\deprecation\\deprecation-input.csv","D:\\ICSE\\jars\\");
    }

    public void getDeprecatoinHistory(String jarLocation, String outputBaseLocation) {
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

                query = Queries.getProjectReleases(proj);
                TupleQueryResult resultSet;
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

    public void getDeprecatoinUsage(String releaseClientsFileLocation, String jarLocation, String outputBaseLocation) throws IOException {
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
                        *//*for (String str : classesWithDeprecatedMethods.keySet()) {
                            List<MethodDeclaration> deprecatedMethods = classesWithDeprecatedMethods.get(str);
                            for (MethodDeclaration methodDeclaration : deprecatedMethods) {
                                log(saveLocation, file.getName() + ", " + str + ", " + methodDeclaration, true);
                            }
                        }*//*
                    }
                }

            } catch (Exception e) {
                System.err.println("Exception: ");
            }
        }
    }

    public HashMap<String, List<MethodDeclaration>> getDeprecatedMethods(File fileLocation) throws IOException {
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

    private Map<String, Set<String>> parseDepractionUsageInputFile(String fileLocation) throws IOException {
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

    private void checkDeprecationUSage(String saveLocation, String release, String client, HashMap<String, List<MethodDeclaration>> classesWithDeprecatedMethods, HashMap<MethodDeclaration, List<MethodInvocation>> clientMethodMap) throws IOException {
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

    //org/apache/commons/io/IOUtils	name=toString	desc=([BLjava/lang/String;)Ljava/lang/String;
    public void extraAnalysis1(String releaseClientsFileLocation, String jarLocation) throws IOException {
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
                        *//*for (String str : classesWithDeprecatedMethods.keySet()) {
                            List<MethodDeclaration> deprecatedMethods = classesWithDeprecatedMethods.get(str);
                            for (MethodDeclaration methodDeclaration : deprecatedMethods) {
                                log(saveLocation, file.getName() + ", " + str + ", " + methodDeclaration, true);
                            }
                        }*//*
                    }
                }

            } catch (Exception e) {
                System.err.println("Exception: ");
            }
        }
    }

    public void extraAnalysis2(String releaseClientsFileLocation, String jarLocation) throws IOException {

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
                        *//*for (String str : classesWithDeprecatedMethods.keySet()) {
                            List<MethodDeclaration> deprecatedMethods = classesWithDeprecatedMethods.get(str);
                            for (MethodDeclaration methodDeclaration : deprecatedMethods) {
                                log(saveLocation, file.getName() + ", " + str + ", " + methodDeclaration, true);
                            }
                        }*//*
                    }
                }

            } catch (Exception e) {
                System.err.println("Exception: ");
            }
        }
    }*/

}
