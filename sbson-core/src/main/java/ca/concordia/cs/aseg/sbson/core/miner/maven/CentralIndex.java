package ca.concordia.cs.aseg.sbson.core.miner.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.concordia.cs.aseg.sbson.core.Utils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.Bits;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.Indexer;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexUtils;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.updater.IndexUpdateRequest;
import org.apache.maven.index.updater.IndexUpdateResult;
import org.apache.maven.index.updater.IndexUpdater;
import org.apache.maven.index.updater.ResourceFetcher;
import org.apache.maven.index.updater.WagonHelper;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.observers.AbstractTransferListener;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;




public class CentralIndex {

	private final PlexusContainer plexusContainer;

	private final Indexer indexer;

	private final IndexUpdater indexUpdater;

	private final Wagon httpWagon;

	private IndexingContext centralContext;

	public CentralIndex() throws PlexusContainerException,
			ComponentLookupException {
		// here we create Plexus container, the Maven default IoC container
		// Plexus falls outside of MI scope, just accept the fact that
		// MI is a Plexus component ;)
		// If needed more info, ask on Maven Users list or Plexus Users list
		// google is your friend!
		this.plexusContainer = new DefaultPlexusContainer();

		// lookup the indexer components from plexus
		this.indexer = plexusContainer.lookup(Indexer.class);
		this.indexUpdater = plexusContainer.lookup(IndexUpdater.class);
		// lookup wagon used to remotely fetch index
		this.httpWagon = plexusContainer.lookup(Wagon.class, "http");

	}

	public boolean buildCentralIndex() throws ComponentLookupException,
			IOException {
		boolean updateHappened =false;
		File centralLocalCache = new File(Utils.MAVEN_INDEX_LOCATION+"target/central-cache");
		File centralIndexDir = new File(Utils.MAVEN_INDEX_LOCATION+"target/central-index");

		// Creators we want to use (search for fields it defines)
		List<IndexCreator> indexers = new ArrayList<IndexCreator>();
		indexers.add(plexusContainer.lookup(IndexCreator.class, "min"));
		indexers.add(plexusContainer.lookup(IndexCreator.class, "jarContent"));
		indexers.add(plexusContainer.lookup(IndexCreator.class, "maven-plugin"));

		// Create context for central repository index
		centralContext = indexer.createIndexingContext("central-context",
				"central", centralLocalCache, centralIndexDir,
				// "http://search.maven.org/", null, true, true, indexers );
				"http://repo1.maven.org/maven2", null, true, true, indexers);

		// Update the index (incremental update will happen if this is not 1st
		// run and files are not deleted)
		// This whole block below should not be executed on every app start, but
		// rather controlled by some configuration
		// since this block will always emit at least one HTTP GET. Central
		// indexes are updated once a week, but
		// other index sources might have different index publishing frequency.
		// Preferred frequency is once a week.
		if (true) {
			System.out.println("Updating Index...");
			System.out
					.println("This might take a while on first run, so please be patient!");
			// Create ResourceFetcher implementation to be used with
			// IndexUpdateRequest
			// Here, we use Wagon based one as shorthand, but all we need is a
			// ResourceFetcher implementation
			TransferListener listener = new AbstractTransferListener() {
				public void transferStarted(TransferEvent transferEvent) {
					System.out.print("  Downloading "
							+ transferEvent.getResource().getName());
				}

				public void transferProgress(TransferEvent transferEvent,
						byte[] buffer, int length) {
				}

				public void transferCompleted(TransferEvent transferEvent) {
					System.out.println(" - Done");
				}
			};
			ResourceFetcher resourceFetcher = new WagonHelper.WagonFetcher(
					httpWagon, listener, null, null);

			Date centralContextCurrentTimestamp = centralContext.getTimestamp();
			IndexUpdateRequest updateRequest = new IndexUpdateRequest(
					centralContext, resourceFetcher);
			IndexUpdateResult updateResult = indexUpdater
					.fetchAndUpdateIndex(updateRequest);
			
			if (updateResult.isFullUpdate()) {
				System.out.println("Full update happened!");
				updateHappened=true;
			} else if (updateResult.getTimestamp().equals(
					centralContextCurrentTimestamp)) {
				System.out.println("No update needed, index is up to date!");
				updateHappened=false;
			} else {
				System.out
						.println("Incremental update happened, change covered "
								+ centralContextCurrentTimestamp + " - "
								+ updateResult.getTimestamp() + " period.");
				updateHappened=true;
			}

		}
		return updateHappened;
	}

	public List<ArtifactInfo> allArtifactInfo() throws IOException {
		List<ArtifactInfo> list = new ArrayList<ArtifactInfo>();
		final IndexSearcher searcher = centralContext.acquireIndexSearcher();
		try {
			final IndexReader ir = searcher.getIndexReader();
			Bits liveDocs = MultiFields.getLiveDocs(ir);
			for (int i = 0; i < ir.maxDoc(); i++) {
				if (!liveDocs.get(i)) {
					// document is deleted...
				}else {
					final Document doc = ir.document(i);
					list.add(IndexUtils.constructArtifactInfo(doc,
							centralContext));
				}
			}
		} finally {
			centralContext.releaseIndexSearcher(searcher);
		}
		return list;
	}

	public int allArtifactSize() throws IOException {
		return centralContext.acquireIndexSearcher().getIndexReader().maxDoc();
	}

	public List<ArtifactInfo> partialArtifactInfo(int startPosition,
			int endPosition) throws IOException {
		List<ArtifactInfo> list = new ArrayList<ArtifactInfo>();
		final IndexSearcher searcher = centralContext.acquireIndexSearcher();
		try {
			final IndexReader ir = searcher.getIndexReader();
			Bits liveDocs = MultiFields.getLiveDocs(ir);
			for (int i = startPosition; i < endPosition; i++) {
				if (!liveDocs.get(i)) {
					// document is deleted...
				}else {
					final Document doc = ir.document(i);
					list.add(IndexUtils.constructArtifactInfo(doc,
							centralContext));
				}
			}
		} finally {
			centralContext.releaseIndexSearcher(searcher);
		}
		return list;
	}

}
