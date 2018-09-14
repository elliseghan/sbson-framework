package ca.concordia.cs.aseg.sbson.experiment.icse;

public class Queries {

    public static String getQueryBase() {
        String base = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "\n"
                + "PREFIX main: <http://se-on.org/ontologies/general/2012/02/main.owl#>" + "\n"
                + "PREFIX meas: <http://se-on.org/ontologies/general/2012/02/measurement.owl#>" + "\n"
                + "PREFIX build: <http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#>" + "\n"
                + "PREFIX sevont: <http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/securityDBs.owl#>" + "\n"
                + "PREFIX history: <http://se-on.org/ontologies/domain-specific/2012/02/history.owl#>\n"
                + "PREFIX nvd: <http://aseg.cs.concordia.ca/segps/ontologies/system-specific/2015/02/securityDBs-nvd.owl#>" + "\n"
                + "PREFIX code: <http://se-on.org/ontologies/domain-specific/2012/02/code.owl#>" + "\n"
                + "PREFIX maven: <http://aseg.cs.concordia.ca/segps/ontologies/system-specific/2015/02/maven.owl#>" + "\n"
                + "PREFIX markosCopyright: <http://www.markosproject.eu/ontologies/copyright#> \n"
                + "PREFIX markosLicense: <http://www.markosproject.eu/ontologies/licenses#> \n"
                + "PREFIX markos: <http://www.markosproject.eu/ontologies/oss-licenses#> \n";
        return base;
    }

    public static String getASMClients(String asmLibGAV) {
        String query = getQueryBase() +
                "select distinct ?client \n" +
                "where {\n" +
                "?client build:hasNonOptionalBuildDependencyOn " + asmLibGAV + "." +
                "} LIMIT 1000";
        return query;
    }

    public static String getASMClientsWithConflicts(String asmLibGAV) {
        String query = getQueryBase() +
                "select distinct ?client ?dep ?asm2 \n" +
                "where {\n" +
                "?client build:hasNonOptionalBuildDependencyOn " + asmLibGAV + "." +
                "?client build:hasNonOptionalBuildDependencyOn ?dep." +
                "?oldasm <http://aseg.cs.concordia.ca/segps/ontologies/system-specific/2015/02/maven.owl#hasGroup> ?oldasmgroup.\n" +
                "?oldasmgroup <http://aseg.cs.concordia.ca/segps/ontologies/system-specific/2015/02/maven.owl#hasGroupID> \"asm\".\n" +
                "?oldasm <http://se-on.org/ontologies/general/2012/02/main.owl#hasRelease> ?asm2.\n" +
                "?dep <http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#hasNonOptionalBuildDependencyOn> ?asm2.\n" +
                "FILTER(?asm2 != " + asmLibGAV + ")" +
                "}";
        return query;
    }

    public static String getASMClientsWithConflicts2(String asmLibGAV) {
        String query = getQueryBase() +
                "select distinct ?client ?dep1 ?dep2 ?asm2 \n" +
                "where {\n" +
                "?client build:hasNonOptionalBuildDependencyOn " + asmLibGAV + "." +
                "?client build:hasNonOptionalBuildDependencyOn ?dep1." +
                "?oldasm <http://aseg.cs.concordia.ca/segps/ontologies/system-specific/2015/02/maven.owl#hasGroup> ?oldasmgroup.\n" +
                "?oldasmgroup <http://aseg.cs.concordia.ca/segps/ontologies/system-specific/2015/02/maven.owl#hasGroupID> \"org.ow2.asm\".\n" +
                "?oldasm <http://se-on.org/ontologies/general/2012/02/main.owl#hasRelease> ?asm2.\n" +
                "?dep2 <http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#hasNonOptionalBuildDependencyOn> ?asm2.\n" +
                "?dep1 <http://aseg.cs.concordia.ca/segps/ontologies/domain-specific/2015/02/build.owl#hasNonOptionalBuildDependencyOn> ?dep2.\n" +
                "FILTER(?asm2 != " + asmLibGAV + ")" +
                "}";
        return query;
    }

    public static String getProjectReleases(String project){
        String query = getQueryBase() +
                "select distinct ?release \n" +
                "where {\n" +
                 project + " main:hasRelease ?release.\n" +
                "}";
        return query;
    }
}
