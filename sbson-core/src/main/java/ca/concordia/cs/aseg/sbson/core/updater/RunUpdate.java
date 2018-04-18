package ca.concordia.cs.aseg.sbson.core.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import ca.concordia.cs.aseg.sbson.core.Utils;
import ca.concordia.cs.aseg.sbson.core.miner.maven.MavenDump;
import org.apache.commons.io.FileUtils;



public class RunUpdate {

	public static void updateFilesOnly() throws IOException {
		File tempIndex = new File("tempArtifactFile");
		File index = new File(Utils.MAVEN_INDEX_LOCATION + "uniqueArtifacts");
		FileUtils.copyFile(index, tempIndex);
		MavenDump mavenDump = new MavenDump();
		boolean updateHappened = mavenDump.initIndex();
		if (updateHappened) {
			index = null;
			index = new File(Utils.MAVEN_INDEX_LOCATION + "uniqueArtifacts");
			File diffList = RunUpdate.getDiff(tempIndex, index, Utils.MAVEN_INDEX_LOCATION + "diffList", 0);

			mavenDump.dumpFiles(diffList.getAbsolutePath(), Utils.POM_DUMP_LOCATION);			
		}
		FileUtils.deleteQuietly(tempIndex);
	}

	public static File updateAndCreateTriples() throws IOException {
		updateFilesOnly();
		// using the diffList, create triples for artifacts in list

		// delete diffList file

		return null;
	}

	private static File getDiff(File oldIndex, File newIndex, String saveLocation, int mode) {
		AbstractArtifactUpdater indexDiff = null;
		switch (mode) {
		case 0:
			indexDiff = new ArtifactIndexUpdater(oldIndex, newIndex);
			break;

		case 1:
			indexDiff = new ArtifactPOMUpdater(oldIndex, newIndex);
			break;
		}
		try {
			indexDiff.performDiff();

			System.out.println("Found new items:" + indexDiff.getAddedArtifacts().size());
			FileOutputStream fos = new FileOutputStream(saveLocation, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");

			for (String string : indexDiff.getAddedArtifacts()) {
				osw.write(string + "\n");
			}
			osw.close();

			return new File(saveLocation);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

}
