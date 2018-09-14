package ca.concordia.cs.aseg.sbson.core.codeanalyzer;

import java.io.Serializable;

public class MethodInvocation implements Serializable{

	private int opcode;
	private String owner;
	private String name;
	private String desc;
	private MethodDeclaration invocationSource;
public MethodInvocation() {
	// TODO Auto-generated constructor stub
}
	public MethodInvocation(int opcode, String owner, String name, String desc) {
		super();
		this.invocationSource = null;
		this.opcode = opcode;
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	public MethodInvocation(MethodDeclaration invocationSource, int opcode,
			String owner, String name, String desc) {
		super();
		this.invocationSource = invocationSource;
		this.opcode = opcode;
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime
				* result
				+ ((invocationSource == null) ? 0 : invocationSource.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + opcode;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodInvocation other = (MethodInvocation) obj;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (invocationSource == null) {
			if (other.invocationSource != null)
				return false;
		} else if (!invocationSource.equals(other.invocationSource))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (opcode != other.opcode)
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

	public int getOpcode() {
		return opcode;
	}

	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public MethodDeclaration getInvocationSource() {
		return invocationSource;
	}

	public void setInvocationSource(MethodDeclaration invocationSource) {
		this.invocationSource = invocationSource;
	}
	@Override
	public String toString() {
		return "MethodInvocation [opcode=" + opcode + ", owner=" + owner
				+ ", name=" + name + ", desc=" + desc + "]";
	}

}
