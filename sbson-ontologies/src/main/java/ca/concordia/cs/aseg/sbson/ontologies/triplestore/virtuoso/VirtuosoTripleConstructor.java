package ca.concordia.cs.aseg.sbson.ontologies.triplestore.virtuoso;

import java.util.List;

import ca.concordia.cs.aseg.sbson.ontologies.triplestore.TripleConstructor;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class VirtuosoTripleConstructor implements TripleConstructor {
	public void constructFromRule(Object graph, String rule, boolean recurse) {
		System.out.println(rule);
		int before = 0, after = 1;
		while ((after - before > 0)) {
			before = ((VirtGraph) graph).getCount();
			System.out.println("graph.getCount() before = " + before);
			System.out.println("materializing triples to the graph().");
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(rule, (VirtGraph) graph);
			ResultSet results = vqe.execSelect();
			materializeInferredTriples((VirtGraph) graph, results, false);
			after = ((VirtGraph) graph).getCount();
			System.out.println("graph.getCount() after = " + after);

			if (!recurse) {
				after = before;
			}
		}
	}

	public void materializeInferredTriples(Object graph, List<Triple> results) {
		for (Triple triple : results) {
			((VirtGraph) graph).add(triple);
		}
	}

	public void materializeInferredTriples(Object graph, ResultSet results, boolean isLiteralObject) {
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode s = result.get("S");
			RDFNode p = result.get("P");
			RDFNode o = result.get("O");
			Node S = Node.createURI(s.toString());
			Node P = Node.createURI(p.toString());
			Node O;
			if (isLiteralObject)
				O = Node.createLiteral(o.toString());
			else
				O = Node.createURI(o.toString());
			((VirtGraph) graph).add(new Triple(S, P, O));
		}
	}

	@Override
	public Object getInferredTriplesFromRule(Object graph, String rule) {
		Object results = null;
		if (graph instanceof VirtGraph) {
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(rule, (VirtGraph) graph);
			results = vqe.execSelect();
		} else if (graph instanceof RepositoryConnection) {
			TupleQuery q;
			try {
				q = ((RepositoryConnection) graph).prepareTupleQuery(QueryLanguage.SPARQL, rule);
				results = q.evaluate();
			} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
	public boolean getResultFromASK(Object graph, String rule) {
		boolean results = false;
		if (graph instanceof VirtGraph) {
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(rule, (VirtGraph) graph);
			results = vqe.execAsk();
		} else if (graph instanceof RepositoryConnection) {
			BooleanQuery q;
			try {
				q = ((RepositoryConnection) graph).prepareBooleanQuery(QueryLanguage.SPARQL, rule);
				
				results = q.evaluate();
			} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
}
