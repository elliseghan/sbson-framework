/*
 * Created by ASEG at Concordia University.
 * http://aseg.cs.concordia.ca
 * http://aseg.cs.concordia.ca/segps
 * Please see the LICENSE file for details.
 */

package ca.concordia.cs.aseg.sbson.ontologies.urigenerator.domain_specific.abox;

import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.namespace.NamespaceFactory;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.NamespaceRegistry;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.OntologyRegistry;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.utils.URLCleaner;

public class CodeABox {

	/*public static String AccessModifier() {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ "AccessModifier";
		return uri;
	}
*/
	
	/*public static String AnnotationType() {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ "AnnotationType";
		return uri;
	}*/

	public static String ClassType(String project, String fullQualifiedClassName) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ URLCleaner.clean(project)+":"+URLCleaner.clean(fullQualifiedClassName);
		return uri;
	}

	/*public static String CodeEntity() {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ "CodeEntity";
		return uri;
	}*/

	/*public static String ComplexType() {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ "ComplexType";
		return uri;
	}*/

	public static String Constructor(String project, String fullQualifiedOwner, String constructorSignature) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ URLCleaner.clean(project)+":"+URLCleaner.clean(fullQualifiedOwner)+":"+URLCleaner.clean(constructorSignature);
		return uri;
	}
	
	public static String Constructor(String fullQualifiedOwnerWithProject, String constructorSignature) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ URLCleaner.clean(fullQualifiedOwnerWithProject)+":"+URLCleaner.clean(constructorSignature);
		return uri;
	}

	/*public static String Datatype() {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ "Datatype";
		return uri;
	}*/

	/*public static String EnumerationType() {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ "EnumerationType";
		return uri;
	}*/

	/*public static String ExceptionType() {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ "ExceptionType";
		return uri;
	}*/

	/*public static String Field() {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ "Field";
		return uri;
	}*/

	public static String InterfaceType(String project, String fullQualifiedInterfaceName) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ URLCleaner.clean(project)+":"+URLCleaner.clean(fullQualifiedInterfaceName);
		return uri;
	}

	public static String Method(String project, String fullQualifiedOwner,String methodSignature) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ URLCleaner.clean(project)+":"+URLCleaner.clean(fullQualifiedOwner)+":"+URLCleaner.clean(methodSignature);
		return uri;
	}
	
	public static String Method(String fullQualifiedOwnerWithProject,String methodSignature) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+URLCleaner.clean(fullQualifiedOwnerWithProject)+":"+URLCleaner.clean(methodSignature);
		return uri;
	}

	public static String Namespace(String project, String nameSpace) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ URLCleaner.clean(project)+":"+URLCleaner.clean(nameSpace);
		return uri;
	}

	public static String Parameter(String owner, String paramString) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ URLCleaner.clean(owner)+":"+URLCleaner.clean(paramString);
		return uri;
	}

	public static String PrimitiveType(String returnType) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ URLCleaner.clean(returnType);
		return uri;
	}

	/*public static String Variable() {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace, OntologyRegistry.seon_code)
				+ "Variable";
		return uri;
	}*/

}
