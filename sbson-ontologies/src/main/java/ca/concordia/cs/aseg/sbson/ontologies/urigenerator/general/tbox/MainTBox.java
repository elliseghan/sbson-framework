/*
 * Created by ASEG at Concordia University.
 * http://aseg.cs.concordia.ca
 * http://aseg.cs.concordia.ca/segps
 * Please see the LICENSE file for details.
 */

package ca.concordia.cs.aseg.sbson.ontologies.urigenerator.general.tbox;

import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.namespace.NamespaceFactory;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.NamespaceRegistry;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.OntologyRegistry;

public class MainTBox {

	// Classes
	public static String Product() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "Product";
		return uri;
	}

	public static String Release() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "Release";
		return uri;
	}

	public static String Artifact() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "Artifact";
		return uri;
	}

	public static String Organization() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "Organization";
		return uri;
	}

	public static String Developer() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "Developer";
		return uri;
	}

	public static String File() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "file";
		return uri;
	}

	public static String Directory() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "directory";
		return uri;
	}

	public static String Milestone() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "milestone";
		return uri;
	}

	public static String Activity() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "activity";
		return uri;
	}

	public static String Stakeholder() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "stakeholder";
		return uri;
	}
	
	public static String License() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "license";
		return uri;
	}

	public static String SeonThing() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "seonThing";
		return uri;
	}

	// PROPERTIES
	/*
	 *      
	 *     
	 *     
	 */
	public static String belongsToOrganization() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "belongsToOrganization";
		return uri;
	}

	public static String belongsToRelease() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "belongsToRelease";
		return uri;
	}

	public static String carriesOutActivity() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "carriesOutActivity";
		return uri;
	}

	public static String containsFile() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "containsFile";
		return uri;
	}

	public static String dependsOn() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "dependsOn";
		return uri;
	}

	public static String hasAuthor() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasAuthor";
		return uri;
	}

	public static String hasChild() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasChild";
		return uri;
	}

	public static String hasMilestone() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasMilestone";
		return uri;
	}

	public static String hasParent() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasParent";
		return uri;
	}

	public static String hasRelease() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasRelease";
		return uri;
	}

	public static String hasSibling() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasSibling";
		return uri;
	}

	public static String isBasedOn() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "isBasedOn";
		return uri;
	}

	public static String isCarriedOutBy() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "isCarriedOutBy";
		return uri;
	}

	public static String isReleaseOf() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "isReleaseOf";
		return uri;
	}

	public static String isSimilar() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "isSimilar";
		return uri;
	}

	public static String hasLicense() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasLicense";
		return uri;
	}
	
	/*
	 * DATA PRPERTIES
	 */
	public static String activityEnd() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "activityEnd";
		return uri;
	}

	public static String activityStart() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "activityStart";
		return uri;
	}

	public static String hasCreationDate() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasCreationDate";
		return uri;
	}

	public static String hasDescription() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasDescription";
		return uri;
	}

	public static String hasEmail() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasEmail";
		return uri;
	}

	public static String hasIdentifier() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasIdentifier";
		return uri;
	}

	public static String hasModificationDate() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasModificationDate";
		return uri;
	}

	public static String hasName() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasName";
		return uri;
	}
	
	public static String hasURL() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasURL";
		return uri;
	}

	public static String hasPath() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasPath";
		return uri;
	}

	public static String hasReleaseDate() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasReleaseDate";
		return uri;
	}

	public static String hasTargetDate() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_main)
				+ "hasTargetDate";
		return uri;
	}
}
