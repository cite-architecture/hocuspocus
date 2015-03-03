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
import java.util.TreeSet;
import java.util.SortedSet;

import edu.holycross.shot.hocuspocus.CtsTtl ;

public class RdfTest extends ConcordionTestCase {

    String docPath = "/build/concordion-results/hocuspocus/rdf/";

    /** Path in concordion build to RNG schema for Text Inventory.*/
    String schema = "../../../resources/test/schemas/TextInventory.rng";

    
    /** Hands back a String parameter so we can save links using concordion's
     * #Href variable for use in later computations. */
    public String setHref(String path) {
	return (path);
    }


    public String getInvTTL (String ti)
    throws Exception {
	String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;

	try {
	    TextInventory inv =  new TextInventory(new File(buildPath + ti));
	    CtsTtl cttl = new CtsTtl(inv);
	    String ttl = cttl.turtleizeInv();
	    System.err.println ("\n\n\nTTL FROM IVENTORY ONLY:  \n" + ttl);
	    return   (ttl);
	} catch (Exception e) {
	    System.err.println ("getInvTTL: execption " + e.toString());
	    throw e;
	}
    }

    
    public Iterable<String>  shouldGetVerbs(String ti, String archive, String outDir) {
	SortedSet<String> verbs = new TreeSet<String>();
	String delims = "[ \t]+";
	String line;
	Integer count = 0;
	try {
	//	File tabDir = new File(outDir);
	String tabPath = new java.io.File( "." ).getCanonicalPath() + "/build/verbtabs";
	File tabDir = new File(tabPath);
	if (! tabDir.exists()) {
	    tabDir.mkdir();
	} else {
	    System.err.println ("\n\nDIR " + outDir + " ALREADY EXISTS");
	}

	File ttl = new File(tabDir, "corpus.ttl");
	if (ttl.exists()) {
	    ttl.delete();
	}

	//System.err.println ("Get verbs for ti " + ti + ", archive " + archive +" out dir " + outDir);
	//System.err.println ("\n\nCORPUS FILE IS " + ttl + "\n\n") ;
	
	String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	    File inv =  new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);	    
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    c.debug = 0;
	    //System.err.println ("Turtleize to " + tabDir);
	    c.turtleizeRepository(tabDir);



	    FileReader ttlReader = new FileReader(ttl);
	    BufferedReader reader = new BufferedReader(ttlReader);
	    while ((line = reader.readLine()) != null) {
		count++;
		String[] tokens = line.split(delims);
		if (tokens.length > 1) {
		    String verb = tokens[1];
		    //System.err.println(verb + " from: " + line);
		    verbs.add(verb);
		}
	    }

	    //System.err.println ("\n\nLooked at a total of " + count + " lines in "   + ttl.toString());

	    
	} catch (Exception e) {
	    System.err.println ("Your test failed miserably: "  + e.toString());
	}


        return verbs;
    }


    public String shouldGetTtlFileName(String ti, String archive, String outDir) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	    File inv =  new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);

	    File tabDir = new File(outDir);
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
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
		    
	    File tabDir = new File(outDir);
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


