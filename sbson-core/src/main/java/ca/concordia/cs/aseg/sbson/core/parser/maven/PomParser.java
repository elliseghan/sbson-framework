package ca.concordia.cs.aseg.sbson.core.parser.maven;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import ca.concordia.cs.aseg.sbson.core.Utils;
import ca.concordia.cs.aseg.sbson.core.model.Dependency;
import ca.concordia.cs.aseg.sbson.core.model.License;
import ca.concordia.cs.aseg.sbson.core.model.MavenArtifact;
import org.apache.commons.lang3.StringUtils;

import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXException;


public abstract class PomParser {

    // protected File pomFile;
    protected Model model;
    protected String[] GAV;
    protected String xmlFilePath;

    public PomParser(Model model, String pomFilePath, String xmlFilePath) {
        this.model = model;
        GAV = Utils.getGAVFromFileName(pomFilePath);
        this.xmlFilePath = xmlFilePath;
    }

    protected String getArtifactID(MavenProject project) {
        if (GAV != null)
            return GAV[1];
        else
            return getNormalizedVariable(this.model, project.getArtifactId());
    }

    protected String getGroupID(MavenProject project) {
        if (GAV != null)
            return GAV[0];
        else
            return getNormalizedVariable(this.model, project.getGroupId());
    }

    protected String getVersion(MavenProject project) {
        if (GAV != null)
            return GAV[2];
        else
            return getNormalizedVariable(this.model, project.getVersion());
    }

    private List<Dependency> getDependencies(MavenProject project) {
        List<Dependency> artifacts = new ArrayList<Dependency>();
        Dependency artifact = null;
        if (project.getDependencyManagement() != null) {
            for (org.apache.maven.model.Dependency dependency : project.getDependencyManagement().getDependencies()) {
                artifact = new Dependency();
                artifact.setArtifactID(getNormalizedVariable(model, dependency.getArtifactId()));
                artifact.setGroupID(getNormalizedVariable(model, dependency.getGroupId()));
                artifact.setVersion(getNormalizedVariable(model, dependency.getVersion()));
                artifact.setOptional(dependency.isOptional());
                artifact.setScope(dependency.getScope());
                artifact.setType(dependency.getType());
                if (!dependency.getExclusions().isEmpty()) {
                    List<String> depExcls = new ArrayList<String>();
                    for (Exclusion exclusion : dependency.getExclusions()) {
                        String depExcl = exclusion.getGroupId() + ":" + exclusion.getArtifactId();
                        depExcls.add(depExcl);
                    }
                    artifact.setExclusions(depExcls);
                }

                if (artifact.getVersion() != null)
                    artifacts.add(artifact);
            }
        }
        for (org.apache.maven.model.Dependency dependency : project.getDependencies()) {
            artifact = new Dependency();
            artifact.setArtifactID(getNormalizedVariable(model, dependency.getArtifactId()));
            artifact.setGroupID(getNormalizedVariable(model, dependency.getGroupId()));
            artifact.setVersion(getNormalizedVariable(model, dependency.getVersion()));
            artifact.setOptional(dependency.isOptional());
            artifact.setScope(dependency.getScope());
            artifact.setType(dependency.getType());
            if (!dependency.getExclusions().isEmpty()) {
                List<String> depExcls = new ArrayList<String>();
                for (Exclusion exclusion : dependency.getExclusions()) {
                    String depExcl = exclusion.getGroupId() + ":" + exclusion.getArtifactId();
                    depExcls.add(depExcl);
                }
                artifact.setExclusions(depExcls);
            }

            if (artifact.getVersion() != null)
                artifacts.add(artifact);
        }
        return artifacts;
    }

