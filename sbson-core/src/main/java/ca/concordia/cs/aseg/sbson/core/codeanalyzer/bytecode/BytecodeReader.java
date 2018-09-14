package ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.util.TraceSignatureVisitor;

import gr.uom.java.xmi.AccessedMember;
import gr.uom.java.xmi.FieldAccess;
import gr.uom.java.xmi.MethodCall;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLGeneralization;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.UMLRealization;
import gr.uom.java.xmi.UMLType;

public class BytecodeReader {
	private UMLModel umlModel;

	public BytecodeReader(JarFile jarFile) {
		this.umlModel = new UMLModel();
		recurse(jarFile);
	}

	public BytecodeReader(File rootFile) {
		this.umlModel = new UMLModel();
		recurse(rootFile);
	}

	public UMLModel getUmlModel() {
		return umlModel;
	}

	private void recurse(JarFile jarFile) {
		try {
			List<ClassReader> classReaders = new JarParser().getClasses(jarFile);
			for (ClassReader reader : classReaders) {
				parseBytecode(reader);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void recurse(File rootFile) {
		if (rootFile.isDirectory()) {
			File[] files = rootFile.listFiles();
			for (File file : files) {
				if (file.isDirectory())
					recurse(file);
				else {
					String fileName = file.getName();
					if (fileName.contains(".")) {
						String extension = fileName.substring(fileName.lastIndexOf("."));
						if (extension.equalsIgnoreCase(".class")) {
							parseBytecode(file);
						}
					}
				}
			}
		} else {
			String fileName = rootFile.getName();
			if (fileName.contains(".")) {
				String extension = fileName.substring(fileName.lastIndexOf("."));
				if (extension.equalsIgnoreCase(".class")) {
					parseBytecode(rootFile);
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void parseBytecode(ClassReader cr) {
		ClassNode cn = new ClassNode();
		cr.accept(cn, ClassReader.SKIP_DEBUG);

		String qualifiedClassName = cn.name.replaceAll("/", ".").replaceAll("\\$", ".");
		String packageName = null;
		String className = null;
		// System.out.println(qualifiedClassName);
		if (qualifiedClassName.contains(".")) {
			packageName = qualifiedClassName.substring(0, qualifiedClassName.lastIndexOf("."));
			className = qualifiedClassName.substring(qualifiedClassName.lastIndexOf(".") + 1);
		} else {
			packageName = "";
			className = qualifiedClassName;
		}

		UMLClass umlClass = new UMLClass(packageName, className, false);
		/*if (cn.sourceFile == null) {
			umlClass.setSourceFile("src/" + cn.name);
		} else {
			umlClass.setSourceFile(cn.sourceFile);
		}*/
		if ((cn.access & Opcodes.ACC_INTERFACE) != 0)
			umlClass.setInterface(true);
		else if ((cn.access & Opcodes.ACC_ABSTRACT) != 0)
			umlClass.setAbstract(true);

		if ((cn.access & Opcodes.ACC_PUBLIC) != 0)
			umlClass.setVisibility("public");
		else if ((cn.access & Opcodes.ACC_PROTECTED) != 0)
			umlClass.setVisibility("protected");
		else if ((cn.access & Opcodes.ACC_PRIVATE) != 0)
			umlClass.setVisibility("private");
		else
			umlClass.setVisibility("package");

		// if ((cn.access & Opcodes.ACC_STATIC) != 0)
		// umlClass.setStatic(true);
		String superClassType = cn.superName;
		if (!superClassType.equals("java/lang/Object")) {
			String type = superClassType.replaceAll("/", ".").replaceAll("\\$", ".");
			UMLType umlType = UMLType.extractTypeObject(type);
			//UMLGeneralization umlGeneralization = new UMLGeneralization(umlClass.getName(), umlType.getClassType());
			UMLGeneralization umlGeneralization = new UMLGeneralization(umlClass, umlType.getClassType());
			umlClass.setSuperclass(umlType);
			umlModel.addGeneralization(umlGeneralization);
		}

		List<String> superInterfaceTypes = cn.interfaces;
		for (String interfaceType : superInterfaceTypes) {
			//UMLRealization umlRealization = new UMLRealization(umlClass.getName(),interfaceType.replaceAll("/", ".").replaceAll("\\$", "."));
			UMLRealization umlRealization = new UMLRealization(umlClass,
					interfaceType.replaceAll("/", ".").replaceAll("\\$", "."));
			umlModel.addRealization(umlRealization);
		}

		List<UMLAttribute> staticFinalFields = new ArrayList<UMLAttribute>();
		List<FieldNode> fields = cn.fields;
		for (FieldNode fieldNode : fields) {
			UMLAttribute umlAttribute = null;
			if (fieldNode.signature != null) {
				TraceSignatureVisitor v = new TraceSignatureVisitor(ClassReader.SKIP_DEBUG);
				SignatureReader r = new SignatureReader(fieldNode.signature);
				r.accept(v);
				String declaration = v.getDeclaration();
				if (declaration.startsWith(" extends "))
					declaration = declaration.substring(9, declaration.length());
				UMLType type = UMLType.extractTypeObject(declaration);
				umlAttribute = new UMLAttribute(fieldNode.name, type);
			} else {
				Type fieldType = Type.getType(fieldNode.desc);
				UMLType type = UMLType.extractTypeObject(fieldType.getClassName().replaceAll("\\$", "."));
				umlAttribute = new UMLAttribute(fieldNode.name, type);
			}
			umlAttribute.setClassName(umlClass.getName());

			if ((fieldNode.access & Opcodes.ACC_PUBLIC) != 0)
				umlAttribute.setVisibility("public");
			else if ((fieldNode.access & Opcodes.ACC_PROTECTED) != 0)
				umlAttribute.setVisibility("protected");
			else if ((fieldNode.access & Opcodes.ACC_PRIVATE) != 0)
				umlAttribute.setVisibility("private");
			else
				umlAttribute.setVisibility("package");

			if ((fieldNode.access & Opcodes.ACC_FINAL) != 0)
				umlAttribute.setFinal(true);

			if ((fieldNode.access & Opcodes.ACC_STATIC) != 0)
				umlAttribute.setStatic(true);

			if (umlAttribute.isFinal() && umlAttribute.isStatic()) {
				umlAttribute.setValue(fieldNode.value);
				staticFinalFields.add(umlAttribute);
			}
			umlClass.addAttribute(umlAttribute);
		}

		List<MethodNode> methods = cn.methods;
		// System.out.println(methods.size());
		for (MethodNode methodNode : methods) {
			// System.out.println(methodNode.line);
			String methodName = null;
			if (methodNode.name.equals("<init>"))
				methodName = className;
			else
				methodName = methodNode.name;
			//UMLOperation umlOperation = new UMLOperation(methodName, null);

			UMLOperation umlOperation = new UMLOperation(methodName);
			umlOperation.setClassName(umlClass.getName());
			if (methodNode.name.equals("<init>"))
				umlOperation.setConstructor(true);

			//System.out.println(umlClass.getName()+"."+methodName+": "+methodNode.access);
			if ((methodNode.access & Opcodes.ACC_PUBLIC) != 0)
				umlOperation.setVisibility("public");
			else if ((methodNode.access & Opcodes.ACC_PROTECTED) != 0)
				umlOperation.setVisibility("protected");
			else if ((methodNode.access & Opcodes.ACC_PRIVATE) != 0)
				umlOperation.setVisibility("private");
			else
				umlOperation.setVisibility("package");

			if (!methodNode.name.equals("<init>")) {
				if ((methodNode.access & Opcodes.ACC_ABSTRACT) != 0)
					umlOperation.setAbstract(true);
				if ((methodNode.access & Opcodes.ACC_FINAL) != 0)
					umlOperation.setFinal(true);
				if ((methodNode.access & Opcodes.ACC_STATIC) != 0)
					umlOperation.setStatic(true);
			}

			if (methodNode.signature != null) {
				TraceSignatureVisitor v = new TraceSignatureVisitor(ClassReader.SKIP_DEBUG);
				SignatureReader r = new SignatureReader(methodNode.signature);
				r.accept(v);
				if (!methodNode.name.equals("<init>")) {
					UMLType rtype = UMLType.extractTypeObject(v.getReturnType());
					UMLParameter returnParameter = new UMLParameter("return", rtype, "return");
					umlOperation.addParameter(returnParameter);
				}

				String declaration = v.getDeclaration();
				String parameterTypes = declaration.substring(declaration.indexOf("(") + 1,
						declaration.lastIndexOf(")"));
				if (!parameterTypes.isEmpty()) {
					String[] tokens = parameterTypes.split(", ");
					for (String token : tokens) {
						try {
							UMLType type = UMLType.extractTypeObject(token);
							UMLParameter umlParameter = new UMLParameter("", type, "in");
							umlOperation.addParameter(umlParameter);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			} else {
				if (!methodNode.name.equals("<init>")) {
					Type returnType = Type.getReturnType(methodNode.desc);
					UMLType rtype = UMLType.extractTypeObject(returnType.getClassName().replaceAll("\\$", "."));
					UMLParameter returnParameter = new UMLParameter("return", rtype, "return");
					umlOperation.addParameter(returnParameter);
				}

				Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
				for (Type argumentType : argumentTypes) {
					UMLType type = UMLType.extractTypeObject(argumentType.getClassName().replaceAll("\\$", "."));
					UMLParameter umlParameter = new UMLParameter("", type, "in");
					boolean skipArgument = false;
					if (cn.name.contains("$") && methodNode.name.equals("<init>")
							&& argumentType.getClassName().replaceAll("\\$", ".").equals(packageName))
						skipArgument = true;
					if (!skipArgument)
						umlOperation.addParameter(umlParameter);
				}
			}

			if (methodNode.instructions.size() > 0) {
				Iterator instructionIterator = methodNode.instructions.iterator();
				while (instructionIterator.hasNext()) {
					AbstractInsnNode instruction = (AbstractInsnNode) instructionIterator.next();
					if (instruction instanceof FieldInsnNode) {
						FieldInsnNode fieldInstruction = (FieldInsnNode) instruction;
						Type fieldType = Type.getType(fieldInstruction.desc);
						FieldAccess fieldAccess = new FieldAccess(
								fieldInstruction.owner.replaceAll("/", ".").replaceAll("\\$", "."),
								fieldType.getClassName(), fieldInstruction.name);
						umlOperation.addAccessedMember(fieldAccess);
					}

					// special handling for accessed final static fields
					// (constants)
					if (instruction instanceof LdcInsnNode) {
						LdcInsnNode ldcInstruction = (LdcInsnNode) instruction;
						Object value = ldcInstruction.cst;
						for (UMLAttribute attr : staticFinalFields) {
							Double attributeValue = null;
							Double fieldAccessValue = null;
							if (attr.getValue() instanceof Number) {
								attributeValue = ((Number) attr.getValue()).doubleValue();
							}
							if (value instanceof Number) {
								fieldAccessValue = ((Number) value).doubleValue();
							}
							if ((attr.getValue() != null && attr.getValue().equals(value)) || (attributeValue != null
									&& fieldAccessValue != null && attributeValue.equals(fieldAccessValue))) {
								FieldAccess fieldAccess = new FieldAccess(attr.getClassName(),
										attr.getType().getClassType(), attr.getName());
								umlOperation.addAccessedMember(fieldAccess);
							}
						}
					}

					if ((instruction.getOpcode() == Opcodes.INVOKEVIRTUAL)
							|| (instruction.getOpcode() == Opcodes.INVOKESTATIC)
							|| (instruction.getOpcode() == Opcodes.INVOKESPECIAL)
							|| (instruction.getOpcode() == Opcodes.INVOKEINTERFACE)) {

						MethodInsnNode methodInstruction = (MethodInsnNode) instruction;
						Type returnType = Type.getReturnType(methodInstruction.desc);
						UMLType rtype = UMLType.extractTypeObject(returnType.getClassName().replaceAll("\\$", "."));
						MethodCall methodCall = new MethodCall(
								methodInstruction.owner.replaceAll("/", ".").replaceAll("\\$", "."),
								methodInstruction.name, rtype);
						Type[] argTypes = Type.getArgumentTypes(methodInstruction.desc);
						for (Type argType : argTypes) {
							UMLType type = UMLType.extractTypeObject(argType.getClassName().replaceAll("\\$", "."));
							methodCall.addParameter(type);
						}
						boolean isObjectConstructorCall = false;
						if (methodInstruction.name.equals("<init>")) {
							methodCall.setConstructorCall(true);
							if (methodCall.getOriginClassName().equals("java.lang.Object"))
								isObjectConstructorCall = true;
						}

						if (!isObjectConstructorCall)
							umlOperation.addAccessedMember(methodCall);
					}

					if ((instruction.getOpcode() == Opcodes.NEW) || (instruction.getOpcode() == Opcodes.ANEWARRAY)) {
						@SuppressWarnings("unused")
						TypeInsnNode typeInstruction = (TypeInsnNode) instruction;
					}
				}
			}

			umlClass.addOperation(umlOperation);
		}

		umlModel.addClass(umlClass);
	}

	private void parseBytecode(File classFile) {
		try {
			FileInputStream fin = new FileInputStream(classFile);
			ClassReader cr = new ClassReader(new DataInputStream(fin));
			parseBytecode(cr);
			fin.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void setUmlModel(UMLModel umlModel) {
		this.umlModel = umlModel;
	}

	public static void main(String[] args) {
		File file = new File("D:\\git\\personal\\SourceCodeAnalysis\\axis-1.4.jar");
		JarFile jarFile;
		try {
			jarFile = new JarFile(file);
			UMLModel model = new BytecodeReader(jarFile).getUmlModel();
			List<UMLClass> classes = model.getClassList();
			List<UMLRealization> realizations = model.getRealizationList();
			System.out.println(classes.size());
			for (UMLClass clazz : classes) {
				System.out.println("class: " + clazz.getName() +" ("+clazz.getSourceFile()+")");
				UMLType superclass = clazz.getSuperclass();
				if(superclass!=null){
					System.out.println("\tinherits: "+superclass.toString());
				}
				if(clazz.isInterface()){
					for(UMLRealization realization: realizations){
						if(realization.getSupplier().equals(clazz.getName())){
							System.out.println("\timplemented by: "+realization.getClient().getName());
						}
					}
				}
				/*for (UMLOperation operation : clazz.getOperations()) {
					System.out.println("\tmethod: " + operation.toString());
					Set<AccessedMember> members = operation.getAccessedMembers();
					for (AccessedMember member : members) {
						//if (member.getClass().getSimpleName().equals("FieldAccess"))
							System.out.println("\t\tinvocation: " + member.toString());
					}
				}*/
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
