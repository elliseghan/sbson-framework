package ca.concordia.cs.aseg.sbson.core.model;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import ca.concordia.cs.aseg.sbson.core.Log;
import ca.concordia.cs.aseg.sbson.core.Utils;
import ca.concordia.cs.aseg.sbson.core.parser.maven.AdvancedPomParser;
import ca.concordia.cs.aseg.sbson.core.parser.maven.DefaultPomParser;
import ca.concordia.cs.aseg.sbson.core.parser.maven.PomParser;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;


public class MavenArtifact extends IArtifact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupId;
	private MavenArtifact parent;
	private List<String> modules;

	public MavenArtifact() {
	}

	public MavenArtifact(String groupId, String artifactId, String version) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	@Override
	public String toString() {
		String string = groupId + ":" + artifactId + ":" + version;
		return string;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		MavenArtifact other = (MavenArtifact) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public File getArtifactFile(ArtifactFileTypes artifactFileType) {
		return null;
	}

	@Override
	public IArtifact getArtifactFromCordinate(ArtifactTypes artifactType, String cordinate) {
		return null;
	}

	@Override
	public IArtifact getArtifactFromFile(File file) {
		return null;
	}

	public boolean partialMatch(MavenArtifact artifact) {
		return false;
	}

	public File getJARFile() {

		/*
		 * 1. Check local maven repository for jar 2. If not found, get jar from
		 * central repository into temp folder
		 */
		File jarFile = Utils.getJarFromShortURI(this.toString());
		return jarFile;
		/*
		 * String[] paths = Utils.createUrl(this.toString(), "jar", "_"); String
		 * url = paths[0]; String localRepoLoc = Utils.LOCAL_MAVEN_REPO +
		 * paths[1];
		 * 
		 * File jar = null; jar = new File(localRepoLoc); if (jar.exists()) {
		 * return jar; } else { boolean success = Utils.getFileFromURL(url,
		 * "src/main/resources/tempJars/" + paths[1]); if (success) return new
		 * File("src/main/resources/tempJars/" + paths[1]); else return null; }
		 */

	}

	public static MavenArtifact getArtifactFromGAV(String gav) {
		File pomFile = Utils.getFileFromGAV(gav, ":", "pom");
		File xmlFile = Utils.getFileFromGAV(gav, ":", "xml");
		return getArtifactFromPOM(pomFile, xmlFile);

	}

	public static MavenArtifact getArtifactFromPOM(File pomFile, File xmlFile) {
		MavenArtifact mavenArtifact = null;
		Model mavenProjectModel = null;
		if (!pomFile.exists())
			return null;
		try {
			Reader reader = new FileReader(pomFile);
			MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
			mavenProjectModel = xpp3Reader.read(reader);
			reader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.logPomParseErrors(pomFile.getAbsolutePath(), exception.getMessage());
		}
		if (mavenProjectModel != null) {
			PomParser parser = null;
			String xmlFilePath = null;
			if (xmlFile != null) {
				xmlFilePath = xmlFile.getAbsolutePath();
			}
			if (mavenProjectModel.getParent() != null) {
				// use advanced parser
				parser = new AdvancedPomParser(mavenProjectModel, pomFile.getAbsolutePath(), xmlFilePath);
			} else {
				// use default parser
				parser = new DefaultPomParser(mavenProjectModel, pomFile.getAbsolutePath(), xmlFilePath);
			}
			mavenArtifact = parser.parse();
		}
		return mavenArtifact;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public MavenArtifact getParent() {
		return parent;
	}

	public void setParent(MavenArtifact parent) {
		this.parent = parent;
	}

	public List<String> getModules() {
		return modules;
	}

	public void setModules(List<String> modules) {
		this.modules = modules;
	}
}