    private String getReleaseDate() throws SAXException, IOException, ParserConfigurationException {
        if (xmlFilePath == null) {
            return null;
        }
        String requestQueueName = Utils.getTagValueFromXML(new File(xmlFilePath), "long");
        if (requestQueueName != null) {
            long lng = Long.valueOf(requestQueueName);
            LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(lng), ZoneId.systemDefault());
            System.out.println(ldt);
            return ldt.toString();
        } else {
            return null;
        }
    }

    private String getName(MavenProject project) {
        if (project.getName() != null) {
            return project.getName();
        } else {
            return null;
        }
    }

    private String getDescription(MavenProject project) {
        if (project.getDescription() != null) {
            return project.getDescription();
        } else {
            return null;
        }
    }

    private String[] getOrganization(MavenProject project) {
        if (project.getOrganization() != null) {
            String[] organization = {project.getOrganization().getName(), project.getOrganization().getUrl()};
            return organization;
        } else {
            return null;
        }
    }

    private List<License> getLicenses(MavenProject project) {
        List<License> licenses = new ArrayList<>();
        License mavenLicense = null;
        for (org.apache.maven.model.License license : project.getLicenses()) {
            mavenLicense = new License(license.getName(), license.getUrl(), license.getComments(),
                    license.getDistribution());
            licenses.add(mavenLicense);
        }
        return licenses;
    }

    protected abstract MavenProject buildProject(Model model);

    protected MavenArtifact getParent(MavenProject project) {
        MavenArtifact artifact = new MavenArtifact();
        Parent parent = project.getModel().getParent();
        if (parent == null) {
            return null;
        } else {
            artifact.setArtifactId(parent.getArtifactId());
            artifact.setGroupId(parent.getGroupId());
            artifact.setVersion(parent.getVersion());
            return artifact;
        }
    }

    public MavenArtifact parse() {
        MavenProject mavenProject = buildProject(this.model);
        MavenArtifact artifact = null;

        artifact = new MavenArtifact();
        artifact.setArtifactId(getArtifactID(mavenProject));
        artifact.setGroupId(getGroupID(mavenProject));
        artifact.setVersion(getVersion(mavenProject));
        artifact.setParent(getParent(mavenProject));
        artifact.setDependencies(getDependencies(mavenProject));
        artifact.setModules(mavenProject.getModules());
        artifact.setDescription(getDescription(mavenProject));
        artifact.setName(getName(mavenProject));
        String[] org = getOrganization(mavenProject);
        if (org != null) {
            artifact.setOrganizationName(org[0]);
            artifact.setOrganizationURL(org[1]);
        }
        try {
            artifact.setReleasedDate(getReleaseDate());
        } catch (Exception exception) {

        }
        List<License> licenses = getLicenses(mavenProject);
        artifact.setLicenses(licenses);
        return artifact;
    }

    protected boolean isVariable(String value) {
        boolean result = false;
        if (value != null) {
            // check if variable
            if (value.contains("${")) {
                result = true;
            }
        }
        return result;
    }

    protected String normalizeVariable(Model model, String variable) {
        String varString = "";
        variable = variable.replace("${", "");
        variable = variable.replace("}", "");

        if (variable.startsWith("project")) {
            // Normalize project variables ${project.version}
            if (StringUtils.containsIgnoreCase(variable, "version")) {
                varString = model.getVersion();
            } else if (StringUtils.containsIgnoreCase(variable, "groupID")) {
                varString = model.getGroupId();
            } else if (StringUtils.containsIgnoreCase(variable, "artifactId")) {
                varString = model.getArtifactId();
            }
        } else if (variable.startsWith("java")) {
            // Normalize java variables - not implemented
        } else if (variable.startsWith("env")) {
            // Normalize system environment variables - not implemented
        } else {
            // Normalize user and parent variables
            Properties props = model.getProperties();
            if (props.containsKey(variable))
                varString = props.getProperty(variable.trim());
            else
                varString = "var";
        }
        return varString;
    }

    protected String getNormalizedVariable(Model model, String variableString) {
        if (isVariable(variableString)) {
            return normalizeVariable(model, variableString);
        } else {
            return variableString;
        }
    }

}
