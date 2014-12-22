package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestEdGenForUrn extends GroovyTestCase {


    File invFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(invFile)
    File archiveDir = new File("testdata/testcorpus2/xml")

    File outDir = new File("testdata/testoutput")

    String srcEditionUrn = "urn:cts:greekLit:tlg0012.tlg001.testlines"

    @Test void testEditionGenerator() {
        outDir.deleteDir()
        outDir.mkdir()

        Corpus c = new Corpus(invFile, archiveDir)
	//c.generateAnalyticalEditionForUrn(srcEditionUrn, outDir)
	
    }
}

