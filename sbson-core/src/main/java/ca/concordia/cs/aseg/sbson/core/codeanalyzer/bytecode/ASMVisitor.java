package ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodDeclaration;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodInvocation;

public class ASMVisitor extends ClassVisitor {
	private String currentClass = null;
	private MethodDeclaration currentMethod = null;
	private HashMap<MethodDeclaration, List<MethodInvocation>> classMethodsInfo = null;

	public ASMVisitor(int arg0) {
		super(arg0);
		classMethodsInfo = new HashMap<MethodDeclaration, List<MethodInvocation>>();
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		currentClass = name;
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		currentMethod = new MethodDeclaration(currentClass, access, name, desc,
				signature, exceptions);
		//System.out.println(access + " " + name + " " + desc + " " + signature	+ " " + exceptions);
		if (!classMethodsInfo.containsKey(currentMethod)) {
			classMethodsInfo.put(currentMethod,
					new ArrayList<MethodInvocation>());
		}
		MethodVisitor impl = new MethodVisitor(Opcodes.ASM5) {
		};

		InstructionAdapter instMv = new InstructionAdapter(Opcodes.ASM5, impl) {

			@Override
			public void visitMethodInsn(int opcode, String owner, String name,
					String desc, boolean itf) {

				MethodInvocation invocation = new MethodInvocation(opcode,
						owner, name, desc);
				invocation.setInvocationSource(currentMethod);
				classMethodsInfo.get(currentMethod).add(invocation);
			}

		};

		return instMv;

	}

	public HashMap<MethodDeclaration, List<MethodInvocation>> getClassMethodsInfo() {
		return classMethodsInfo;
	}

	public void setClassMethodsInfo(
			HashMap<MethodDeclaration, List<MethodInvocation>> classMethodsInfo) {
		this.classMethodsInfo = classMethodsInfo;
	}
}
