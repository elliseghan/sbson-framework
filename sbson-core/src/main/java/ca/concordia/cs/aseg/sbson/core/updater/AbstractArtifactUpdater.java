package ca.concordia.cs.aseg.sbson.core.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public abstract class AbstractArtifactUpdater {
	protected List<String> addedArtifacts, deletedArtifacts;
	protected TreeSet<String> oldIndexSet, newIndexSet;

	public AbstractArtifactUpdater() {
		addedArtifacts = new ArrayList<String>();
		deletedArtifacts = new ArrayList<String>();
	}


	/*
	 * The Maven Central Repository provides cconstraints/guidelines as to
	 * how published artifacts can be managed. Once an artifact is published
	 * to the central repository, it can not be modified but only removed.
	 * New additions/modifications come by as newer versions of the project
	 * in question.
	 * 
	 * Heurestic/Assumption Used: 
	 * 1. Modifications not possible 
	 * 2. Deletions are ignored since other dependent
	 * projects will still be using the deleted artifacts 
	 * 3. Only new additions to the repository are considered.
	 */
	protected abstract void performDiff();
	
	public List<String> getAddedArtifacts() {
				return addedArtifacts;
	}

	public List<String> getRemovedArtifacts() {
	
		return deletedArtifacts;
	}

	protected TreeSet<String> getStringSetFromFile(File file) {
		System.out.println("Building set from index file: "+file.getAbsolutePath());
		TreeSet<String> set = new TreeSet<String>();
		try {
			FileReader reader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				set.add(line);
			}
			bufferedReader.close();
		} catch (Exception ex) {
		}
		System.out.println("\t Set Size: " + set.size());
		return set;
	}

/*	protected TreeSet<String> getStringSetFromPomLocation(File file) {
		System.out.println("Building set from pom file location...:"+file.getAbsolutePath());
		TreeSet<String> set = new TreeSet<String>();
		String[] extensions = { "pom" };
		Collection<File> files = FileUtils.listFiles(file, extensions, true);
		System.out.println("\t Files Found: " + files.size());
		for (File f : files) {
			String[] gav = Utils.getGAVFromFileName(f.getAbsolutePath());
			if (gav != null) {
				String value = gav[0] + ":" + gav[1] + ":" + gav[2];
				set.add(value);
			}
			f=null;
		}
		System.out.println("\t Set Size: " + set.size());
		return set;
	}*/

}
