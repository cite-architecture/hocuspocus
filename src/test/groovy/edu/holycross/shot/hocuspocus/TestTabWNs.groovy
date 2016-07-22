package edu.holycross.shot.hocuspocus


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail



import edu.harvard.chs.cite.CtsUrn

/**
*
*/
class TestTabWNs  {

  File invFile = new File("testdata/conf2016/testinventory3.xml")
  TextInventory inv = new TextInventory(invFile)

  File confFile = new File("testdata/conf2016/citationconfig3.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)
  @Test
  void testXmlTabulator() {

    XmlTabulator tab = new XmlTabulator()

    CtsUrn txtUrn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines:")
    File txtFile = new File("testdata/conf2016/xml/A_Iliad_testlines.xml")

    def taboutput = tab.tabulateFile(txtUrn, inv, conf, txtFile).readLines()

    Integer citableNodes = 0
    taboutput.eachWithIndex { l, i ->
      def cols = l.split(/#/)
      if (cols[0] != "namespace") {
        citableNodes++
      }
    }
    Integer expectedCitableNodes = 22
    assert citableNodes == expectedCitableNodes
  }
}
