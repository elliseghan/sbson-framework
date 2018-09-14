package ca.concordia.cs.aseg.sbson.core.codeanalyzer;

import java.util.List;

public class LicenseAnalyzer {

	public static String getLicenseFromString(String commentStr) {
		if (commentStr.contains("license"))
			return commentStr;
		else
			return null;
	}

	public static String getLicenseFromStringList(List<String> comments) {
		String license = null;
		for (String str : comments) {
			if (str.contains("license")) {
				license = str;
				break;
			}
		}
		return license;
	}

	public static String getLicenseFromComment(UMLComment comment) {
		if (comment.getCommentString().contains("license"))
			return comment.getCommentString();
		else
			return null;
	}

	public static String getLicenseFromCommentList(List<UMLComment> comments) {
		String license = null;
		for (UMLComment	comment : comments) {
			if (comment.getCommentString().contains("license")) {
				license = comment.getCommentString();
				break;
			}
		}
		return license;
	}
}
