package ca.concordia.cs.aseg.sbson.ontologies.triplestore;

import java.util.List;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.ResultSet;

public interface TripleConstructor {

	void constructFromRule(Object graph, String rule, boolean recurse);
	
	Object getInferredTriplesFromRule(Object graph, String rule);

	void materializeInferredTriples(Object graph, List<Triple> results);

	void materializeInferredTriples(Object graph, ResultSet results, boolean isLiteralObject);
}
