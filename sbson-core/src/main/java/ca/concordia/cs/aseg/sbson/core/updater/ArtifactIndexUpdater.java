package ca.concordia.cs.aseg.sbson.core.updater;

import java.io.File;

public class ArtifactIndexUpdater extends AbstractArtifactUpdater{

	public ArtifactIndexUpdater(File oldLocation, File newLocation) {
		super();
		this.oldIndexSet = getStringSetFromFile(oldLocation);
		this.newIndexSet = getStringSetFromFile(newLocation);
	}

	@Override
	protected void performDiff() {
		this.addedArtifacts.clear();
		for(String artifact:newIndexSet){
			//check if artifact exists in old index
			if(!oldIndexSet.contains(artifact)){
				//get artifact into local dataset
				
				//keep track of newly added artifacts
				addedArtifacts.add(artifact);
			}
		}
		
	}

}
