package edu.holycross.shot.hocuspocus


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
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

    Tabulator tab = new Tabulator()
    CtsUrn txtUrn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines:")
    File txtFile = new File("testdata/testcorpus2/xml/A_Iliad_testlines.xml")

    def taboutput = tab.tabulateFile(txtUrn, inv, conf, txtFile).readLines()
    System.err.println "Got " + taboutput.size() + " nodes."

    Integer citableNodes = 0
    taboutput.eachWithIndex { l, i ->
      def cols = l.split(/#/)
      //assert cols[0] == expectedUrns[i]
      if (cols[0] != "namespace") {
        citableNodes++
      }
    }
    Integer expectedCitableNodes = 22
    assert citableNodes == expectedCitableNodes
  }
}
