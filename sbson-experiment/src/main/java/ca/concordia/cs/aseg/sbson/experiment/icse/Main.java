package ca.concordia.cs.aseg.sbson.experiment.icse;


public class Main {

//    static String[][] strutsLibs = new String[][]{{"struts:struts:1.2.2", "struts:struts:1.2.4"}, {"struts:struts:1.2.7", "struts:struts:1.2.8"}, {"struts:struts:1.2.8", "struts:struts:1.2.9"}};
//    static String[][] cxfLibs = new String[][]{{"org.apache.cxf:cxf-rt-ws-security:2.4.0", "org.apache.cxf:cxf-rt-ws-security:2.4.1"},
//            {"org.apache.cxf:cxf-rt-ws-security:2.4.3", "org.apache.cxf:cxf-rt-ws-security:2.4.4"}, {"org.apache.cxf:cxf-rt-ws-security:2.4.5", "org.apache.cxf:cxf-rt-ws-security:2.4.6"},
//            {"org.apache.cxf:cxf-rt-ws-security:2.6.2", "org.apache.cxf:cxf-rt-ws-security:2.6.3"}, {"org.apache.cxf:cxf-rt-ws-security:2.6.3", "org.apache.cxf:cxf-rt-ws-security:2.7.0"}};
//    static String[][] fileuploadLibs =
//            new String[][]{{"commons-fileupload:commons-fileupload:1.0","commons-fileupload:commons-fileupload:1.0"}, {"commons-fileupload:commons-fileupload:1.0","commons-fileupload:commons-fileupload:1.1"},
//                    {"commons-fileupload:commons-fileupload:1.1","commons-fileupload:commons-fileupload:1.2"}, {"commons-fileupload:commons-fileupload:1.2","commons-fileupload:commons-fileupload:1.2.1"},
//                    {"commons-fileupload:commons-fileupload:1.2.1","commons-fileupload:commons-fileupload:1.2.2"}, {"commons-fileupload:commons-fileupload:1.2.2","commons-fileupload:commons-fileupload:1.3"}};


    public static void main(String[] args) {
        try {
            //getALLASMLibs("D:\\ICSE\\sparql1", "D:\\ICSE\\asm-libs\\");
            //parseALLASMLibs( "D:\\ICSE\\asm-libs\\", "D:\\ICSE\\asm-libs\\out.nt");
            // getASMClients("D:\\ICSE\\asm-libs\\", "D:\\ICSE\\asm-libs\\clientDirect1.csv", 0);
            //Init.getASMClients("D:\\ICSE\\asm-libs\\", "D:\\ICSE\\asm-libs\\clientInDirect2.csv", 1);


//            filterDirectClients("D:\\ICSE\\asm-libs\\clientDirect1.csv", "D:\\ICSE\\asm-libs\\filtered-clientDirect.csv", "D:\\ICSE\\dep-libs\\");
//            filterDirectClients("D:\\ICSE\\asm-libs\\clientDirect2.csv", "D:\\ICSE\\asm-libs\\filtered-clientDirect.csv", "D:\\ICSE\\dep-libs\\");
//            filterIndirectClients("D:\\ICSE\\asm-libs\\clientInDirect1.csv", "D:\\ICSE\\asm-libs\\filtered-clientInDirect.csv", "D:\\ICSE\\dep-libs\\");
//            Init.filterIndirectClients("D:\\ICSE\\output-csv\\clientDirect2.csv", "D:\\ICSE\\output-csv\\filtered-clientInDirect.csv", "D:\\ICSE\\jars\\");
//
//
//            getBCD(cxfLibs);
//            getBCD(strutsLibs);
//            getBCD(fileuploadLibs);

            //new Experiment().usageExperiment("D:\\ICSE\\output-csv\\filtered-clientDirect.csv","D:\\ICSE\\jars\\","D:\\ICSE\\output-csv\\directUsageResults",0);
            // new Experiment().usageExperiment("D:\\ICSE\\output-csv\\filtered-clientInDirect.csv","D:\\ICSE\\jars\\","D:\\ICSE\\output-csv\\indirectUsageResults",1);


            //Init.getDeprecationLibClients("D:\\ICSE\\jars\\","D:\\ICSE\\output-csv\\deprecation-input.csv");
            // Experiment.getDeprecatoinHistory("D:\\ICSE\\jars\\","D:\\ICSE\\output-csv\\deprecation\\");
            //Experiment.getDeprecatoinUsage("D:\\ICSE\\output-csv\\deprecation\\deprecation-input.csv","D:\\ICSE\\jars\\","D:\\ICSE\\output-csv\\deprecation\\usage\\");
            Experiment.extraAnalysis2("D:\\ICSE\\output-csv\\deprecation\\deprecation-input.csv","D:\\ICSE\\jars\\");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


}


