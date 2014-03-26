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
    File archiveDir = new File("testdata/bigfile/xml")

    String sepChar = "#"
    File outDir = new File("testdata/testoutput")

    @Test void testCorpusTurtleizer() {
        outDir.deleteDir()
        outDir.mkdir()

        Corpus c = new Corpus(tiFile, archiveDir)
        c.turtleizeRepository(outDir)
    }
}

