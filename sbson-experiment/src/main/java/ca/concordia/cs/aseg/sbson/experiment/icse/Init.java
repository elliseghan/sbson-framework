package ca.concordia.cs.aseg.sbson.experiment.icse;

import ca.concordia.cs.aseg.sbson.core.Utils;
import ca.concordia.cs.aseg.sbson.ontologies.publisher.code.JavaByteCodePublisher;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.domain_specific.abox.BuildABox;
import org.apache.commons.io.FileUtils;


import java.io.*;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.query.*;
import org.openrdf.repository.Repository;

import com.fluidops.fedx.Config;
import com.fluidops.fedx.FedXFactory;
import com.fluidops.fedx.exception.FedXRuntimeException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class Init {

    public static void getALLASMLibs(String csvFile, String destinationPath) {
        File file = new File(csvFile);
        String line = "";
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                String url[] = Utils.createUrl(line.trim(), "jar", ":");
                Utils.getFileFromURL(url[0], destinationPath + url[1]);


                url = Utils.createUrl(line.trim(), "sources.jar", ":");
                boolean result = Utils.getFileFromURL(url[0], destinationPath + url[1]);
            }
        } catch (Exception e) {
            System.err.println("Exception: " + line);
        }
    }

    public static void parseALLASMLibs(String libLocation, String outputLocation) {
        Collection<File> asmLibs = FileUtils.listFiles(new File(libLocation), new String[]{"jar"}, true);
        try {
            for (File jar : asmLibs) {
                //ignore ASM sources.jar results
                if (jar.getAbsolutePath().endsWith("-sources.jar")) {
                    continue;
                }
                String[] gav = Utils.getGAVFromFileName(jar.getAbsolutePath(), libLocation);
                String gavStr = gav[0] + ":" + gav[1] + ":" + gav[2];
                if (gavStr != null) {
                    createTriplesFromJar(jar, gavStr, outputLocation);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception: ");
        }
    }

    public static void createTriplesFromJar(File jarFile, String projectGAV, String outputLocation) {
        System.out.println("Publishing Code triples...");
        String projectURI = BuildABox.BuildRelease(projectGAV);
        new JavaByteCodePublisher().publish(jarFile, projectURI, null, outputLocation);
        System.out.println("Done!\n");
    }

    public static void getASMClients(String libLocation, String outputLocation, int mode) {
        Set<String> clientResults = new TreeSet<String>();
        RemoteVirtuosoConnection remoteVirtuosoConnection = RemoteVirtuosoConnection.getInstance();

        Collection<File> asmLibs = FileUtils.listFiles(new File(libLocation), new String[]{"jar"}, true);
        try {
            for (File jar : asmLibs) {
                String[] gav = Utils.getGAVFromFileName(jar.getAbsolutePath(), libLocation);
                String gavStr = gav[0] + ":" + gav[1] + ":" + gav[2];
                if (gavStr != null) {
                    String asmLib = "<http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#" + gavStr + ">";
                    String query;
                    TupleQueryResult resultSet;
                    if (mode == 0) {
                        query = Queries.getASMClientsWithConflicts(asmLib);
                    } else {
                        query = Queries.getASMClientsWithConflicts2(asmLib);
                    }
                    resultSet = remoteVirtuosoConnection.executeQuery(query);

                    while (resultSet.hasNext()) {
                        BindingSet bs = resultSet.next();
                        if (mode == 0) {
                            clientResults.add(bs.getBinding("client").getValue() + ", " + asmLib + ", " + bs.getBinding("dep").getValue() + ", " + bs.getBinding("asm2").getValue());
                        } else {
                            clientResults.add(bs.getBinding("client").getValue() + ", " + asmLib + ", " + bs.getBinding("dep1").getValue() + ", " + bs.getBinding("dep2").getValue() + ", " + bs.getBinding("asm2").getValue());
                        }
                    }
                }
                //break;
            }
        } catch (Exception e) {
            System.err.println("Exception: ");
        }

        //write set to file
        try {
            FileWriter fileWriter = new FileWriter(new File(outputLocation), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (String str : clientResults) {
                bufferedWriter.write(str + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void filterDirectClients(String fileLoc, String outputLocation, String dumpLocation) {
        File file = new File(fileLoc);
        String line = "";
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int count = 0;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replace("<", "");
                line = line.replace("http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#", "");
                line = line.replace(">", "");
                line = line.trim();
                String parts[] = line.split(",");
                String client = parts[0];
                String asm1 = parts[1];
                String dep = parts[2];
                String asm2 = parts[3];

                //ignore clients and transitive dependencies which are ASM libraries
                if (client.startsWith("asm:") || client.startsWith("org.ow2.asm:")) {
                    //count++;
                    continue;
                }
                if (dep.startsWith("asm:") || dep.startsWith("org.ow2.asm:")) {
                    // count++;
                    continue;
                }
                //ignore ASM sources.jar results
                String asm1jar = getJarLoc(asm1, "D:\\ICSE\\asm-libs\\");
                if (!new File(asm1jar).exists()) {
                    //count++;
                    continue;
                }
                String asm2jar = getJarLoc(asm2, "D:\\ICSE\\asm-libs\\");
                if (!new File(asm2jar).exists()) {
                    //count++;
                    continue;
                }
                //ignore clients and transitive dependencies without jar files
                String[] url = Utils.createUrl(client, "jar", ":");
                boolean result1 = Utils.getFileFromURL(url[0], dumpLocation + url[1]);
                url = Utils.createUrl(dep, "jar", ":");
                boolean result2 = Utils.getFileFromURL(url[0], dumpLocation + url[1]);
                if (result1 && result2) {
                    try {
                        FileWriter fileWriter = new FileWriter(new File(outputLocation), true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                        bufferedWriter.write(client + ", " + asm1 + ", " + dep + ", " + asm2 + "\n");

                        bufferedWriter.flush();
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    count++;
                    continue;
                }
            }
            System.out.println(count);
        } catch (Exception e) {
            System.err.println("Exception: " + line);
        }
    }

    public static void filterIndirectClients(String fileLoc, String outputLocation, String dumpLocation) {
        File file = new File(fileLoc);
        String line = "";
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int count = 0;
            TreeSet<String> deps = new TreeSet<>();
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                line = line.replace("<", "");
                line = line.replace("http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#", "");
                line = line.replace(">", "");
                line = line.trim();
                String parts[] = line.split(",");
                String client = parts[0].trim();
                String asm1 = parts[1].trim();
                String dep1 = parts[2].trim();
                // String dep2 = parts[3].trim();
                String asm2 = parts[3].trim();

                //ignore clients and transitive dependencies which are ASM libraries
                if (client.startsWith("asm:") || client.startsWith("org.ow2.asm:")) {
                    //count++;
                    continue;
                }
                if (dep1.startsWith("asm:") || dep1.startsWith("org.ow2.asm:")) {
                    // count++;
                    continue;
                }
//                if (dep2.startsWith("asm:") || dep2.startsWith("org.ow2.asm:")) {
//                    // count++;
//                    continue;
//                }
                //ignore ASM sources.jar results
                String asm1jar = getJarLoc(asm1, "D:\\ICSE\\jars\\");
                if (!new File(asm1jar).exists()) {
                    //count++;
                    continue;
                }
                String asm2jar = getJarLoc(asm2, "D:\\ICSE\\jars\\");
                if (!new File(asm2jar).exists()) {
                    //count++;
                    continue;
                }

                deps.add(dep1);
                // deps.add(dep2);
                //ignore clients and transitive dependencies without jar files
                // String[] url = Utils.createUrl(client, "jar", ":");
                //boolean result1 = Utils.getFileFromURL(url[0], dumpLocation + url[1]);
//                String[] url = Utils.createUrl(dep1, "jar", ":");
//                boolean result2 = Utils.getFileFromURL(url[0], dumpLocation + url[1],false);
//                url = Utils.createUrl(dep2, "jar", ":");
//                boolean result3 = Utils.getFileFromURL(url[0], dumpLocation + url[1],false);
                // if ( result2 && result3) {
//                    try {
//                        FileWriter fileWriter = new FileWriter(new File(outputLocation), true);
//                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//
//                        bufferedWriter.write(client + ", " + asm1 + ", " + dep1 + ", " + dep2 + ", " + asm2 + "\n");
//
//                        bufferedWriter.flush();
//                        bufferedWriter.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    count++;
//                    continue;
//                }
            }
            for (String dep : deps) {
                String[] url = Utils.createUrl(dep, "jar", ":");
                boolean result2 = Utils.getFileFromURL(url[0], dumpLocation + url[1], false);
            }
            //System.out.println(count);
        } catch (Exception e) {
            System.err.println("Exception: " + line);
        }
    }

    public static String getJarLoc(String gav, String baseLoc) {
        String jarFile = null;
        String[] jarURLs = Utils.createUrl(gav, "jar", ":");
        String fileSaveName = baseLoc + jarURLs[1].trim();
        return fileSaveName;
    }

    static File getJarLocal(String gav) {
        String[] gavparts = gav.split(":");
        String filePath = "D:\\ICSE\\New folder\\" + gavparts[1] + "-" + gavparts[2] + ".jar";
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }

    }

    public static void getDeprecationLibs(String destination) {
        String[] libs = new String[]{"commons-io:commons-io",
                "log4j:log4j",
                "org.slf4j:slf4j-api",
                "ch.qos.logback:logback-classic",
                "commons-lang:commons-lang",
                "commons-logging:commons-logging"};

        RemoteVirtuosoConnection remoteVirtuosoConnection = RemoteVirtuosoConnection.getInstance();
        Set<String> clientResults = new TreeSet<String>();
        try {
            for (String lib : libs) {

                String proj = "<http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#" + lib.trim() + ">";
                String query;
                TupleQueryResult resultSet;
                query = Queries.getProjectReleases(proj);

                resultSet = remoteVirtuosoConnection.executeQuery(query);

                while (resultSet.hasNext()) {
                    BindingSet bs = resultSet.next();
                    clientResults.add(bs.getBinding("release").getValue().stringValue());
                }

            }


            for (String release : clientResults) {
                String gav = Utils.shortenURI(release);
                String[] url = Utils.createUrl(gav, "jar", ":");
                boolean result2 = Utils.getFileFromURL(url[0], destination + url[1], false);
            }

        } catch (Exception e) {
            System.err.println("Exception: ");
        }
    }

    public static void getDeprecationLibClients(String jarDestination, String outputFile) {
        String[] libs = new String[]{"commons-io:commons-io",
                "log4j:log4j",
                "org.slf4j:slf4j-api",
                "ch.qos.logback:logback-classic",
                "commons-lang:commons-lang",
                "commons-logging:commons-logging"};

        RemoteVirtuosoConnection remoteVirtuosoConnection = RemoteVirtuosoConnection.getInstance();
        Set<String> libraryResults = new TreeSet<String>();
        Set<String> clientResults = new TreeSet<String>();
        try {
            for (String lib : libs) {

                String proj = "<http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#" + lib.trim() + ">";
                String query;
                TupleQueryResult resultSet;
                query = Queries.getProjectReleases(proj);

                resultSet = remoteVirtuosoConnection.executeQuery(query);

                while (resultSet.hasNext()) {
                    BindingSet bs = resultSet.next();
                    libraryResults.add(bs.getBinding("release").getValue().stringValue());
                }

            }

            for (String release : libraryResults) {
                String query;
                TupleQueryResult resultSet;
                query = Queries.getASMClients("<"+release+">");
               // System.out.println(query);
                resultSet = remoteVirtuosoConnection.executeQuery(query);

                while (resultSet.hasNext()) {
                    BindingSet bs = resultSet.next();
                    String dep = bs.getBinding("client").getValue().stringValue();

                    String gav = Utils.shortenURI(dep);
                    String[] url = Utils.createUrl(gav, "jar", ":");
                    boolean result2 = Utils.getFileFromURL(url[0], jarDestination + url[1], false);

                    if (result2) {
                        Experiment.log(outputFile, release + ", " + dep, true);
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception: ");
        }
    }

}
