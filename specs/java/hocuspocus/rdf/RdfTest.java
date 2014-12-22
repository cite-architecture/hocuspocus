package hocuspocus.rdf;

import org.concordion.integration.junit3.ConcordionTestCase;

import  edu.holycross.shot.hocuspocus.Corpus;

import  edu.harvard.chs.cite.CtsUrn;
import  edu.harvard.chs.cite.TextInventory;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.Collections;

public class RdfTest extends ConcordionTestCase {

    String docPath = "/build/concordion-results/hocuspocus/rdf/";

    
    /** Hands back a String parameter so we can save links using concordion's
     * #Href variable for use in later computations. */
    public String setHref(String path) {
	return (path);
    }


    public String shouldGetTtlFileName(String ti, String archive, String outDir) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	    File inv =  new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    Corpus c = new Corpus(inv, archiveDir);

	    File tabDir = new File("build/tabulated");
	    if (! tabDir.exists()) {
		tabDir.mkdir();
	    }
	    c.turtleizeRepository(tabDir);
		
	} catch (Exception e) {
	    System.err.println("Unable to write TTL: " + e.toString());
	}
	return ("Unimplemented.");
    }





       public Integer shouldCountTtlContentLines(String ti, String archive, String outDir) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	    File inv =  new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    Corpus c = new Corpus(inv, archiveDir);

	    File tabDir = new File("build/tabulated");
	    if (! tabDir.exists()) {
		tabDir.mkdir();
	    }
	    


	    File ttl = new File(tabDir, "corpus.ttl");
	    if (ttl.exists()) {
		ttl.delete();
	    }
	    c.turtleizeRepository(tabDir);

	    FileReader ttlReader = new FileReader(ttl);
	    Integer count = 0;
	    BufferedReader reader = new BufferedReader(ttlReader);
	     String line;
	     while ((line = reader.readLine()) != null) {
		 System.out.println(line);
		 if (line.matches(".*TextContent.*")) {
		     count++;
		 }
	    }
	    reader.close();
	    return count;
		
	} catch (Exception e) {
	    System.err.println("Unable to write TTL: " + e.toString());
	    return  -1;
	}
    }


}


