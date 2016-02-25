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

    /** Path in concordion build to RNG schema for Text Inventory.*/
    String schema = "../../../resources/test/schemas/TextInventory.rng";

    /** Hands back a String parameter so we can save links using concordion's
     * #Href variable for use in later computations. */
    public String setHref(String path) {
	return (path);
    }



    /** Creates a Corpus object. */
    /*
    public boolean shouldMakeCorpus(String ti, String archive) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    return true;

	
	} catch (Exception e) {
	    System.err.println ("CorpusTest: unable to make corpus: " + e.toString());
	    return false;
	}
    }

    public Integer shouldGetNumberFilesInInventory(String ti, String archive) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir,schemaFile);
	    return c.filesInInventory().size();

	} catch (Exception e) {
	    System.err.println ("CorpusTest, number files: Unable to make corpus: " + e.toString());
	    return -1;
	}
    }

        public Integer shouldGetNumberFilesOnDisk(String ti, String archive) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    return c.filesInArchive().size();

	} catch (Exception e) {
	    System.err.println ("CorpusTest: unable to make corpus: " + e.toString());
	    return -1;
	}
    }


    
    public String shouldGetFilenameFromInventory(String ti, String archive, int idx) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    ArrayList invFiles =  c.filesInInventory();
	    Collections.sort(invFiles);
	    return(invFiles.get(idx).toString());
		   
	} catch (Exception e) {
	    return ("CorpusTest, number in inv: unable to make corpus: " + e.toString());
	}

    }


        public String shouldGetUrnsFromInventory(String ti, String archive, int idx) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir,schemaFile);
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
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    ArrayList diskFiles =  c.filesInArchive();
	    Collections.sort(diskFiles);
	    return(diskFiles.get(idx).toString());
		   
	} catch (Exception e) {
	    return ("Unable to make corpus: " + e.toString());
	}
    }



    public boolean filesAndInventoryShouldMatch(String ti, String archive) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    return c.filesAndInventoryMatch();

	} catch (Exception e) {
	    System.err.println ("Unable to make corpus: " + e.toString());
	    return  false;
	}
    }

    


    public Integer shouldGetNumberFilesOnDiskNotInventoried(String ti, String archive) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    return c.filesMissingFromInventory().size();

	} catch (Exception e) {
	    System.err.println ("Unable to make corpus: " + e.toString());
	    return -1;
	}
    }



    public String shouldGetFileOnDiskNotInventoried(String ti, String archive, int idx) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    ArrayList diskFiles =  c.filesMissingFromInventory();
	    Collections.sort(diskFiles);
	    return(diskFiles.get(idx).toString());
		   
	} catch (Exception e) {
	    return ("CorpusTest, on disk no inv: Unable to make corpus: " + e.toString());
	}
    }



    ////
        public Integer shouldGetNumberInventoriedFilesNotOnDisk(String ti, String archive) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    return c.inventoriedMissingFromArchive().size();

	} catch (Exception e) {
	    System.err.println ("Unable to make corpus: " + e.toString());
	    return -1;
	}
    }



    public String shouldGetInventoriedFileNotFound(String ti, String archive, int idx) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath; 
	    File inv = new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    ArrayList invFiles =  c.inventoriedMissingFromArchive();
	    Collections.sort(invFiles);
	    return(invFiles.get(idx).toString());
		   
	} catch (Exception e) {
	    return ("CorpusTest, in inv not on disk: unable to make corpus: " + e.toString());
	}
    }
    */
}


