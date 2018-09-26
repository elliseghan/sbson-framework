package ca.concordia.cs.aseg.sbson.ontologies.triplestore;

public interface TriplestoreConnection {

	Object connectToStore(String url, String graphUri, String uname, String pwd);
	
	Object connectToStore(String url, String uname, String pwd);
}
