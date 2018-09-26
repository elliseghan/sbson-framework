package ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso;

import ca.concordia.cs.aseg.sbson.ontologies.triplestore.TriplestoreConnection;
import virtuoso.jena.driver.VirtGraph;

public class LocalVirtuosoConnection implements TriplestoreConnection {

    private static LocalVirtuosoConnection connection = null;
    private static Object graph = null;

    private LocalVirtuosoConnection() {

    }

    public static LocalVirtuosoConnection getInstance() {
        if (connection == null)
            connection = new LocalVirtuosoConnection();
        return connection;
    }

    public Object connectToStore(String url, String graphUri, String uname, String pwd) {
        if (graph != null && ((VirtGraph) graph).getGraphUrl().equals(url)
                && ((VirtGraph) graph).getGraphUser().equals(uname))
            return graph;
        if (graphUri != null) {
            graph = new VirtGraph(graphUri, url, uname, pwd);
        } else {
            graph = new VirtGraph(url, uname, pwd);
        }
        return graph;
    }

    public Object connectToStore(String url, String uname, String pwd) {
        return connectToStore(url, null, uname, pwd);
    }

}
