package ca.concordia.cs.aseg.sbson.core.codeanalyzer;

import java.io.Serializable;
import java.util.Arrays;

import ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode.Converter;

public class MethodDeclaration implements Comparable<MethodDeclaration>,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int access;
	private String name;
	private String desc;
	private String signature;
	private String[] exceptions;
	private String className;

	public MethodDeclaration() {
		// TODO Auto-generated constructor stub
	}

	public MethodDeclaration(String className, int access, String name,
			String desc, String signature, String[] exceptions) {
		super();
		this.className = className;
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + access;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + Arrays.hashCode(exceptions);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((signature == null) ? 0 : signature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodDeclaration other = (MethodDeclaration) obj;
			if (access != other.access)
				return false;
			if (className == null) {
				if (other.className != null)
					return false;
			} else if (!className.equals(other.className))
				return false;
			if (desc == null) {
				if (other.desc != null)
					return false;
			} else if (!desc.equals(other.desc))
				return false;

			/*if (!Arrays.equals(exceptions, other.exceptions))
				return false;
*/
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (signature == null) {
				if (other.signature != null)
					return false;
			} else if (!signature.equals(other.signature))
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String[] getExceptions() {
		return exceptions;
	}

	public void setExceptions(String[] exceptions) {
		this.exceptions = exceptions;
	}

	@Override
	public String toString() {
		return "MethodDeclaration [className=" + className + ", access="
				+ access + ", name=" + name + ", desc=" + desc + ", signature="
				+ signature + "]";
	}

	
	public int compareTo(MethodDeclaration arg0) {
		if (this.equals(arg0))
			return 0;
		else
			return -1;
	}

	public String convertToString() {
		String methodAPI;
		Converter converter = Converter.getInstance();
		String returnType = null, parameters = null;
		int pos = desc.indexOf(")");
		returnType = desc.substring(pos + 1);
		returnType = converter.convertByteReturnType(returnType);
		parameters = desc.substring(1, pos);
		if(parameters.isEmpty()){
			parameters="()";
		}else{
		parameters = "("+converter
				.convertByteParameterType(Converter.getParameterList(parameters))+")";
		}
		if (name.equals("<init>")) {
			methodAPI = className + " " + access + " " + " "
					+ className.substring(className.lastIndexOf("/") + 1)
					+ parameters;
		} else {
			methodAPI = className + " " + access + " " + returnType + " "
					+ name + parameters;
		}

		return methodAPI;
	}

	

}
