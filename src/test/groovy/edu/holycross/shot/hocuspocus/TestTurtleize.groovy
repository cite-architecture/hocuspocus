package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestTurtleize extends GroovyTestCase {


    File tiFile = new File( "testdata/testcorpus2/testinventory2.xml")

    TextInventory inv = new TextInventory(tiFile)
    File archiveDir = new File("testdata/testcorpus2/xml")
    File iliadAFile = new File(archiveDir, "A_Iliad_testlines.xml")

    String sepChar = "#"

    File outDir = new File("testdata/testoutput")

    @Test void testCorpusTurtleizer() {
        outDir.deleteDir()
        outDir.mkdir()

        Corpus c = new Corpus(tiFile, archiveDir)
        c.turtleizeRepository(outDir)
    }
}

