package ca.concordia.cs.aseg.sbson.core.parser.maven;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ca.concordia.cs.aseg.sbson.core.Utils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;


public class AdvancedPomParser extends PomParser {

	public AdvancedPomParser(Model model,String pomFilePath, String xmlFilePath) {
		super(model, pomFilePath, xmlFilePath);
	}

	@Override
	protected MavenProject buildProject(Model model) {
		MavenProject mavenProject = new MavenProject(model);
		if (model.getParent() != null) {
			Model parentModel = getModelFromParentFile(model.getParent());
			if (parentModel != null) {
				MavenProject project = buildProject(parentModel);
				// insert parent values into missing child slots
				if (project != null) {
					/*if (mavenProject.getGroupId() == null)
						mavenProject.setGroupId(project.getGroupId());
					if (mavenProject.getArtifactId() == null)
						mavenProject.setArtifactId(project.getArtifactId());
					if (mavenProject.getVersion() == null)
						mavenProject.setVersion(project.getVersion());*/

					List<Dependency>  dep= project.getDependencies();
					for(Dependency dependency:dep){
						mavenProject.getDependencies().add(dependency);
					}
					if(project.getDependencyManagement()!=null){
						List<Dependency>  depList= project.getDependencyManagement().getDependencies();
						for(Dependency dependency:depList){
							mavenProject.getDependencies().add(dependency);
						}
					}
					
					Properties properties = project.getProperties();
					Set<Object> keyset = properties.keySet();
					for (Object object : keyset) {
						if (!mavenProject.getProperties().containsKey(object)) {
							mavenProject.getProperties().put(object,
									properties.get(object));
						}
					}
				}
			}
		}
		return mavenProject;
	}

	/*
	 * @Override protected String getArtifactID(MavenProject project) { if
	 * (model.getArtifactId() == null) { return
	 * getParent(project).getArtifactId(); } else { return
	 * model.getArtifactId(); }
	 * 
	 * }
	 * 
	 * protected String getGroupID(MavenProject project) { if
	 * (model.getGroupId() == null) { return getParent(project).getGroupId(); }
	 * else { return model.getGroupId(); } }
	 * 
	 * protected String getVersion(MavenProject project) { if
	 * (model.getVersion() == null) { return getParent(project).getVersion(); }
	 * else { return model.getVersion(); } }
	 */
	private Model getModelFromParentFile(Parent parent) {

		String basePath = Utils.POM_DUMP_LOCATION;
		String groupID=parent.getGroupId();
		groupID=groupID.replace(".", "/");
		String filePath = basePath +groupID+"/"+parent.getArtifactId()+"/"+parent.getVersion()+"/"+ parent.getArtifactId() + "-"
				+ parent.getVersion() + ".pom";
		//System.out.println("getting parent from: " + filePath);
		File file = new File(filePath);
		if (file.exists()) {
			try {
				Model model = null;
				Reader reader = new FileReader(file);
				MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
				model = xpp3Reader.read(reader);
				reader.close();
				return model;
			} catch (Exception exception) {
				return null;
			}
		} else
			return null;
	}

}
