package ca.concordia.cs.aseg.sbson.ontologies.urigenerator.system_specific.tbox;

import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.namespace.NamespaceFactory;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.NamespaceRegistry;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.OntologyRegistry;

public class Issues_jiraTBox {
	/**
	 * CONCEPTS
	 */
	public static String Question() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Question";
		return uri;
	}
	
	public static String Task() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Task";
		return uri;
	}
	
	public static String Test() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Test";
		return uri;
	}
	
	public static String Wish() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Wish";
		return uri;
	}
	
	/**
	 * OPBJECT PROPERTIES
	 */
	
	
	/**
	 * DATA PROPERTIES
	 */
	public static String hasKey() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasKey";
		return uri;
	}
	
	/**
	 * INDIVIDUALS
	 */
	public static String blocker() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "blocker";
		return uri;
	}
	
	public static String cannotreproduce() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "cannotreproduce";
		return uri;
	}
	
	public static String closed() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "closed";
		return uri;
	}
	
	public static String critical() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "critical";
		return uri;
	}
	
	public static String duplicate() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "duplicate";
		return uri;
	}
	
	public static String fixed() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "fixed";
		return uri;
	}
	
	public static String incomplete() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "incomplete";
		return uri;
	}
	
	public static String major() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "major";
		return uri;
	}
	
	public static String minor() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "minor";
		return uri;
	}
	
	public static String open() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "open";
		return uri;
	}
	
	public static String reopened() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "reopened";
		return uri;
	}
	
	public static String resolved() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "resolved";
		return uri;
	}
	
	public static String trivial() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "trivial";
		return uri;
	}
	
	public static String wontfix() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "wontfix";
		return uri;
	}
}
