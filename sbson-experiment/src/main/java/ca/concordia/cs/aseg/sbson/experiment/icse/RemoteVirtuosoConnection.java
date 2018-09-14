package ca.concordia.cs.aseg.sbson.experiment.icse;

import java.util.Arrays;

import org.openrdf.query.*;
import org.openrdf.repository.Repository;

import com.fluidops.fedx.Config;
import com.fluidops.fedx.FedXFactory;
import com.fluidops.fedx.exception.FedXRuntimeException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;


public class RemoteVirtuosoConnection {

    /*
     * This class provides the capability to connect to a remote Virtuoso Server through a provided SPARQL endpoint
     * The url format should be of the form http://url-to-endpoint (e.g. http://aseg.encs.concordia.ca/virtuoso/sparql)
     */

    private static RemoteVirtuosoConnection connection = null;
    private static Object graph = null;

    private RemoteVirtuosoConnection() {

    }

    public static RemoteVirtuosoConnection getInstance() {
        if (connection == null)
            connection = new RemoteVirtuosoConnection();
        return connection;
    }

    public Object connectToStore(String url, String graphUri, String uname, String pwd) {
        Repository repo = null;
        try {
            Config.initialize();
            repo = FedXFactory.initializeSparqlFederation(Arrays.asList(url));
            System.out.println("FEDX REPO: " + repo.getConnection().isOpen());
            graph = repo.getConnection();
        } catch (FedXRuntimeException e) {
           // System.err.println("FEDX: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return graph;
    }

    public Object connectToStore(String url, String uname, String pwd) {
        return connectToStore(url, null, uname, pwd);
    }

    public TupleQueryResult executeQuery(String query) throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        String url = "http://aseg.encs.concordia.ca/virtuoso/sparql";
        String uname = "dba";
        String pwd = "dba";
        RemoteVirtuosoConnection connection = RemoteVirtuosoConnection.getInstance();
        RepositoryConnection graph = (RepositoryConnection) connection.connectToStore(url, uname, pwd);
        TupleQuery q = ((RepositoryConnection) graph).prepareTupleQuery(QueryLanguage.SPARQL, query);
        TupleQueryResult resultSet = q.evaluate();
        return resultSet;

    }

}
