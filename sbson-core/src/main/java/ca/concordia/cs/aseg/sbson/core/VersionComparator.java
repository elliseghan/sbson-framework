package ca.concordia.cs.aseg.sbson.core;
import java.util.Comparator;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class VersionComparator implements Comparator<Object> {
	public int compare(Object o1, Object o2) {
		String s1 = (String) o1;
		String s2 = (String) o2;
		DefaultArtifactVersion v1 = new DefaultArtifactVersion(s1);
		DefaultArtifactVersion v2 = new DefaultArtifactVersion(s2);
		int result = 0;
		// a negative integer, zero, or a positive integer as the first
		// argument is less than, equal to, or greater than the second.
		if (v1.compareTo(v2) == 0) {
			result = 0;
		} else if (v1.compareTo(v2) < 0) {
			result = -1;
		} else if (v1.compareTo(v2) > 0) {
			result = 1;
		}
		return result;
	}

}
