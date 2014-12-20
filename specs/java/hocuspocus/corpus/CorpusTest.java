package hocuspocus.corpus;

import  edu.holycross.shot.hocuspocus.Corpus;
import java.io.File;

import org.concordion.integration.junit3.ConcordionTestCase;

public class CorpusTest extends ConcordionTestCase {



    public String setHref(String path) {
	return (path);
    }



    
    public boolean shouldMakeCorpus(String ti, String archive) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + "/build/concordion-results/hocuspocus/corpus/"; 



	    
	    File inv = new File(buildPath + ti);
	    System.err.println("INV: " + inv.toString());
	    File archiveDir = new File(buildPath + archive);
	    System.err.println("ARCH: " + archiveDir.toString());
	    Corpus c = new Corpus(inv, archiveDir);
	    return true;

	
	} catch (Exception e) {
	    return false;
	}
    }
}
