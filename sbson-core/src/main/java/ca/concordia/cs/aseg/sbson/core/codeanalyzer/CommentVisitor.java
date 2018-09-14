package ca.concordia.cs.aseg.sbson.core.codeanalyzer;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.LineComment;

public class CommentVisitor extends ASTVisitor {
	CompilationUnit cu;
	String source;
	String comment;
 
	public CommentVisitor(CompilationUnit cu, String source) {
		super();
		this.cu = cu;
		this.source = source;
	}
 
	public boolean visit(LineComment node) {
		int start = node.getStartPosition();
		int end = start + node.getLength();
		String comment = source.substring(start, end);
		//System.out.println(comment);
		this.comment=comment;
		return true;
	}
 
	public boolean visit(BlockComment node) {
		int start = node.getStartPosition();
		int end = start + node.getLength();
		String comment = source.substring(start, end);
		//System.out.println(comment);
		this.comment=comment;
		return true;
	}
	
	public String getComment(){
		return this.comment;
	}
}
