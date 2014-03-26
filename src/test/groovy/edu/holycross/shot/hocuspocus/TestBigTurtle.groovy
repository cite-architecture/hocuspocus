package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestBigTurtle extends GroovyTestCase {


    File tiFile = new File( "testdata/bigfile/inventory.xml")

    TextInventory inv = new TextInventory(tiFile)
    File tabDir = new File("testdata/bigfile/tabs")
    File outDir = new File("testdata/testoutput")
    String charEnc = "UTF-8"



    boolean destroyTabs = false

    @Test void testCorpusTurtleizer() {
        outDir.deleteDir()
        outDir.mkdir()
	File ttlFile = new File(outDir, "bigFile.ttl")
        Corpus c = new Corpus(tiFile, tabDir)
        c.turtleizeTabsToFile(tabDir, ttlFile, destroyTabs)
    }
}

