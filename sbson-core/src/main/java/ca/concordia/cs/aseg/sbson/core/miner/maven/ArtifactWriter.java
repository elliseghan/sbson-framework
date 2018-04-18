package ca.concordia.cs.aseg.sbson.core.miner.maven;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ca.concordia.cs.aseg.sbson.core.Log;
import ca.concordia.cs.aseg.sbson.core.Utils;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.artifact.Gav;



public class ArtifactWriter {

	static int totalArtifactsNumber;
	static boolean performWrite;

	public void writeAllArtifactInfo(String fileName) {
		try {
			CentralIndex app = new CentralIndex();
			boolean updateHappened = app.buildCentralIndex();
			performWrite = updateHappened;
			if (updateHappened) {

				totalArtifactsNumber = app.allArtifactSize();
				System.out.println(totalArtifactsNumber);
				int subListsNumber = 100;
				int count = 0, error = 0;
				List<ArtifactInfo> partialArtifact;
				FileWriter fw = new FileWriter(Utils.MAVEN_INDEX_LOCATION+fileName, false);
				BufferedWriter bw = new BufferedWriter(fw);
				Gav gav;
				for (int index = 0; index < subListsNumber; index++) {
					partialArtifact = app.partialArtifactInfo(
							(int) (((long) (totalArtifactsNumber * index)) / subListsNumber),
							Math.min((int) (((long) (totalArtifactsNumber * (index + 1))) / subListsNumber),
									(int) totalArtifactsNumber));
					Log.print("Storing artifacts from index " + ((totalArtifactsNumber * index) / subListsNumber)
							+ " to "
							+ (Math.min((totalArtifactsNumber * (index + 1)) / subListsNumber, totalArtifactsNumber)));
					String text = "";

					for (ArtifactInfo ai : partialArtifact) {
						try {
							gav = ai.calculateGav();
							text += gav.getGroupId() + ":" + gav.getArtifactId() + ":" + gav.getVersion()
									+ System.getProperty("line.separator");
							count++;
						} catch (Exception ex) {
							error++;
						}
					}
					bw.write(text);
					bw.flush();

				}
				bw.close();

				Log.print("count: {}, error: {}", count, error);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writeAllArtifactInfo(String fileName, int startPosition, int endPosition) {
		try {
			CentralIndex app = new CentralIndex();
			app.buildCentralIndex();
			totalArtifactsNumber = endPosition - startPosition + 1;
			int subListsNumber = 100000;
			int count = 0, error = 0;
			List<ArtifactInfo> partialArtifact;
			FileWriter fw = new FileWriter(fileName, false);
			BufferedWriter bw = new BufferedWriter(fw);
			Gav gav;
			for (int index = 0; index < subListsNumber; index++) {
				partialArtifact = app.partialArtifactInfo(
						(int) (((long) (totalArtifactsNumber * index)) / subListsNumber),
						Math.min((int) (((long) (totalArtifactsNumber * (index + 1))) / subListsNumber),
								(int) totalArtifactsNumber));
				/*
				 * Log.debug( "Storing artifacts from index {} to {}",
				 * (totalArtifactsNumber * index) / subListsNumber,
				 * Math.min((totalArtifactsNumber * (index + 1)) /
				 * subListsNumber, totalArtifactsNumber));
				 */
				String text = "";
				for (ArtifactInfo ai : partialArtifact) {
					try {
						gav = ai.calculateGav();
						text += gav.getGroupId() + ":" + gav.getArtifactId() + ":" + gav.getVersion()
								+ System.getProperty("line.separator");
						count++;
					} catch (Exception ex) {
						error++;
					}
				}
				bw.write(text);
				bw.flush();
			}
			bw.close();

			Log.print("count: {}, error: {}", count, error);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void getUniqueArtifacts(String artifactFileName, String outputFileName) throws IOException {
		FileReader fr = new FileReader(Utils.MAVEN_INDEX_LOCATION+artifactFileName);
		BufferedReader br = new BufferedReader(fr);
		Set<String> artifacts = new TreeSet<String>();
		String line;
		while ((line = br.readLine()) != null) {
			artifacts.add(line);
		}
		br.close();
		FileWriter fw = new FileWriter(Utils.MAVEN_INDEX_LOCATION+outputFileName, false);
		BufferedWriter bw = new BufferedWriter(fw);
		for (String artifact : artifacts) {
			bw.write(artifact + System.getProperty("line.separator"));
		}
		bw.close();
	}

	public void compactArtifacts(String artifactFileName, String outputFileName) throws IOException {
		FileReader fr = new FileReader(Utils.MAVEN_INDEX_LOCATION+artifactFileName);
		BufferedReader br = new BufferedReader(fr);
		Set<String> artifacts = new TreeSet<String>();
		String line;
		while ((line = br.readLine()) != null) {
			artifacts.add(line.split(":")[0] + ":" + line.split(":")[1]);
		}
		br.close();
		FileWriter fw = new FileWriter(Utils.MAVEN_INDEX_LOCATION+outputFileName, false);
		BufferedWriter bw = new BufferedWriter(fw);
		for (String artifact : artifacts) {
			bw.write(artifact + System.getProperty("line.separator"));
		}
		bw.close();
	}

	@SuppressWarnings("resource")
	public void renameArtifacts(String dependencyTableFileName, String usageTableFileName,
			String outputDependencyFileName, String outputUsageFileName) throws IOException { // dependencies
		FileReader fr =

		new FileReader(dependencyTableFileName);
		BufferedReader br = new BufferedReader(fr);
		Map<String, String> servicesById = new HashMap<String, String>();
		String line;
		String[] splitLine;
		int counter = 0;
		while ((line = br.readLine()) != null) {
			splitLine = line.split(",");
			servicesById.put(splitLine[0], "s" + counter);
			counter++;
			for (int i = 2; i < splitLine.length; i++) {
				if (!servicesById.containsKey(splitLine[i])) {
					servicesById.put(splitLine[i], "s" + counter);
					counter++;
				}
			}
		}
		br.close(); // usages
		int depCounter = counter;
		Log.print("Found {} dependency nodes", depCounter);
		fr = new FileReader(usageTableFileName);
		br = new BufferedReader(fr);
		while ((line = br.readLine()) != null) {
			splitLine = line.split(",");
			if (!servicesById.containsKey(splitLine[0])) {
				servicesById.put(splitLine[0], "s" + counter);
				counter++;
			}
			for (int i = 2; i < splitLine.length; i++) {
				if (!servicesById.containsKey(splitLine[i])) {
					servicesById.put(splitLine[i], "s" + counter);
					counter++;
				}
			}
		}
		Log.print("Found {} usage nodes", counter - depCounter);
		br.close();
		// rewrite
		fr = new FileReader(dependencyTableFileName);
		br = new BufferedReader(fr);
		FileWriter fw = new FileWriter(outputDependencyFileName);
		BufferedWriter bw = new BufferedWriter(fw);
		while ((line = br.readLine()) != null) {
			splitLine = line.split(",");
			bw.write(servicesById.get(splitLine[0]) + "," + splitLine[1] + ",");
			for (int i = 2; i < splitLine.length; i++) {
				bw.write(servicesById.get(splitLine[i]) + ",");
			}
			bw.write(System.getProperty("line.separator"));
		}
		bw.close();
		fr = new FileReader(usageTableFileName);
		br = new BufferedReader(fr);
		fw = new FileWriter(outputUsageFileName);
		bw = new BufferedWriter(fw);
		while ((line = br.readLine()) != null) {
			splitLine = line.split(",");
			bw.write(servicesById.get(splitLine[0]) + "," + splitLine[1] + ",");
			for (int i = 2; i < splitLine.length; i++) {
				bw.write(servicesById.get(splitLine[i]) + ",");
			}
			bw.write(System.getProperty("line.separator"));
		}
		bw.close();
	}
}
