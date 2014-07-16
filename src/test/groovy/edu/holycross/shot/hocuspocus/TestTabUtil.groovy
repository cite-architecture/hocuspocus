package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/** Class testing hocuspocus Corpus class.
*/
class TestTabUtil extends GroovyTestCase {

  File tabFile = new File("testdata/A_Iliad_testlines-00001.txt")
  File outDir = new File("testdata/testoutput")
    
  @Test void testOneText() {
    outDir.deleteDir()
    outDir.mkdir()
    
    TabUtil tabber = new TabUtil()
    assert tabber.getColumnSeparator() == "#"
  }

}
