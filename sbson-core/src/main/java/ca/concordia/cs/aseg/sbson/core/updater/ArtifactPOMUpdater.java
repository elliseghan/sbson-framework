package ca.concordia.cs.aseg.sbson.core.updater;

import ca.concordia.cs.aseg.sbson.core.Utils;

import java.io.File;



public class ArtifactPOMUpdater extends AbstractArtifactUpdater {

	public ArtifactPOMUpdater(File oldLocation, File newLocation) {
		super();
		this.newIndexSet = getStringSetFromFile(newLocation);
	}

	@Override
	protected void performDiff() {
		System.out.println("Performing Diff action...");
		this.addedArtifacts.clear();
		for (String artifact : newIndexSet) {
			File pom = Utils.getFileFromGAV(artifact, ":", "pom");
			if (!pom.exists()) {
				addedArtifacts.add(artifact);
			}
		}

	}

}
