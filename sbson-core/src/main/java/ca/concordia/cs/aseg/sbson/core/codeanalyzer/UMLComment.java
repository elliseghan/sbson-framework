package ca.concordia.cs.aseg.sbson.core.codeanalyzer;

import java.io.Serializable;

public class UMLComment implements Comparable<UMLComment>, Serializable{

	private String commentString;
	@Override
	public int compareTo(UMLComment arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	public String getCommentString() {
		return commentString;
	}
	public void setCommentString(String commentString) {
		this.commentString = commentString;
	}
	
	

}
