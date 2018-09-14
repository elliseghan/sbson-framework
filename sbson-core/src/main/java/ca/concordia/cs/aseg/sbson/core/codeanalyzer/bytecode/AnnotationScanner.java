package ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarFile;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;

import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodDeclaration;
import ca.concordia.cs.aseg.sbson.core.codeanalyzer.MethodInvocation;

public class AnnotationScanner extends ClassVisitor {
    private String currentClass = null;
    private MethodDeclaration currentMethod = null;
    private HashMap<String, List<MethodDeclaration>> classMethodsInfo = null;

    public AnnotationScanner(int arg0) {
        super(arg0);
        classMethodsInfo = new HashMap<String, List<MethodDeclaration>>();
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
       // System.out.println(access + " " + name + " " + desc + " " + signature	+ " " + exceptions);
        if (!classMethodsInfo.containsKey(currentClass)) {
            classMethodsInfo.put(currentClass,
                    new ArrayList<MethodDeclaration>());
        }
        MethodVisitor impl = new MethodVisitor(Opcodes.ASM5) {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

                if(desc.equals("Ljava/lang/Deprecated;")){
                    classMethodsInfo.get(currentClass).add(currentMethod);
                    //System.out.println(currentClass+"\t"+currentMethod+"\t"+"visitAnnotation: desc="+desc+" visible="+visible);

                }
                return super.visitAnnotation(desc, visible);
            }
        };


        return impl;

    }

    public HashMap<String, List<MethodDeclaration>> getClassMethodsInfo() {
        return classMethodsInfo;
    }

    public void setClassMethodsInfo(
            HashMap<String, List<MethodDeclaration>> classMethodsInfo) {
        this.classMethodsInfo = classMethodsInfo;
    }


}
