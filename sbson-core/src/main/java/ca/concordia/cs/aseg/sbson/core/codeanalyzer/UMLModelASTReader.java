package ca.concordia.cs.aseg.sbson.core.codeanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.IDocElement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import gr.uom.java.xmi.AnonymousClassDeclarationVisitor;
import gr.uom.java.xmi.UMLAnonymousClass;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLGeneralization;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.UMLRealization;
import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.decomposition.OperationBody;

public class UMLModelASTReader {
	private static final String systemFileSeparator = Matcher.quoteReplacement(File.separator);

	private UMLModel umlModel;
	private String projectRoot;
	private ASTParser parser;

	public UMLModelASTReader(File rootFolder, List<String> javaFiles2) {
		this(rootFolder, buildAstParser(rootFolder), javaFiles2);
	}

	public UMLModelASTReader(File rootFolder, ASTParser parser, List<String> javaFiles) {
		this.umlModel = new UMLModel();
		this.projectRoot = rootFolder.getPath();
		this.parser = parser;
		final String[] emptyArray = new String[0];

		String[] filesArray = new String[javaFiles.size()];
		for (int i = 0; i < filesArray.length; i++) {
			// System.out.println(rootFolder + File.separator +
			// javaFiles.get(i).replaceAll("/", systemFileSeparator));
			filesArray[i] = rootFolder + File.separator + javaFiles.get(i).replaceAll("/", systemFileSeparator);
		}

		FileASTRequestor fileASTRequestor = new FileASTRequestor() {
			@Override
			public void acceptAST(String sourceFilePath, CompilationUnit ast) {
				String relativePath = sourceFilePath.substring(projectRoot.length() + 1).replaceAll(systemFileSeparator,
						"/");
				try {
					processCompilationUnit(sourceFilePath,relativePath, ast);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		this.parser.createASTs((String[]) filesArray, null, emptyArray, fileASTRequestor, null);
	}

	private static ASTParser buildAstParser(File srcFolder) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setResolveBindings(false);
		parser.setEnvironment(new String[0], new String[] { srcFolder.getPath() }, null, false);
		return parser;
	}

	public UMLModel getUmlModel() {
		return this.umlModel;
	}

	protected void processCompilationUnit(String sourceFilePath, String relativePath, CompilationUnit compilationUnit) throws IOException {
		PackageDeclaration packageDeclaration = compilationUnit.getPackage();
		String packageName = null;
		if (packageDeclaration != null)
			packageName = packageDeclaration.getName().getFullyQualifiedName();
		else
			packageName = "";

		List<AbstractTypeDeclaration> topLevelTypeDeclarations = compilationUnit.types();
		for (AbstractTypeDeclaration abstractTypeDeclaration : topLevelTypeDeclarations) {
			if (abstractTypeDeclaration instanceof TypeDeclaration) {
				TypeDeclaration topLevelTypeDeclaration = (TypeDeclaration) abstractTypeDeclaration;
				processTypeDeclaration(compilationUnit, topLevelTypeDeclaration, packageName, sourceFilePath, relativePath);
			}
		}
		

	}

	private String getTypeName(Type type, int extraDimensions) {
		ITypeBinding binding = type.resolveBinding();
		if (binding != null) {
			return binding.getQualifiedName();
		}
		String typeToString = type.toString();
		for (int i = 0; i < extraDimensions; i++) {
			typeToString += "[]";
		}
		return typeToString;
	}

	/*private void processTypeDeclaration(TypeDeclaration typeDeclaration, String packageName, String sourceFile) {
		Javadoc javaDoc = typeDeclaration.getJavadoc();
		if (javaDoc != null) {
			List<TagElement> tags = javaDoc.tags();
			for (TagElement tag : tags) {
				List<IDocElement> fragments = tag.fragments();
				for (IDocElement docElement : fragments) {
					if (docElement instanceof TextElement) {
						TextElement textElement = (TextElement) docElement;
						if (textElement.getText().contains("Source code generated using FreeMarker template")) {
							return;
						}
					}
				}
			}
		}
		String className = typeDeclaration.getName().getFullyQualifiedName();
		UMLClass umlClass = new UMLClass(packageName, className, sourceFile,
				typeDeclaration.isPackageMemberTypeDeclaration());

		if (typeDeclaration.isInterface()) {
			umlClass.setInterface(true);
		}

		int modifiers = typeDeclaration.getModifiers();
		if ((modifiers & Modifier.ABSTRACT) != 0)
			umlClass.setAbstract(true);

		if ((modifiers & Modifier.PUBLIC) != 0)
			umlClass.setVisibility("public");
		else if ((modifiers & Modifier.PROTECTED) != 0)
			umlClass.setVisibility("protected");
		else if ((modifiers & Modifier.PRIVATE) != 0)
			umlClass.setVisibility("private");
		else
			umlClass.setVisibility("package");

		Type superclassType = typeDeclaration.getSuperclassType();
		if (superclassType != null) {
			UMLType umlType = UMLType.extractTypeObject(this.getTypeName(superclassType, 0));
			UMLGeneralization umlGeneralization = new UMLGeneralization(umlClass, umlType.getClassType());
			umlClass.setSuperclass(umlType);
			getUmlModel().addGeneralization(umlGeneralization);
		}

		List<Type> superInterfaceTypes = typeDeclaration.superInterfaceTypes();
		for (Type interfaceType : superInterfaceTypes) {
			UMLRealization umlRealization = new UMLRealization(umlClass, this.getTypeName(interfaceType, 0));
			getUmlModel().addRealization(umlRealization);
		}

		FieldDeclaration[] fieldDeclarations = typeDeclaration.getFields();
		for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
			List<UMLAttribute> attributes = processFieldDeclaration(fieldDeclaration  , bytecodeClass  );
			for (UMLAttribute attribute : attributes) {
				attribute.setClassName(umlClass.getName());
				umlClass.addAttribute(attribute);
			}
		}

		MethodDeclaration[] methodDeclarations = typeDeclaration.getMethods();
		for (MethodDeclaration methodDeclaration : methodDeclarations) {
			UMLOperation operation = processMethodDeclaration(methodDeclaration, packageName,
					className  , bytecodeClass  );
			operation.setClassName(umlClass.getName());
			umlClass.addOperation(operation);
		}

		AnonymousClassDeclarationVisitor visitor = new AnonymousClassDeclarationVisitor();
		typeDeclaration.accept(visitor);
		Set<AnonymousClassDeclaration> anonymousClassDeclarations = visitor.getAnonymousClassDeclarations();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for (AnonymousClassDeclaration anonymous : anonymousClassDeclarations) {
			insertNode(anonymous, root);
		}

		Enumeration<DefaultMutableTreeNode> enumeration = root.preorderEnumeration();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode node = enumeration.nextElement();
			if (node.getUserObject() != null) {
				AnonymousClassDeclaration anonymous = (AnonymousClassDeclaration) node.getUserObject();
				String anonymousName = getAnonymousName(node);
				processAnonymousClassDeclaration(anonymous, packageName + "." + className, anonymousName, sourceFile);
			}
		}
		

		this.getUmlModel().addClass(umlClass);

		TypeDeclaration[] types = typeDeclaration.getTypes();
		for (TypeDeclaration type : types) {
			processTypeDeclaration(type, umlClass.getName(), sourceFile);
		}
	}*/

	private void processTypeDeclaration(CompilationUnit cu,TypeDeclaration typeDeclaration, String packageName,  String sourcePath, String sourceFile) {
		Javadoc javaDoc = typeDeclaration.getJavadoc();
		if (javaDoc != null) {
			List<TagElement> tags = javaDoc.tags();
			for (TagElement tag : tags) {
				List<IDocElement> fragments = tag.fragments();
				for (IDocElement docElement : fragments) {
					if (docElement instanceof TextElement) {
						TextElement textElement = (TextElement) docElement;
						if (textElement.getText().contains("Source code generated using FreeMarker template")) {
							return;
						}
					}
				}
			}
		}
		String className = typeDeclaration.getName().getFullyQualifiedName();
		UMLClass umlClass = new UMLClass(packageName, className, sourceFile,
				typeDeclaration.isPackageMemberTypeDeclaration());

		if (typeDeclaration.isInterface()) {
			umlClass.setInterface(true);
		}

		int modifiers = typeDeclaration.getModifiers();
		if ((modifiers & Modifier.ABSTRACT) != 0)
			umlClass.setAbstract(true);

		if ((modifiers & Modifier.PUBLIC) != 0)
			umlClass.setVisibility("public");
		else if ((modifiers & Modifier.PROTECTED) != 0)
			umlClass.setVisibility("protected");
		else if ((modifiers & Modifier.PRIVATE) != 0)
			umlClass.setVisibility("private");
		else
			umlClass.setVisibility("package");

		Type superclassType = typeDeclaration.getSuperclassType();
		if (superclassType != null) {
			UMLType umlType = UMLType.extractTypeObject(this.getTypeName(superclassType, 0));
			UMLGeneralization umlGeneralization = new UMLGeneralization(umlClass, umlType.getClassType());
			umlClass.setSuperclass(umlType);
			getUmlModel().addGeneralization(umlGeneralization);
		}

		List<Type> superInterfaceTypes = typeDeclaration.superInterfaceTypes();
		for (Type interfaceType : superInterfaceTypes) {
			UMLRealization umlRealization = new UMLRealization(umlClass, this.getTypeName(interfaceType, 0));
			getUmlModel().addRealization(umlRealization);
		}

		FieldDeclaration[] fieldDeclarations = typeDeclaration.getFields();
		for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
			List<UMLAttribute> attributes = processFieldDeclaration(fieldDeclaration /* , bytecodeClass */ );
			for (UMLAttribute attribute : attributes) {
				attribute.setClassName(umlClass.getName());
				umlClass.addAttribute(attribute);
			}
		}

		MethodDeclaration[] methodDeclarations = typeDeclaration.getMethods();
		for (MethodDeclaration methodDeclaration : methodDeclarations) {
			UMLOperation operation = processMethodDeclaration(methodDeclaration, packageName,
					className /* , bytecodeClass */ );
			operation.setClassName(umlClass.getName());
			umlClass.addOperation(operation);
		}

		AnonymousClassDeclarationVisitor visitor = new AnonymousClassDeclarationVisitor();
		typeDeclaration.accept(visitor);
		Set<AnonymousClassDeclaration> anonymousClassDeclarations = visitor.getAnonymousClassDeclarations();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for (AnonymousClassDeclaration anonymous : anonymousClassDeclarations) {
			insertNode(anonymous, root);
		}

		Enumeration<TreeNode> enumeration = root.preorderEnumeration();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
			if (node.getUserObject() != null) {
				AnonymousClassDeclaration anonymous = (AnonymousClassDeclaration) node.getUserObject();
				String anonymousName = getAnonymousName(node);
				processAnonymousClassDeclaration(anonymous, packageName + "." + className, anonymousName, sourceFile);
			}
		}
		
		try {
			List<UMLComment> commentList = processCommentDeclaration(cu, sourcePath);
			umlClass.setComments(commentList);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.getUmlModel().addClass(umlClass);

		TypeDeclaration[] types = typeDeclaration.getTypes();
		for (TypeDeclaration type : types) {
			processTypeDeclaration(cu, type, umlClass.getName(), sourcePath, sourceFile);
		}
	}
	
	private UMLOperation processMethodDeclaration(MethodDeclaration methodDeclaration, String packageName,
			String className) {
		String methodName = methodDeclaration.getName().getFullyQualifiedName();
		UMLOperation umlOperation = new UMLOperation(methodName);
		// umlOperation.setClassName(umlClass.getName());
		if (methodDeclaration.isConstructor())
			umlOperation.setConstructor(true);

		int methodModifiers = methodDeclaration.getModifiers();
		if ((methodModifiers & Modifier.PUBLIC) != 0)
			umlOperation.setVisibility("public");
		else if ((methodModifiers & Modifier.PROTECTED) != 0)
			umlOperation.setVisibility("protected");
		else if ((methodModifiers & Modifier.PRIVATE) != 0)
			umlOperation.setVisibility("private");
		else
			umlOperation.setVisibility("package");

		if ((methodModifiers & Modifier.ABSTRACT) != 0)
			umlOperation.setAbstract(true);

		if ((methodModifiers & Modifier.FINAL) != 0)
			umlOperation.setFinal(true);

		if ((methodModifiers & Modifier.STATIC) != 0)
			umlOperation.setStatic(true);

		Block block = methodDeclaration.getBody();
		if (block != null) {
			OperationBody body = new OperationBody(block);
			umlOperation.setBody(body);
			if (block.statements().size() == 0) {
				umlOperation.setEmptyBody(true);
			}
		} else {
			umlOperation.setBody(null);
		}

		Type returnType = methodDeclaration.getReturnType2();
		if (returnType != null) {
			UMLType type = UMLType.extractTypeObject(getTypeName(returnType, 0));
			UMLParameter returnParameter = new UMLParameter("return", type, "return");
			umlOperation.addParameter(returnParameter);
		}
		List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
		for (SingleVariableDeclaration parameter : parameters) {
			Type parameterType = parameter.getType();
			String parameterName = parameter.getName().getFullyQualifiedName();
			String typeName = getTypeName(parameterType, parameter.getExtraDimensions());
			if (parameter.isVarargs()) {
				typeName = typeName + "[]";
			}
			UMLType type = UMLType.extractTypeObject(typeName);
			UMLParameter umlParameter = new UMLParameter(parameterName, type, "in");
			umlOperation.addParameter(umlParameter);
		}
		return umlOperation;
	}

	private List<UMLAttribute> processFieldDeclaration(
			FieldDeclaration fieldDeclaration /* , UMLClass bytecodeClass */ ) {
		List<UMLAttribute> attributes = new ArrayList<UMLAttribute>();
		Type fieldType = fieldDeclaration.getType();
		List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
		for (VariableDeclarationFragment fragment : fragments) {
			UMLType type = UMLType.extractTypeObject(getTypeName(fieldType, fragment.getExtraDimensions()));
			String fieldName = fragment.getName().getFullyQualifiedName();
			UMLAttribute umlAttribute = new UMLAttribute(fieldName, type);
			// umlAttribute.setClassName(umlClass.getName());

			int fieldModifiers = fieldDeclaration.getModifiers();
			if ((fieldModifiers & Modifier.PUBLIC) != 0)
				umlAttribute.setVisibility("public");
			else if ((fieldModifiers & Modifier.PROTECTED) != 0)
				umlAttribute.setVisibility("protected");
			else if ((fieldModifiers & Modifier.PRIVATE) != 0)
				umlAttribute.setVisibility("private");
			else
				umlAttribute.setVisibility("package");

			if ((fieldModifiers & Modifier.FINAL) != 0)
				umlAttribute.setFinal(true);

			if ((fieldModifiers & Modifier.STATIC) != 0)
				umlAttribute.setStatic(true);

			attributes.add(umlAttribute);
		}
		return attributes;
	}

	private void processAnonymousClassDeclaration(AnonymousClassDeclaration anonymous, String packageName,
			String className, String sourceFile) {
		List<BodyDeclaration> bodyDeclarations = anonymous.bodyDeclarations();

		UMLAnonymousClass anonymousClass = new UMLAnonymousClass(packageName, className, sourceFile);

		for (BodyDeclaration bodyDeclaration : bodyDeclarations) {
			if (bodyDeclaration instanceof FieldDeclaration) {
				FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
				List<UMLAttribute> attributes = processFieldDeclaration(fieldDeclaration /* , bytecodeClass */ );
				for (UMLAttribute attribute : attributes) {
					attribute.setClassName(anonymousClass.getName());
					anonymousClass.addAttribute(attribute);
				}
			} else if (bodyDeclaration instanceof MethodDeclaration) {
				MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
				UMLOperation operation = processMethodDeclaration(methodDeclaration, packageName,
						className /* , bytecodeClass */ );
				operation.setClassName(anonymousClass.getName());
				anonymousClass.addOperation(operation);
			}
		}

		this.getUmlModel().addAnonymousClass(anonymousClass);
	}

	private List<UMLComment> processCommentDeclaration(CompilationUnit cu, String sourceFile) throws IOException {
		List<UMLComment> comments = new ArrayList<>();
		//System.out.println("Comments in: " + sourceFile);
		// System.out.println(cu.toString());
		for (Comment comment : (List<Comment>) cu.getCommentList()) {
			CommentVisitor commentVisitor = new CommentVisitor(cu, readFileToString(sourceFile));
			comment.accept(commentVisitor);
			String commentStr =commentVisitor.getComment();
			if(commentStr!=null){
				UMLComment umlcomment = new UMLComment();
				umlcomment.setCommentString(commentStr);
				comments.add(umlcomment);
			}
		}
		return comments;
	}

	private void insertNode(AnonymousClassDeclaration childAnonymous, DefaultMutableTreeNode root) {
		Enumeration<TreeNode> enumeration = (root.postorderEnumeration());
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childAnonymous);

		DefaultMutableTreeNode parentNode = root;
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
			AnonymousClassDeclaration currentAnonymous = (AnonymousClassDeclaration) currentNode.getUserObject();
			if (currentAnonymous != null && isParent(childAnonymous, currentAnonymous)) {
				parentNode = currentNode;
				break;
			}
		}
		parentNode.add(childNode);
	}

	private String getAnonymousName(DefaultMutableTreeNode node) {
		StringBuilder name = new StringBuilder();
		TreeNode[] path = node.getPath();
		for (int i = 0; i < path.length; i++) {
			DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) path[i];
			if (tmp.getUserObject() != null) {
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tmp.getParent();
				int index = parent.getIndex(tmp);
				name.append(index + 1);
				if (i < path.length - 1)
					name.append(".");
			}
		}
		return name.toString();
	}

	private boolean isParent(ASTNode child, ASTNode parent) {
		ASTNode current = child;
		while (current.getParent() != null) {
			if (current.getParent().equals(parent))
				return true;
			current = current.getParent();
		}
		return false;
	}

	private static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();
		return fileData.toString();
	}
}
