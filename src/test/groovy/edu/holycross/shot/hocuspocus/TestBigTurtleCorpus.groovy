package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestBigTurtleCorpus extends GroovyTestCase {


    File tiFile = new File( "testdata/bigfile/inventory.xml")

    TextInventory inv = new TextInventory(tiFile)
    File archiveDir = new File("testdata/bigfile/xml")
    File outDir = new File("testdata/testoutput")
    String charEnc = "UTF-8"

    boolean addPrefix = true

    @Test void testCorpusTurtleizer() {
        outDir.deleteDir()
        outDir.mkdir()
	File ttlFile = new File(outDir, "bigFile.ttl")
        Corpus c = new Corpus(tiFile, archiveDir)
	c.ttl(ttlFile,addPrefix,outDir)
    }
}

