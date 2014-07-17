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

  CtsUrn urn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines:1.4")
    
  @Test void testOneUrn() {
    outDir.deleteDir()
    outDir.mkdir()
    
    TabUtil tabber = new TabUtil()
    assert tabber.getColumnSeparator() == "#"
    String oneMatch = tabber.tabEntryForUrn(tabFile, urn)
    println "MATCH: " + oneMatch

  }


  @Test void testUrnList() {
    TabUtil tabber = new TabUtil()

    def urnCheck = ["urn:cts:greekLit:tlg0012.tlg001.testlines:1.4", "urn:cts:greekLit:tlg0012.tlg001.testlines:1.5"]

    def entries = tabber.tabEntriesForUrns(tabFile,urnCheck)
    assert entries.size() == urnCheck.size()
    
  }



}
