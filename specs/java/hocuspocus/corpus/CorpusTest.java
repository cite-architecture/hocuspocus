package hocuspocus.corpus;

import  edu.holycross.shot.hocuspocus.Corpus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.concordion.integration.junit3.ConcordionTestCase;

public class CorpusTest extends ConcordionTestCase {


    /** Path in concordion build to Corpus documentation.  Knowing this
     * lets us write markdown docs with relative references to data files
     * in the documentation.*/
    String docPath = "/build/concordion-results/hocuspocus/corpus/";
    

    /** Hands back a String parameter so we can save links using concordion's
     * #Href variable for use in later computations. */
    public String setHref(String path) {
	return (path);
    }



    /** Creates a Corpus object. */
    public boolean shouldMakeCorpus(String ti, String archive) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    Corpus c = new Corpus(inv, archiveDir);
	    return true;

	
	} catch (Exception e) {
	    System.err.println ("Unable to make corpus: " + e.toString());
	    return false;
	}
    }

    public Integer shouldGetNumberFilesInInventory(String ti, String archive) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    Corpus c = new Corpus(inv, archiveDir);
	    return c.filesInInventory().size();

	} catch (Exception e) {
	    System.err.println ("Unable to make corpus: " + e.toString());
	    return -1;
	}
    }


    
    public String shouldGetFilenameFromInventory(String ti, String archive, int idx) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    Corpus c = new Corpus(inv, archiveDir);
	    ArrayList invFiles =  c.filesInInventory();
	    Collections.sort(invFiles);
	    return(invFiles.get(idx).toString());
		   
	} catch (Exception e) {
	    return ("Unable to make corpus: " + e.toString());
	}

    }


        public String shouldGetUrnsFromInventory(String ti, String archive, int idx) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    Corpus c = new Corpus(inv, archiveDir);
	    ArrayList invUrns =  c.urnsInInventory();
	    Collections.sort(invUrns);
	    return(invUrns.get(idx).toString());
		   
	} catch (Exception e) {
	    return ("Unable to make corpus: " + e.toString());
	}

    }


    
    public String shouldGetFilesOnDisk(String ti, String archive, int idx) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    Corpus c = new Corpus(inv, archiveDir);
	    ArrayList diskFiles =  c.filesInArchive();
	    Collections.sort(diskFiles);

	    System.err.println ("DISK FILE: " + diskFiles.toString());
	    return(diskFiles.get(idx).toString());
		   
	} catch (Exception e) {
	    return ("Unable to make corpus: " + e.toString());
	}

    }

    
}
