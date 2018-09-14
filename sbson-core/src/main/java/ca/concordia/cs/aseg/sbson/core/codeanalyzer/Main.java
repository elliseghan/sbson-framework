package ca.concordia.cs.aseg.sbson.core.codeanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarFile;

import ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode.BytecodeReader;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode.JarParser;
import org.apache.commons.io.FileUtils;


import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLRealization;
import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.decomposition.OperationBody;
import gr.uom.java.xmi.decomposition.OperationInvocation;

public class Main {

	public static void main(String[] args) {
		try {
			//testLicense("aether-util-1.0.2.v20150114.jar", "maven-embedder-3.3.9.jar", "officeframe-2.18.0.jar");
			testLicense("aether-util-1.0.2.v20150114.jar", "maven-aether-provider-3.3.9.jar", "officeframe-2.18.0.jar");
			// testAPIConversion("testCallgraph.jar");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void testLicense(String p1, String p2, String p3) throws IOException {

		File p1Jar = new File(p1);
		HashMap<MethodDeclaration, List<MethodInvocation>> p1AllProductMethodCalls = new HashMap<MethodDeclaration, List<MethodInvocation>>();
		JarParser jarParser = new JarParser(p1Jar);
		if (jarParser != null) {
			p1AllProductMethodCalls = jarParser.getMethodCalls();
		}

		File p2Jar = new File(p2);
		HashMap<MethodDeclaration, List<MethodInvocation>> p2AllProductMethodCalls = new HashMap<MethodDeclaration, List<MethodInvocation>>();
		jarParser = new JarParser(p2Jar);
		if (jarParser != null) {
			p2AllProductMethodCalls = jarParser.getMethodCalls();
		}

		File p3Jar = new File(p3);
		HashMap<MethodDeclaration, List<MethodInvocation>> p3AllProductMethodCalls = new HashMap<MethodDeclaration, List<MethodInvocation>>();
		jarParser = new JarParser(p3Jar);
		if (jarParser != null) {
			p3AllProductMethodCalls = jarParser.getMethodCalls();
		}

		// check if p3 using p2
		JarFile jarFile = new JarFile(p1);
		UMLModel model = new BytecodeReader(jarFile).getUmlModel();
		Set<String> sources = new TreeSet();
		for(UMLClass umlClass: model.getClassList()){
			sources.add(umlClass.getName());
		}
	System.out.println(sources);

		
		for (MethodDeclaration declaration : p2AllProductMethodCalls.keySet()) {
			for (MethodInvocation invocation : p2AllProductMethodCalls.get(declaration)) {
				String modifiedSource = invocation.getOwner().replace("/", ".");
//				if(modifiedSource.startsWith("org.eclipse.aether.util."))
//				System.out.println(modifiedSource);

				
				if (sources.contains(modifiedSource)) {
					System.out.println(declaration.getClassName() +" -> "+modifiedSource);
					break;
				}
			}
		}

		
	}

	static void testAPIConversion(String jarLocation) throws IOException {
		JarFile jarFile = new JarFile(jarLocation);
		UMLModel model = new BytecodeReader(jarFile).getUmlModel();
		System.out.println(model.getClassList().size());
		/*
		 * File jar = new File(jarLocation); HashMap<MethodDeclaration,
		 * List<MethodInvocation>> allProductMethodCalls = new
		 * HashMap<MethodDeclaration, List<MethodInvocation>>(); JarParser
		 * jarParser = new JarParser(jar); if (jarParser != null) {
		 * allProductMethodCalls = jarParser.getMethodCalls(); for
		 * (MethodDeclaration declaration : allProductMethodCalls.keySet()) {
		 * System.out.println(declaration.convertToString()); } }
		 */

	}

	private void searchLicenseComments() {
		// String rootFolder = "D:\\git\\personal\\maven-processor\\src";
		String rootFolder = "D:\\eclipse\\workspace\\xmi-diff\\";
		File rootDir = new File(rootFolder);
		Collection<File> files = FileUtils.listFiles(rootDir, new String[] { "java" }, true);
		List<String> fileList = new ArrayList<>();
		for (File file : files) {
			String javaFile = file.getAbsolutePath().replace(rootFolder, "");
			fileList.add(javaFile);
		}

		try {
			UMLModelASTReader astReader = new UMLModelASTReader(rootDir, fileList);
			UMLModel model = astReader.getUmlModel();
			List<UMLClass> classes = model.getClassList();
			List<UMLRealization> realizations = model.getRealizationList();
			System.out.println(classes.size());
			for (UMLClass clazz : classes) {
				System.out.println("class: " + clazz.getName() + " (" + clazz.getSourceFile() + ")");
				System.out.println(clazz.getComments().size() + " comments found");
				String license = LicenseAnalyzer.getLicenseFromCommentList(clazz.getComments());
				if (license != null)
					System.out.println("Has License?: true");
				else
					System.out.println("Has License?: false");
				UMLType superclass = clazz.getSuperclass();
				if (superclass != null) {
					System.out.println("\tinherits: " + superclass.toString());
				}
				if (clazz.isInterface()) {
					for (UMLRealization realization : realizations) {
						if (realization.getSupplier().equals(clazz.getName())) {
							System.out.println("\timplemented by: " + realization.getClient().getName());
						}
					}
				}
				for (UMLOperation operation : clazz.getOperations()) {
					System.out.println("\tmethod: " + operation.toString());
					// Set<AccessedMember> members =
					// operation.getAccessedMembers();
					OperationBody operationBody = operation.getBody();
					if (operationBody != null) {
						Set<OperationInvocation> members = operationBody.getAllOperationInvocations();
						for (OperationInvocation member : members) {
							// if
							// (member.getClass().getSimpleName().equals("FieldAccess"))
							System.out.println("\t\tinvocation: " + member.toString());
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
