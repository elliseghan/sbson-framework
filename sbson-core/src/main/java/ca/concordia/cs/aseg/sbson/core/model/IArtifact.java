package ca.concordia.cs.aseg.sbson.core.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;


public abstract class IArtifact implements Serializable {

    public String name = null, description = null, organizationName = null, organizationURL = null;
    public String artifactId = null;
    public String version = null;
    public List<Dependency> dependencies = null;
    public String releasedDate = null;
    public List<License> licenses = null;

    public static enum ArtifactTypes {MAVEN, RUBYGEM, NPM}

    public static enum ArtifactFileTypes {POM, JAR, XML, JSON}

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    public abstract File getArtifactFile(ArtifactFileTypes artifactFileType);

    public abstract IArtifact getArtifactFromCordinate(ArtifactTypes artifactType, String cordinate);

    public abstract IArtifact getArtifactFromFile(File file);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationURL() {
        return organizationURL;
    }

    public void setOrganizationURL(String organizationURL) {
        this.organizationURL = organizationURL;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }

    public List<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }
}
