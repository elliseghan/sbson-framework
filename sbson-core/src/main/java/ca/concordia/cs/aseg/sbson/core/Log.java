package ca.concordia.cs.aseg.sbson.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Log {

	public static void print(Object... args) {

	}

	public static void print(String message) {
		System.out.println(message);
	}

	public static void save(String message) {

	}

	public static void printAndSave(String message) {
		print(message);
		save(message);
	}

	public static void logPomParseErrors(String file, String message) {
		try {
			FileOutputStream fos = new FileOutputStream(
					"src/main/resources/log.txt", true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(file + "::" + message + "\n");
			osw.close();
		} catch (IOException exception) {

		}
	}
}
