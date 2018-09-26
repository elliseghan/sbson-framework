package ca.concordia.cs.aseg.sbson.ontologies.urigenerator.domain_specific.abox;

import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.namespace.NamespaceFactory;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.NamespaceRegistry;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.registry.OntologyRegistry;
import ca.concordia.cs.aseg.sbson.ontologies.urigenerator.utils.URLCleaner;

public class HistoryABox {

	public static String Branch(String branch) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace,
				OntologyRegistry.seon_history) + URLCleaner.clean(branch);
		return uri;
	}

	public static String ChangeSet(String repo, String commitID) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace,
				OntologyRegistry.seon_history) + URLCleaner.clean(repo + ":" + commitID);
		return uri;
	}

	public static String ChangeSet(String commitID, String committer, String commitDate) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace,
				OntologyRegistry.seon_history) + URLCleaner.clean(commitID + ":" + committer + ":" + commitDate);
		return uri;
	}

	public static String Commit(String commit) {
		if (commit.startsWith("http")) {
			return commit;
		} else {
			String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace,
					OntologyRegistry.seon_history) + URLCleaner.clean(commit);
			return uri;
		}
	}

	public static String Committer(String committer) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace,
				OntologyRegistry.seon_history) + URLCleaner.clean(committer);
		return uri;
	}

	public static String Version(String version) {
		if (version.startsWith("http")) {
			return version;
		} else {
			String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace,
					OntologyRegistry.seon_history) + URLCleaner.clean(version);
			return uri;
		}
	}

	public static String FileUnderVersionControl(String file) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theSEONAboxNameSpace,
				OntologyRegistry.seon_history) + URLCleaner.clean(file);
		return uri;
	}
}
