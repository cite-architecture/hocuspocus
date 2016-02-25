package edu.holycross.shot.hocuspocus


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*
*/
class TestTabLarge  {

  File invFile = new File("testdata/testhdt/hdtinventory.xml")
  TextInventory inv = new TextInventory(invFile)

  File confFile = new File("testdata/testhdt/hdtconfig.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  @Test
  void testHdt() {
    Tabulator tab = new Tabulator()
    File log = new File("testdata/output/log.txt")
    log.text = ""
    tab.log = log
    tab.debug = 1

    File ttloutput = new File("testdata/output/hdt.ttl")
    String urn = "urn:cts:greekLit:tlg0016.tlg001.grcTokFull:"
    String fName = "testdata/testhdt/fu-hdt-grc-tok.xml"

    CtsUrn txtUrn = new CtsUrn(urn)
    File txtFile = new File(fName)
    def tabulated = tab.tabulateFile(txtUrn, inv, conf, txtFile)
    ttloutput.append(tabulated)
  }
}
