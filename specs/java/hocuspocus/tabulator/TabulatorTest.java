package hocuspocus.tabulator;

import org.concordion.integration.junit3.ConcordionTestCase;

import  edu.holycross.shot.hocuspocus.Corpus;

import  edu.harvard.chs.cite.CtsUrn;
import  edu.harvard.chs.cite.TextInventory;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.Collections;



public class TabulatorTest extends ConcordionTestCase {


    String docPath = "/build/concordion-results/hocuspocus/tabulator/";

    /** Path in concordion build to RNG schema for Text Inventory.*/
    String schema = "../../../resources/test/schemas/TextInventory.rng";


    
    /** Hands back a String parameter so we can save links using concordion's
     * #Href variable for use in later computations. */
    public String setHref(String path) {
	return (path);
    }

    public Integer shouldCountTabs(String ti, String archive, String outDir) {

	Integer count = 0;
	
	File tabDir = new File("build/tabulated");
	if (! tabDir.exists()) {
	    tabDir.mkdir();
	}

	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	    File inv =  new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    c.tabulateRepository(tabDir);


	    File[] fileList = tabDir.listFiles();
	    for (File f : fileList)  {
		if (f.getName().matches(".*txt"))  {
		    count++;
		}
	    }

	} catch(Exception e) {
	    System.err.println ("Failed to count tabs: " + e.toString());
	}
	
	return count;
    }

    
    public String shouldGetFileNameForUrn(String ti, String archive, String urnStr) {
	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	    File inv =  new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	
	    CtsUrn urn = new CtsUrn(urnStr);

	    String xmlName = c.getInventory().onlineDocname(urn);
	    String txtName = xmlName.replaceFirst(".xml",".txt");
	    return txtName;
	    
	} catch (Exception e) {
	    return ("Failed: " + e.toString());
	}
    }
    
    public Integer shouldCountTabulatedLines(String ti, String archive, String urnStr) {


	try {
	    String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	    File inv =  new File(buildPath + ti);
	    File archiveDir = new File(buildPath + archive);


	    File buildDir =  new File(new java.io.File( "." ).getCanonicalPath() + "/build");
	    CtsUrn urn = new CtsUrn(urnStr);
	    File schemaFile = new File(buildPath + schema);
	    Corpus c = new Corpus(inv, archiveDir, schemaFile);
	    c.tabulateFile(urn,buildDir);
	    System.err.println("Tabulated files into  " + buildDir);
	    String xmlName = c.getInventory().onlineDocname(urn);
	    String txtName = xmlName.replaceFirst(".xml",".txt");
	    System.err.println ("Look for " + txtName);
	    File tabFile = new File(buildDir + "/" + txtName);
	    FileReader tiny = new FileReader(tabFile);
	    Integer count = 0;
	    BufferedReader reader = new BufferedReader(tiny);
	    while (reader.readLine() != null) {
		count++;
	    }
	    reader.close();
	    return count;
	    
	} catch (Exception e) {
	    System.err.println ("Failed: " + e);
	}
	
	return -1;
    }
}


