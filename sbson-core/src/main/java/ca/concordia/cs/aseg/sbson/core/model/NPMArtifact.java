package ca.concordia.cs.aseg.sbson.core.model;

import java.io.File;

public class NPMArtifact extends IArtifact {
    @Override
    public String toString() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
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
}
