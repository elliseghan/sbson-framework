package ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;


import ca.concordia.cs.aseg.sbson.ontologies.triplestore.TripleLoader;
import org.openrdf.repository.RepositoryConnection;
import virtuoso.jena.driver.VirtGraph;

public class VirtousoTripleLoader extends TripleLoader {
	/*public VirtousoTripleLoader() {

	}*/

    public VirtousoTripleLoader(String defaultGraph, String defaultRuleset) {
        this.defaultGraph = defaultGraph;
        this.defaultRuleset = defaultRuleset;
    }

    public VirtousoTripleLoader(String defaultGraph, String schemaGraph, String dataGraph, String ruleset) {
        this.defaultGraph = defaultGraph;
        this.defaultRuleset = ruleset;
        this.setGraphMapping(defaultGraph, schemaGraph, dataGraph, ruleset);
    }


    @Override
    public void loadSchema(Object connectionGraph, String source, String schemaGraphURI, String rulesetURI, boolean extractRules) {
        File schemaSource = new File(source);
        String dirpath = "";
        if (schemaSource.exists()) {
            File[] files = null;
            if (schemaSource.isDirectory()) {
                files = schemaSource.listFiles();
                dirpath=source;
            } else {
                files = new File[]{schemaSource};
                dirpath=source.substring(0, source.lastIndexOf("/") + 1);
            }
            try {
                String staRDFXML = "ld_dir (?, ?, ?)";
                PreparedStatement querySQLRDFXML = ((VirtGraph) connectionGraph).getConnection().prepareStatement(staRDFXML);
                long start = System.currentTimeMillis();
                System.out
                        .println("\nStart uploading ontology schema into memory ...");
                for (int f = 0; f < files.length; f++) {
                    File file = files[f];
                    if (file.getName().endsWith(".owl") || file.getName().endsWith(".rdf")) {
                        try {
                            // Creating the Query

                            querySQLRDFXML.setString(1, dirpath);
                            querySQLRDFXML.setString(2, file.getName());
                            querySQLRDFXML.setString(3, schemaGraphURI);

                            // Query execution
                            querySQLRDFXML.executeQuery();
                            System.out.println("Done: " + file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


                if (extractRules) {
                    String inferenceRule = "rdfs_rule_set (?,?)";
                    PreparedStatement queryInferenceRules = ((VirtGraph) connectionGraph).getConnection().prepareStatement(inferenceRule);
                    System.out.println("\nPerforming extraction of rules from schema . . .");

                    queryInferenceRules.setString(1, rulesetURI);
                    queryInferenceRules.setString(2, schemaGraphURI);
                    queryInferenceRules.executeQuery();
                }


                System.out.println("Elapsed time: "
                        + (System.currentTimeMillis() - start) / 1000.00
                        + " seconds.");

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void loadData(Object connectionGraph, String source, String dataGraphURI) {
        File dataSource = new File(source);
        if (dataSource.exists()) {
            String[] files = null;
            if (dataSource.isDirectory()) {
                files = dataSource.list();
            } else {
                files = new String[]{dataSource.getAbsolutePath()};
            }
            try {
                String staRDFXML = "ld_dir (?, ?, ?)";
                PreparedStatement querySQLRDFXML = ((VirtGraph) connectionGraph).getConnection().prepareStatement(staRDFXML);
                long start = System.currentTimeMillis();
                System.out
                        .println("\nStart uploading ontology schema into memory ...");
                for (int f = 0; f < files.length; f++) {
                    String file = files[f];
                    if (file.endsWith(".nt") || file.endsWith(".ttl")) {
                        try {
                            // Creating the Query
                            String dirpath = file.substring(0, file.lastIndexOf("/") + 1);
                            querySQLRDFXML.setString(1, dirpath);
                            querySQLRDFXML.setString(2, file);
                            querySQLRDFXML.setString(3, dataGraphURI);

                            // Query execution
                            querySQLRDFXML.executeQuery();
                            System.out.println("Done: " + file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("Elapsed time: "
                        + (System.currentTimeMillis() - start) / 1000.00
                        + " seconds.");

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void loadSchemaAndData(Object connectionGraph, String schemaSource, String dataSource, String schemaGraphURI, String dataGraphURI, String rulesetURI, boolean extractRules) {
        loadSchema(connectionGraph, schemaSource, schemaGraphURI, rulesetURI, extractRules);
        loadData(connectionGraph, dataSource, dataGraphURI);
    }

    @Override
    public void flushMemory(Object connectionGraph) {
        String bulkRun = "rdf_loader_run()";
        PreparedStatement querySQLBulkRun = null;
        long start = System.currentTimeMillis();
        try {
            querySQLBulkRun = ((VirtGraph) connectionGraph).getConnection().prepareStatement(bulkRun);
            System.out.println("\nPerforming the bulk load of all data . . .");
            querySQLBulkRun.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start) / 1000.00 + " seconds.");
        System.out.println("Done.");
    }

    public static void main(String[] args) {
        VirtousoTripleLoader virtousoTripleLoader = new VirtousoTripleLoader("http://asm-default.com", "http://asm-experiments.com", "http://asm-experiments.com", "asm-rules");
        LocalVirtuosoConnection localVirtuosoConnection = LocalVirtuosoConnection.getInstance();
        VirtGraph graph = (VirtGraph)  localVirtuosoConnection.connectToStore("jdbc:virtuoso://localhost:1111", "dba", "dba");
        virtousoTripleLoader.loadSchema(graph, "D:\\Openlink Software\\VOS7\\virtuoso-opensource\\database\\dataset\\schema\\", true);
        virtousoTripleLoader.flushMemory(graph);

        System.exit(0);
    }
}