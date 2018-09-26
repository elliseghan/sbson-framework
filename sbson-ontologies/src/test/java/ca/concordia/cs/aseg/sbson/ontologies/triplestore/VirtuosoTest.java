package ca.concordia.cs.aseg.sbson.ontologies.triplestore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso.LocalVirtuosoConnection;
import ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso.RemoteVirtuosoConnection;
import ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso.VirtuosoTripleConstructor;
import org.junit.Test;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.hp.hpl.jena.query.ResultSet;


import virtuoso.jena.driver.VirtGraph;

public class VirtuosoTest {

	@Test
	public void testLocalVirtuosoConnection() {
		String url = "jdbc:virtuoso://localhost:1111";
		String uname = "dba";
		String pwd = "dba";
		LocalVirtuosoConnection connection = LocalVirtuosoConnection.getInstance();
		VirtGraph graph = (VirtGraph) connection.connectToStore(url, uname, pwd);
		assertEquals(uname, graph.getGraphUser());
	}

	@Test
	public void testRemoteVirtuosoConnection() {
		//dbpedia.org
		String url = "http://aseg.encs.concordia.ca/virtuoso/sparql";
		String uname = "dba";
		String pwd = "dba";
		//String graphUri = "http://experiment2-schema.com";
		RemoteVirtuosoConnection connection = RemoteVirtuosoConnection.getInstance();
		RepositoryConnection graph = (RepositoryConnection) connection.connectToStore(url, uname, pwd);
		try {
			assertEquals(true, graph.isOpen());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testExistingLocalVirtuosoConnection() {
		String url = "jdbc:virtuoso://localhost:1111";
		String uname = "dba";
		String pwd = "dba";
		LocalVirtuosoConnection connection = LocalVirtuosoConnection.getInstance();
		VirtGraph graph = (VirtGraph) connection.connectToStore(url, uname, pwd);
		
		LocalVirtuosoConnection connection2 = LocalVirtuosoConnection.getInstance();
		VirtGraph graph2 = (VirtGraph) connection2.connectToStore(url, uname, pwd);
		assertEquals(uname, graph2.getGraphUser());
	}

	@Test
	public void testExistingRemoteVirtuosoConnection() {
		//dbpedia.org
		String url = "http://aseg.encs.concordia.ca/virtuoso/sparql";
		String uname = "dba";
		String pwd = "dba";
		//String graphUri = "http://experiment2-schema.com";
		RemoteVirtuosoConnection connection = RemoteVirtuosoConnection.getInstance();
		RepositoryConnection graph = (RepositoryConnection) connection.connectToStore(url, uname, pwd);
		
		RemoteVirtuosoConnection connection2 = RemoteVirtuosoConnection.getInstance();
		RepositoryConnection graph2 = (RepositoryConnection) connection2.connectToStore(url, uname, pwd);
		
		try {
			assertEquals(true, graph2.isOpen());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testVirtuosoTripleLoaderFromFile1() {
		fail("Not yet implemented");
	}

	@Test
	public void testVirtuosoTripleLoaderFromFile2() {
		fail("Not yet implemented");
	}

	@Test
	public void testVirtuosoTripleLoaderFromDirectory1() {
		fail("Not yet implemented");
	}

	@Test
	public void testVirtuosoTripleLoaderFromDirectory2() {
		fail("Not yet implemented");
	}

	@Test
	public void testVirtuosoTripleLoaderFromDirectory3() {
		fail("Not yet implemented");
	}

	@Test
	public void testVirtuosoMaterialization1() {
		fail("Not yet implemented");
	}

	@Test
	public void testVirtuosoMaterialization2() {
		fail("Not yet implemented");
	}

	@Test
	public void testVirtuosoLocalInference() {
		String url = "jdbc:virtuoso://localhost:1111";
		String uname = "dba";
		String pwd = "dba";
		String graphUri = "http://experiment-data.com";
		LocalVirtuosoConnection connection = LocalVirtuosoConnection.getInstance();
		VirtGraph graph = (VirtGraph) connection.connectToStore(url,graphUri, uname, pwd);
		VirtuosoTripleConstructor tripleConstructor = new VirtuosoTripleConstructor();
		String query="select * from <"+graphUri+"> where {?s ?p ?o.} LIMIT 10";
		ResultSet resultSet = (ResultSet) tripleConstructor.getInferredTriplesFromRule(graph, query);
		int count=0;
		while(resultSet.hasNext()){
			resultSet.nextSolution();
			count=count+1;
		}
		assertEquals(9, count);
	}

	@Test
	public void testVirtuosoRemoteInference() {
		String url = "http://aseg.encs.concordia.ca/virtuoso/sparql";
		String uname = "dba";
		String pwd = "dba";
		String graphUri = "http://build-data.com";
		RemoteVirtuosoConnection connection = RemoteVirtuosoConnection.getInstance();
		RepositoryConnection graph = (RepositoryConnection) connection.connectToStore(url,graphUri, uname, pwd);
		VirtuosoTripleConstructor tripleConstructor = new VirtuosoTripleConstructor();
		String query="select * from <"+graphUri+"> where {?s ?p ?o.} LIMIT 10";
		TupleQueryResult resultSet = (TupleQueryResult) tripleConstructor.getInferredTriplesFromRule(graph, query);
		int count=0;
		try {
			while (resultSet.hasNext()) {
				BindingSet bs =resultSet.next();
				count=count+1;
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		assertEquals(10, count);
	}
	
	@Test
	public void testVirtuosoTripleConstruction() {
		fail("Not yet implemented");
	}

}
