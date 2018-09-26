package ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso;

import ca.concordia.cs.aseg.sbson.ontologies.triplestore.TriplestoreConnection;
import com.fluidops.fedx.Config;
import com.fluidops.fedx.FedXFactory;
import com.fluidops.fedx.exception.FedXRuntimeException;
import org.openrdf.repository.Repository;

import java.util.Arrays;



public class RemoteVirtuosoConnection implements TriplestoreConnection {

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
			System.err.println("FEDX: "+e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return graph;
	}

	public Object connectToStore(String url, String uname, String pwd) {
		return connectToStore(url, null, uname, pwd);
	}

}
