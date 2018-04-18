package ca.concordia.cs.aseg.sbson.core;

import java.util.Collections;
import java.util.List;

public class VersionNumberUtils {

	public static void compare(String v1, String v2) {
		System.out.println("comparing: " + v1 + " & " + v2 + " = " + new VersionComparator().compare(v1, v2));

	}

	public static boolean isBefore(String v1, String v2) {
		if (new VersionComparator().compare(v1, v2) < 0) {
			System.out.println("isBefore: " + v1 + " & " + v2 + " = true");
			return true;
		} else {
			System.out.println("isBefore: " + v1 + " & " + v2 + " = false");
			return false;
		}
	}

	public static boolean isAfter(String v1, String v2) {
		if (new VersionComparator().compare(v1, v2) > 0) {
			System.out.println("isAfter: " + v1 + " & " + v2 + " = true");
			return true;
		} else {
			System.out.println("isAfter: " + v1 + " & " + v2 + " = false");
			return false;
		}
	}

	public static boolean isInRange(String v, String v1, String v2) {
		if (new VersionComparator().compare(v, v1) >= 0 && new VersionComparator().compare(v, v2) <= 0) {
			System.out.println(v + " isInRange: [" + v1 + "," + v2 + "] = true");
			return true;
		} else {
			System.out.println(v + " isInRange: [" + v1 + "," + v2 + "] = false");
			return false;
		}
	}

	public static void sort(List<String> vList) {
		Collections.sort(vList, new VersionComparator());
	}

	private void testComparison() {
		VersionNumberUtils.compare("1.7.1", "1.7.10");
		VersionNumberUtils.compare("1.7.2", "1.7.10");
		VersionNumberUtils.compare("1.6.1", "1.7.10");
		VersionNumberUtils.compare("1.6.20", "1.7.10");
		VersionNumberUtils.compare("1.7.1", "1.7.10");
		VersionNumberUtils.compare("1.7", "1.7.0");
		VersionNumberUtils.compare("1.7", "1.8.0");
		VersionNumberUtils.compare("1.7.2", "1.7.10b");
		VersionNumberUtils.compare("1.7.10", "1.7.1");
		VersionNumberUtils.compare("1.7.10", "1.6.1");
		VersionNumberUtils.compare("1.7.10", "1.6.20");
		VersionNumberUtils.compare("1.7.0", "1.7");
		VersionNumberUtils.compare("1.8.0", "1.7");
		VersionNumberUtils.compare("1.7.10", "1.7.10");
		VersionNumberUtils.compare("1.7", "1.7");
		VersionNumberUtils.compare("1.7", "1.7.0");
		System.out.println("All done");
	}

	private void testIsBefore() {
		VersionNumberUtils.isBefore("1.7.1", "1.7.10");
		VersionNumberUtils.isBefore("1.7.2", "1.7.10");
		VersionNumberUtils.isBefore("1.6.1", "1.7.10");
		VersionNumberUtils.isBefore("1.6.20", "1.7.10");
		VersionNumberUtils.isBefore("1.7.1", "1.7.10");
		VersionNumberUtils.isBefore("1.7", "1.7.0");
		VersionNumberUtils.isBefore("1.7", "1.8.0");
		VersionNumberUtils.isBefore("1.7.2", "1.7.10b");
		VersionNumberUtils.isBefore("1.7.10", "1.7.1");
		VersionNumberUtils.isBefore("1.7.10", "1.6.1");
		VersionNumberUtils.isBefore("1.7.10", "1.6.20");
		VersionNumberUtils.isBefore("1.7.0", "1.7");
		VersionNumberUtils.isBefore("1.8.0", "1.7");
		VersionNumberUtils.isBefore("1.7.10", "1.7.10");
		VersionNumberUtils.isBefore("1.7", "1.7");
		VersionNumberUtils.isBefore("1.7", "1.7.0");
		System.out.println("All done");
	}

	private void testIsInRange() {
		VersionNumberUtils.isInRange("1.7.4.6", "1.7.1", "1.7.10");
		VersionNumberUtils.isInRange("1.7.2.0", "1.7.2", "1.7.10");
		VersionNumberUtils.isInRange("1.7.10-RELEASE", "1.6.1", "1.7.10");
		VersionNumberUtils.isInRange("1.5", "1.6.20", "1.7.10");
		VersionNumberUtils.isInRange("1.4.5.3", "1.7.1", "1.7.10");
		VersionNumberUtils.isInRange("1.7.5", "1.8.0", "1.7");
		System.out.println("All done");
	}

	private void testIsAfter() {
		VersionNumberUtils.isAfter("1.7.1", "1.7.10");
		VersionNumberUtils.isAfter("1.7.2", "1.7.10");
		VersionNumberUtils.isAfter("1.6.1", "1.7.10");
		VersionNumberUtils.isAfter("1.6.20", "1.7.10");
		VersionNumberUtils.isAfter("1.7.1", "1.7.10");
		VersionNumberUtils.isAfter("1.7", "1.7.0");
		VersionNumberUtils.isAfter("1.7", "1.8.0");
		VersionNumberUtils.isAfter("1.7.2", "1.7.10b");
		VersionNumberUtils.isAfter("1.7.10", "1.7.1");
		VersionNumberUtils.isAfter("1.7.10", "1.6.1");
		VersionNumberUtils.isAfter("1.7.10", "1.6.20");
		VersionNumberUtils.isAfter("1.7.0", "1.7");
		VersionNumberUtils.isAfter("1.8.0", "1.7");
		VersionNumberUtils.isAfter("1.7.10", "1.7.10");
		VersionNumberUtils.isAfter("1.7", "1.7");
		VersionNumberUtils.isAfter("1.7", "1.7.0");
		System.out.println("All done");
	}

}
