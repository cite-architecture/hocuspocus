package edu.holycross.shot.hocuspocus


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*
*/
class TestTabGen  {

  File invFile = new File("testdata/testcorpus2016/testinventory-2016.xml")
  TextInventory inv = new TextInventory(invFile)

  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)
  @Test
  void testXmlTabulator() {

    Tabulator tab = new Tabulator()
    CtsUrn txtUrn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testAllen:")
    File txtFile = new File("testdata/testcorpus2016/xml/Iliad-Allen.xml")

    def tabulated = tab.tabulateFile(txtUrn, inv, conf, txtFile)
    System.err.println "Tabulated: " + tabulated
    File allen = new File("build/allen-tabs.txt")

    allen.setText(tabulated, "UTF-8")
    System.err.println "No lines: " + allen.readLines().size()
  }
}
