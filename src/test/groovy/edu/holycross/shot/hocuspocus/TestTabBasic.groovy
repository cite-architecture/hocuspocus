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

}
