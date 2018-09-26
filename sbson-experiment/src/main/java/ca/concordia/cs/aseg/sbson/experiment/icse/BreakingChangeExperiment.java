package ca.concordia.cs.aseg.sbson.experiment.icse;

import ca.concordia.cs.aseg.sbson.core.Utils;
import ca.concordia.cs.aseg.sbson.ontologies.publisher.code.JavaByteCodePublisher;
import ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso.VirtuosoTripleConstructor;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import virtuoso.jena.driver.VirtGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BreakingChangeExperiment extends Experiment {

    List<String> asmClassAPIs = new ArrayList<>();
    List<String> asmMethodAPIs = new ArrayList<>();

    public static void main(String[] args) {
        BreakingChangeExperiment breakingChangeExperiment = new BreakingChangeExperiment();
        //breakingChangeExperiment.setup();
        breakingChangeExperiment.usageExperiment1("");
        System.exit(0);
    }

    public BreakingChangeExperiment() {
        asmClassAPIs.add("org.objectweb.asm.AnnotationVisitor");
        asmClassAPIs.add("org.objectweb.asm.FieldVisitor");
        asmClassAPIs.add("org.objectweb.asm.ClassVisitor");
        asmClassAPIs.add("org.objectweb.asm.MethodVisitor");
        asmClassAPIs.add("org.objectweb.asm.signature.SignatureVisitor");
        asmClassAPIs.add("org.objectweb.asm.tree.analysis.Interpreter");
    }

    private Map<String, Set<String>> parseInputFile(String inputFileLocation) throws IOException {
        Map<String, Set<String>> map = new HashMap<>();
        FileReader fileReader = new FileReader(inputFileLocation);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = "";
        bufferedReader.readLine();//read out headers
        while ((line = bufferedReader.readLine()) != null) {
            line = line.replaceAll("\"", "");
            String[] lineParts = line.split(",");
            String asm = Utils.shortenURI(lineParts[0].trim());
            String dependent = Utils.shortenURI(lineParts[1].trim());

            Set<String> dependents = new TreeSet<>();
            if (map.containsKey(asm)) {
                dependents = map.get(asm);
            }
            dependents.add(dependent);
            map.put(asm, dependents);
        }
        return map;
    }

    private void generateTriples(String inputFileLocation, String jarStoreLocation) {
        try {
            Map<String, Set<String>> map = parseInputFile(inputFileLocation);
            for (String asm : map.keySet()) {
                //if jar already exists, generate triples without dependent info
                String fileLocation = getJarLoc(asm, jarStoreLocation);
                File asmFile = new File(fileLocation);

                if (asmFile.exists()) {
                    System.out.println(asm);
                    new JavaByteCodePublisher().publish(asmFile, asm, null, 1, "out.nt");

                    Set<String> dependents = map.get(asm);
                    for (String dependent : dependents) {
                        // System.out.println(dependent);
                        //if jar already exists, generate triples using the asm lib as dependent
                        fileLocation = getJarLoc(dependent, jarStoreLocation);
                        File dependentFile = new File(fileLocation);

                        if (dependentFile.exists()) {

                            List<String> asmUsed = new ArrayList<>();
                            asmUsed.add(asm);
                            new JavaByteCodePublisher().publish(dependentFile, dependent, asmUsed, 0, "out.nt");
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        Utils.initProperties("D:\\IdeaProjects\\sbsonframework\\sbson-core\\src\\main\\resources\\sbson-core.properties");
        generateTriples("C:\\Users\\elliseghan\\Downloads\\new-asm-info", Utils.JAR_DUMP_LOCATION);
        generateTriples("C:\\Users\\elliseghan\\Downloads\\old-asm-info", Utils.JAR_DUMP_LOCATION);
    }

    public void usageExperiment1(String outputLocation) {
        /*
            1. get all ASM 4+ clients
            2. get all ASM 3- clients
            2. run experiment for each individual pair -> check if there is any possible usage of conflicting APIs
         */
        List<String> asm4Clients = getASMClients(4);
        List<String> asm3Clients = getASMClients(3);
        Set<String> asm4ClientEntities = getUniqueCLassEntites(asm4Clients);
        Set<String> asm3ClientEntities = getUniqueCLassEntites(asm3Clients);
        System.out.println("ASM 4: "+asm4ClientEntities.size());
        System.out.println("ASM 3: "+asm3ClientEntities.size());
        System.out.println("");

        for (String asm4ClientEntity : asm4ClientEntities) {
           /* String[] asm4ClientParts = asm4Client.split(",");
            String asm4ClientProject = asm4ClientParts[0].trim();
            String asm4ClientEntity = asm4ClientParts[1].trim();
            String asm4ClientMethod = asm4ClientParts[2].trim();*/
            for (String asm3ClientEntity : asm3ClientEntities) {
               /* String[] asm3ClientParts = asm3Client.split(",");
                String asm3ClientProject = asm3ClientParts[0].trim();
                String asm3ClientEntity = asm3ClientParts[1].trim();
                String asm3ClientMethod = asm3ClientParts[2].trim();*/

                if(asm4ClientEntity.equals(asm3ClientEntity)){
                    continue;
                }
               // System.out.println(asm4ClientEntity +", "+ asm3ClientEntity);
                String query = Queries.checkUsage2("<" + asm4ClientEntity + ">", "<" + asm3ClientEntity + ">");
                try {
                    ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso.LocalVirtuosoConnection virtuosoConnection = ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso.LocalVirtuosoConnection.getInstance();
                    VirtGraph graph = (VirtGraph) virtuosoConnection.connectToStore("jdbc:virtuoso://localhost:1111", "dba", "dba");
                    ResultSet resultSet;
                    VirtuosoTripleConstructor tripleConstructor = new VirtuosoTripleConstructor();
                    resultSet = (ResultSet) tripleConstructor.getInferredTriplesFromRule(graph, query);

                    while (resultSet.hasNext()) {
                        QuerySolution bs = resultSet.next();
                        //String someMethod = bs.get("someMethod").toString();
                        String someClientEntity = bs.get("someClientEntity").toString();
                        System.out.println(someClientEntity + " ==> " + asm4ClientEntity + " ==> " + asm3ClientEntity);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

   /* public void usageExperiment2(String outputLocation) {
        *//*
            1. get internal ASM APIs using the initial list of classes
            1. get all ASM 4+ clients
            2. get all ASM 3- clients
            2. run experiment for each individual pair -> check if there is any possible usage of conflicting APIs
         *//*
        List<String> asm4Clients = getASMClients(4);
        List<String> asm3Clients = getASMClients(3);
        System.out.println("ASM 4: "+asm4Clients.size());
        System.out.println("ASM 3: "+asm3Clients.size());
        System.out.println("");

        for (String asm4Client : asm4Clients) {
            String[] asm4ClientParts = asm4Client.split(",");
            String asm4ClientProject = asm4ClientParts[0].trim();
            String asm4ClientEntity = asm4ClientParts[1].trim();
            String asm4ClientMethod = asm4ClientParts[2].trim();
            for (String asm3Client : asm3Clients) {
                String[] asm3ClientParts = asm3Client.split(",");
                String asm3ClientProject = asm3ClientParts[0].trim();
                String asm3ClientEntity = asm3ClientParts[1].trim();
                String asm3ClientMethod = asm3ClientParts[2].trim();

                if(asm4ClientProject.equals(asm3ClientProject)){
                    continue;
                }
                String query = Queries.checkUsage("<" + asm4ClientProject + ">","<" + asm4ClientEntity + ">", "<" + asm4ClientMethod + ">", "<" + asm3ClientMethod + ">");
                try {
                    RemoteVirtuosoConnection remoteVirtuosoConnection = RemoteVirtuosoConnection.getInstance();
                    RepositoryConnection repositoryConnection = (RepositoryConnection) remoteVirtuosoConnection.connectToStore("http://localhost:8890/sparql", "dba", "dba");
                    TupleQueryResult resultSet;
                    resultSet = remoteVirtuosoConnection.executeQuery(repositoryConnection, query);

                    while (resultSet.hasNext()) {
                        BindingSet bs = resultSet.next();
                        String someMethod = bs.getBinding("someMethod").getValue().stringValue();
                        String someClientEntity = bs.getBinding("someClientEntity").getValue().stringValue();
                        System.out.println(someMethod + " ==> " + asm4ClientMethod + " ==> " + asm3ClientMethod);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
*/
    public List<String> getASMClients(int mode) {
        List<String> asmClients = new ArrayList<>();
        try {
            for (String api : asmClassAPIs) {
                String query;
                if (mode == 4) {
                    query = Queries.getClientsASM4AndAbove(api);
                } else {
                    query = Queries.getClientsASM3AndBelow(api);
                }
                // System.out.println(query);
                ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso.LocalVirtuosoConnection virtuosoConnection = ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso.LocalVirtuosoConnection.getInstance();
                VirtGraph graph = (VirtGraph) virtuosoConnection.connectToStore("jdbc:virtuoso://localhost:1111", "dba", "dba");
                ResultSet resultSet;
                VirtuosoTripleConstructor tripleConstructor = new VirtuosoTripleConstructor();
                resultSet = (ResultSet) tripleConstructor.getInferredTriplesFromRule(graph, query);
                while (resultSet.hasNext()) {
                    QuerySolution bs = resultSet.next();
                    String client = bs.get("client").toString();
                    String clientMethod = bs.get("clientMethod").toString();
                    String clientEntity = bs.get("clientEntity").toString();
                    if(client.startsWith("asm:") || client.startsWith("org.ow2.asm")){
                        continue;
                    }
                    asmClients.add(client + ", " + clientEntity + ", " + clientMethod);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return asmClients;
    }

    private Set<String> getUniqueCLassEntites(List<String> input){
        Set<String> set = new TreeSet<>();
        for(String str: input){
            String[] clientParts = str.split(",");
            String clientProject = clientParts[0].trim();
            String clientEntity = clientParts[1].trim();
            String clientMethod = clientParts[2].trim();
            set.add(clientEntity);
        }
        return set;
    }


}
