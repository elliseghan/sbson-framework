/*
 * Created by ASEG at Concordia University.
 * http://aseg.cs.concordia.ca
 * http://aseg.cs.concordia.ca/segps
 * Please see the LICENSE file for details.
 */

package ca.concordia.cs.aseg.sbson.ontologies.urigenerator.system_specific.abox;

import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.namespace.NamespaceFactory;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.NamespaceRegistry;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.OntologyRegistry;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.utils.URLCleaner;

public class JavaABox {

	public static String JavaDoc(String javaDocID) {
		String uri = NamespaceFactory.createAboxNamespace(
				NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_main)
				+ URLCleaner.clean(javaDocID);
		return uri;
	}
	
	public static String JavaPackage(String packageName) {
		String uri = NamespaceFactory.createAboxNamespace(
				NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_main)
				+ URLCleaner.clean(packageName);
		return uri;
	}
	
}
