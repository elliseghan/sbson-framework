package ca.concordia.cs.aseg.sbson.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class Serializer {
	public static Object unserializeFile(String loc)
			throws ClassNotFoundException, IOException {
		return unserializeFile(new File(loc));
	}

	public static Object unserializeFile(File loc) throws IOException,
			ClassNotFoundException {
		Object object = null;

		InputStream file = new FileInputStream(loc);
		InputStream buffer = new BufferedInputStream(file);
		ObjectInput input = new ObjectInputStream(buffer);
		object = input.readObject();
		input.close();
		return object;
	}

	public static void serializeObject(Object object, String loc) {
		serializeObject(object, new File(loc));
	}

	public static void serializeObject(Object object, File loc) {
		try {
			OutputStream file = new FileOutputStream(loc);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(object);
			output.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
