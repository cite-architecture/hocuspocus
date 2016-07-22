package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail



import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestCitationConfig  {

  @Test
  void testXmlCorpus() {
    File confFile = new File("testdata/testcorpfunctions/citationconfig-2016.xml")
    CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)
    System.err.println "MAP = " + conf.onlineMap

    String urnVal = "urn:cts:greekLit:tlg0012.tlg001.minimal:"
    String expectedFileName = "Iliad-minimal.xml"

    OnlineSettings settings = conf.onlineMap[urnVal]
    assert settings.docFormat == DocumentFormat.XML
    assert settings.nodeFormat == NodeFormat.XML
    assert settings.fileName == expectedFileName


  }
}
