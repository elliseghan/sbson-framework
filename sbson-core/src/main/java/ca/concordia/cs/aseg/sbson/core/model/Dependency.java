package ca.concordia.cs.aseg.sbson.core.model;

import java.util.ArrayList;
import java.util.List;



public class Dependency {

	private String groupID, artifactID, version, scope, type;
	private boolean optional=false;
	private List<String> exclusions;

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getArtifactID() {
		return artifactID;
	}

	public void setArtifactID(String artifactID) {
		this.artifactID = artifactID;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public List<String> getExclusions() {
		if(exclusions==null)
			return new ArrayList<String>();
		return exclusions;
	}

	public void setExclusions(List<String> exclusions) {
		this.exclusions = exclusions;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		String string = groupID + ":" + artifactID + ":" + version;
		return string;
	}
}
