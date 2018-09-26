package ca.concordia.cs.aseg.sbson.ontologies.urigenerator.domain_specific.tbox;

import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.namespace.NamespaceFactory;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.NamespaceRegistry;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.OntologyRegistry;

public class IssuesTBox {

	/**
	 * Concepts
	 */
	public static String Assignee() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Assignee";
		return uri;
	}
	
	public static String Attachment() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Attachment";
		return uri;
	}
	
	public static String Bug() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Bug";
		return uri;
	}
	
	public static String Comment() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Comment";
		return uri;
	}
	
	public static String Enhancement() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Enhancement";
		return uri;
	}
	
	public static String FeatureAddition() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "FeatureAddition";
		return uri;
	}
	
	public static String FeatureRequest() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "FeatureRequest";
		return uri;
	}
	
	public static String Improvement() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Improvement";
		return uri;
	}
	
	public static String Issue() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Issue";
		return uri;
	}
	
	public static String Priority() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Priority";
		return uri;
	}
	
	public static String Reporter() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Reporter";
		return uri;
	}
	
	public static String Resolution() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Resolution";
		return uri;
	}
	
	public static String Severity() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Severity";
		return uri;
	}
	
	public static String Status() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "Status";
		return uri;
	}
	
	/**
	 * OBJECT PROPERTIES
	 */
	public static String blocksIssue() {
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "blocksIssue";
		return uri;
	}
	
	public static String commentsIssue(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "commentsIssue";
		return uri;
	}
	
	public static String dependsOnIssue(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "dependsOnIssue";
		return uri;
	}
	
	public static String hasAssignee(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasAssignee";
		return uri;
	}
	
	public static String hasAttachment(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasAttachment";
		return uri;
	}
	
	public static String hasComment(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasComment";
		return uri;
	}
	
	public static String hasDuplicate(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasDuplicate";
		return uri;
	}
	
	public static String hasPriority(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasPriority";
		return uri;
	}
	
	public static String hasReporter(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasReporter";
		return uri;
	}
	
	public static String hasResolution(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasResolution";
		return uri;
	}
	
	public static String hasSeverity(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasSeverity";
		return uri;
	}
	
	public static String hasStatus(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasStatus";
		return uri;
	}
	
	public static String isAssigneeOf(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "isAssigneeOf";
		return uri;
	}
	
	public static String isAttachementOf(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "isAttachementOf";
		return uri;
	}
	
	public static String isBlockedBy(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "isBlockedBy";
		return uri;
	}
	
	public static String isCommentedBy(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "isCommentedBy";
		return uri;
	}
	
	public static String isCommentOf(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "isCommentOf";
		return uri;
	}
	
	public static String isReporterOf(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "isReporterOf";
		return uri;
	}
	
	/**
	 * DATA PROPERTIES
	 */
	public static String hasActualEffort(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasActualEffort";
		return uri;
	}
	
	public static String hasCommentText(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasCommentText";
		return uri;
	}
	
	public static String hasEstimatedEffort(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasEstimatedEffort";
		return uri;
	}
	
	public static String hasIssueNumber(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasIssueNumber";
		return uri;
	}
	
	public static String hasResolutionDate(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasResolutionDate";
		return uri;
	}
	
	public static String hasTitle(){
		String uri = NamespaceFactory.createTboxNamespace(NamespaceRegistry.theSEONTboxNameSpace, OntologyRegistry.seon_issues)
				+ "hasTitle";
		return uri;
	}
}

