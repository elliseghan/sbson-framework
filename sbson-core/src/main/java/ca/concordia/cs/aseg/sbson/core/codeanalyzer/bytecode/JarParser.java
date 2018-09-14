package ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodDeclaration;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodInvocation;


public class JarParser {

	private JarFile jarFile;
	private List<ClassReader> allClasses;

	public JarParser() {
	}

	public JarParser(File file) {
		try {
			jarFile = new JarFile(file);
			allClasses = getClasses();
		} catch (ZipException e) {
			//e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<ClassReader> getClasses(JarFile jarFile) throws IOException {
		List<ClassReader> classes = new ArrayList<ClassReader>();
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.isDirectory())
				continue;

			if (!entry.getName().endsWith(".class"))
				continue;

			ClassReader cp = new ClassReader(jarFile.getInputStream(entry));
			classes.add(cp);
		}
		return classes;
	}

	private List<ClassReader> getClasses() throws IOException {
		return getClasses(this.jarFile);
	}

	public List<String> getClassNames() throws IOException {
		List<String> classNames = new ArrayList<String>();
		for (ClassReader reader : allClasses) {
			classNames.add(reader.getClassName());
		}
		return classNames;
	}

	public HashMap<MethodDeclaration, List<MethodInvocation>> getMethodCalls() {
		/*
		 * Get all classes, and accept a visitor for each. Compile the results
		 * returned by the visitor objects
		 */
		// List<String> allInvokedMethods = new ArrayList<String>();
		HashMap<MethodDeclaration, List<MethodInvocation>> allClassMethodsInfo = new HashMap<MethodDeclaration, List<MethodInvocation>>();
		if (allClasses != null) {
			for (ClassReader reader : allClasses) {

				ASMVisitor asmVisitor = new ASMVisitor(Opcodes.ASM5);
				try {
					reader.accept(asmVisitor, ClassReader.SKIP_DEBUG);
				} catch (ArrayIndexOutOfBoundsException exception) {
					
				}
				// System.out.println(asmVisitor.getClassMethodsInfo());
				allClassMethodsInfo.putAll(asmVisitor.getClassMethodsInfo());
			}
		}
		return allClassMethodsInfo;
	}

	public static MethodDeclaration getMethodDeclaration(
			HashMap<MethodDeclaration, List<MethodInvocation>> allMethodCalls,
			String className, String methodName, String params) {
		MethodDeclaration methodDeclaration = null;
		String byteClassName = className.replace('.', '/');
		// System.out.println("byteClassName:"+byteClassName);
		for (MethodDeclaration declaration : allMethodCalls.keySet()) {

			// System.out.println(declaration.getClassName());
			if (declaration.getClassName().equalsIgnoreCase(byteClassName)
					&& declaration.getName().equalsIgnoreCase(methodName)) {
				// check parameter
				// System.out.println(declaration);
				String[] byteParams = getParameters(params);
				methodDeclaration = declaration;
				break;
			}
		}

		return methodDeclaration;
	}

	public static List<MethodDeclaration> getMethodDeclaration(
			HashMap<MethodDeclaration, List<MethodInvocation>> allMethodCalls,
			String className) {
		List<MethodDeclaration> methodDeclarations = new ArrayList<MethodDeclaration>();
		String byteClassName = className.replace('.', '/');
		// System.out.println("byteClassName:"+byteClassName);
		for (MethodDeclaration declaration : allMethodCalls.keySet()) {
			// System.out.println(declaration.getClassName());
			if (declaration.getClassName().equalsIgnoreCase(byteClassName)) {
				methodDeclarations.add(declaration);
			}
		}

		return methodDeclarations;
	}

	private static String[] getParameters(String parameters) {
		if (parameters == null || parameters.trim().isEmpty())
			return null;

		parameters = parameters.replace("(", "");
		parameters = parameters.replace(")", "");
		String[] paramArray = parameters.split(",");
		// System.out.println(paramArray.length);
		for (String str : paramArray) {
			str = str.trim();
			String[] parts = str.split(" ");
			// System.out.println(parts[0]);
		}
		return null;
	}

	private static String getReturn(String returnStr) {
		return null;
	}

	public static void main(String[] args) {
		getReturn("boolean");
		getParameters("(String a, String[] b)");
	}
}
