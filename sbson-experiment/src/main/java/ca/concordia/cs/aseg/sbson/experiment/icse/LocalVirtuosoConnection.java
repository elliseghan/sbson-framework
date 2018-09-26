package ca.concordia.cs.aseg.sbson.experiment.icse;

//import ca.aseg.triplestore.ITriplestoreConnection;
//import virtuoso.jena.driver.VirtGraph;

public class LocalVirtuosoConnection{

	/*
	 * This class provides the capability to connect to a localhost Virtuoso Server or one on the LAN
	 * For localhost, provided url should be in format of jdbc:virtuoso://localhost:port
	 * For host on LAN, the format should be jdbc:virtuoso://hostname:port (e.g. jdbc:virtuoso://slicer:1111)
	 */
	/*private static LocalVirtuosoConnection connection = null;
	private static Object graph = null;

	private LocalVirtuosoConnection() {

	}

	public static LocalVirtuosoConnection getInstance() {
		if (connection == null)
			connection = new LocalVirtuosoConnection();
		return connection;
	}*/

//	public Object connectToStore(String url, String graphUri, String uname, String pwd) {
//		if (graph != null && ((VirtGraph) graph).getGraphUrl().equals(url)
//				&& ((VirtGraph) graph).getGraphUser().equals(uname))
//			return graph;
//		if (graphUri != null) {
//			graph = new VirtGraph(graphUri, url, uname, pwd);
//		} else {
//			graph = new VirtGraph(url, uname, pwd);
//		}
//		return graph;
//	}
//
//	public Object connectToStore(String url, String uname, String pwd) {
//		return connectToStore(url, null, uname, pwd);
//	}

}
