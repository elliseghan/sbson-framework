package ca.concordia.cs.aseg.sbson.core.parser.maven;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

/*
 * Default parser for POM file without a parent
 */
public class DefaultPomParser extends PomParser {

	public DefaultPomParser(Model model,String pomFilePath, String xmlFilePath) {
		super(model, pomFilePath, xmlFilePath);
	}

	@Override
	protected MavenProject buildProject(Model model) {
		MavenProject mavenProject = new MavenProject(model);
		return mavenProject;
	}

}
