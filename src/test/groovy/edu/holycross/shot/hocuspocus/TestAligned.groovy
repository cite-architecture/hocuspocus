package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/** Class testing hocuspocus Corpus class.
*/
class TestAligned extends GroovyTestCase {

    File tiFile = new File( "testdata/aligned/TextInventory.xml")
    TextInventory inv = new TextInventory(tiFile)
    File archiveDir = new File("testdata/aligned/editions")

    File outDir = new File("testdata/testoutput")
    
    @Test void testOneText() {
      outDir.deleteDir()
      outDir.mkdir()

      Corpus c = new Corpus(tiFile, archiveDir)
      c.tabulateRepository(outDir)
    }

}
