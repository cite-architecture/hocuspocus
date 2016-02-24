package edu.holycross.shot.hocuspocus


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*
*/
class TestTabFromXml  {

  File invFile = new File("testdata/conf2016/testinventory3.xml")
  TextInventory inv = new TextInventory(invFile)

  File confFile = new File("testdata/conf2016/citationconfig3.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)
  @Test
  void testXmlTabulator() {

    Tabulator tab = new Tabulator()
    CtsUrn txtUrn = new CtsUrn("urn:cts:test1:test.unclassified.unittest:")
    File txtFile = new File("testdata/testcorpus2/xml/no-namespace.xml")

    def taboutput = tab.tabulateFile(txtUrn, inv, conf, txtFile).readLines()
    assert taboutput.size() == 2

    def expectedUrns = ["urn:cts:test1:test.unclassified.unittest:preface", "urn:cts:test1:test.unclassified.unittest:1.1"]
    taboutput.eachWithIndex { l, i ->
      def cols = l.split(/#/)
      assert cols[0] == expectedUrns[i]
    }

  }
}
