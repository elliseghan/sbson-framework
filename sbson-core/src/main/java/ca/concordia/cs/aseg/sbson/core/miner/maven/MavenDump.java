package ca.concordia.cs.aseg.sbson.core.miner.maven;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ca.concordia.cs.aseg.sbson.core.Log;
import ca.concordia.cs.aseg.sbson.core.Utils;
import org.apache.commons.io.FileUtils;



public class MavenDump {

	public boolean initIndex() {

		ArtifactWriter artifactWriter = new ArtifactWriter();
		try {
			Log.print("Building artifact index");
			artifactWriter.writeAllArtifactInfo("allArtifact");
			if (ArtifactWriter.performWrite) {
				artifactWriter.getUniqueArtifacts("allArtifact", "uniqueArtifacts");
				Log.print("Compacting artifact index");
				artifactWriter.compactArtifacts("uniqueArtifacts", "allArtifactCompact");
			}
		} catch (IOException exception) {
			exception.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return ArtifactWriter.performWrite;

	}

	public void dumpFiles(String indexLoc, String dumpLoc) throws IOException {
		// System.out.println(dumpLoc);
		int pomCount = 0;
		int xmlCount = 0;
		int count = 0;
		/*
		 * File directory = new File(destinationPath); directory.mkdir();
		 */
		File indexFile = new File(indexLoc);
		if (!indexFile.exists()) {
			initIndex();
		}
		List<String> lines = FileUtils.readLines(indexFile);
		int totalLines = lines.size();

		System.out.println("Performing POM & XML dump for " + lines.size() + " entries.");
		for (String str : lines) {
			boolean res = dump(str, dumpLoc, "pom");
			if (res) {
				pomCount++;
			}
			res = dump(str, dumpLoc, "xml");
			if (res) {
				xmlCount++;
			}
			count++;
			if (count % 10000 == 0) {
				double percentProcessed = ((double) count / totalLines) * 100;
				System.out.println(percentProcessed + "% processed");
			}
			// break;
		}
		System.out.println(pomCount + "/" + lines.size() + " POM files were successfully downloaded");
		System.out.println(xmlCount + "/" + lines.size() + " XML files were successfully downloaded");
	}

	private boolean dump(String artifactGAV, String destinationPath, String extension) throws IOException {
		String url[] = Utils.createUrl(artifactGAV, extension, ":");
		return (Utils.getFileFromURL(url[0], destinationPath + url[1]) == true);
	}
}
