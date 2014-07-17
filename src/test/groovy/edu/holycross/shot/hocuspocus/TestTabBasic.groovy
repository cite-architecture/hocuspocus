package org.homermultitext.hocuspocus

import edu.holycross.shot.hocuspocus.Corpus

import static org.junit.Assert.*
import org.junit.Test

class TestTabBasic extends GroovyTestCase {


  File tabData = new File("testdata/testtab.txt")
  def dataLines = tabData.readLines()


  @Test void testUrn() {
    TabularText tab =  new TabularText()
    String expectedUrnStr = "urn:cts:greekLit:tlg0012.tlg001.testlines:1.1"
    assert tab.getUrn(dataLines[1]).toString() == expectedUrnStr
  }


  @Test void testSeq() {
    TabularText tab =  new TabularText()
    Integer expectedSeq = 1
    assert tab.getSequence(dataLines[1]) == expectedSeq

  }



  @Test void testPrevSeq() {
    TabularText tab =  new TabularText()
    
    assert tab.getPrevSequence(dataLines[1]) == null
    assert tab.getPrevSequence(dataLines[2]) == 1

  }



  @Test void testNextSeq() {
    TabularText tab =  new TabularText()
    assert tab.getNextSequence(dataLines[1]) == 2

  }

  @Test void testText() {
    TabularText tab =  new TabularText()

    String actualText = tab.getText(dataLines[1])
    String expectedText =  """<l xmlns="http://www.tei-c.org/ns/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" n="1">Μῆνιν <w>ἄ<unclear>ει</unclear>δε</w> θεὰ Πηληϊάδεω Ἀχιλῆος </l>"""

    assert actualText == expectedText    
  }


}
