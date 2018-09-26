package ca.concordia.cs.aseg.sbson.ontologies.triplestore;

import java.util.HashMap;
import java.util.Map;

public abstract class TripleLoader {

    protected String defaultGraph = "";
    protected String defaultRuleset = "";
    protected Map<graphType, String> graphMapping = null;

    public enum graphType {
        DEFAULT, SCHEMA, DATA, RULESET
    }

    public abstract void loadSchema(Object connectionGraph, String source, String schemaGraphURI, String rulesetURI, boolean extractRules);

    public void loadSchema(Object connectionGraph, String source, Map<graphType, String> graphMapping, boolean extractRules) {
        loadSchema(connectionGraph, source, graphMapping.get(graphType.SCHEMA), graphMapping.get(graphType.RULESET), extractRules);
    }

    public void loadSchema(Object connectionGraph, String source, boolean extractRules) {
        if (this.graphMapping == null) {
            if (defaultGraph.isEmpty()) {
                System.err.println("You have to set your default graph URI first!");
                return;
            }
            if (defaultRuleset.isEmpty()) {
                System.err.println("You have to set the name of your default ruleset first!");
                return;
            }
            loadSchema(connectionGraph, source, defaultGraph, defaultRuleset, extractRules);
        } else {
            loadSchema(connectionGraph, source, this.graphMapping, extractRules);
        }
    }

    public abstract void loadData(Object connectionGraph, String source, String dataGraphURI);

    public void loadData(Object connectionGraph, String source, Map<graphType, String> graphMapping) {
        loadData(connectionGraph, source, graphMapping.get(graphType.DATA));
    }

    public void loadData(Object connectionGraph, String source) {
        if (this.graphMapping == null) {
            if (defaultGraph.isEmpty()) {
                System.err.println("You have to set your default graph URI first!");
                return;
            }
            loadData(connectionGraph, source, defaultGraph);
        } else {
            loadData(connectionGraph, source, this.graphMapping);
        }
    }

    public abstract void loadSchemaAndData(Object connectionGraph, String schemaSource,String dataSource, String schemaGraphURI, String dataGraphURI, String rulesetURI, boolean extractRules);

    public void loadSchemaAndData(Object connectionGraph, String schemaSource,String dataSource, Map<graphType, String> graphMapping, boolean extractRules) {
        loadSchemaAndData(connectionGraph, schemaSource,dataSource,graphMapping.get(graphType.SCHEMA), graphMapping.get(graphType.DATA), graphMapping.get(graphType.RULESET),extractRules);
    }

    public void loadSchemaAndData(Object connectionGraph, String schemaSource,String dataSource, boolean extractRules) {
        if (this.graphMapping == null) {
            if (defaultGraph.isEmpty()) {
                System.err.println("You have to set your default graph URI first!");
                return;
            }
            if (defaultRuleset.isEmpty()) {
                System.err.println("You have to set the name of your default ruleset first!");
                return;
            }
            loadSchemaAndData(connectionGraph, schemaSource,dataSource, defaultGraph, defaultGraph, defaultRuleset, extractRules);
        } else {
            loadSchemaAndData(connectionGraph, schemaSource,dataSource, this.graphMapping, extractRules);
        }
    }

    public abstract void flushMemory(Object connectionGraph);

    public String getDefaultGraphURI() {
        return defaultGraph;
    }

    public void setDefaultGraphURI(String defaultGraph) {
        this.defaultGraph = defaultGraph;
    }

    public String getDefaultRuleset() {
        return defaultRuleset;
    }

    public void setDefaultRuleset(String defaultRuleset) {
        this.defaultRuleset = defaultRuleset;
    }

    public Map<graphType, String> getGraphMapping() {
        return graphMapping;
    }

    public void setGraphMapping(String defaultGraph, String schemaGraph, String dataGraph, String ruleset) {
        this.graphMapping = new HashMap<>();
        graphMapping.put(graphType.DEFAULT, defaultGraph);
        graphMapping.put(graphType.SCHEMA, schemaGraph);
        graphMapping.put(graphType.DATA, dataGraph);
        graphMapping.put(graphType.RULESET, ruleset);
    }

}
