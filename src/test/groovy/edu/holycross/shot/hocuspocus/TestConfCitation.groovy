package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestConfCitation {

  @Test
  void testNsMapping() {

    File confFile = new File("testdata/newconf/citationconfig3.xml")
    def root = new XmlParser().parseText(confFile.getText("UTF-8"))

    def citationData = CitationConfigurationFileReader.collectCitationModels(root)
    System.err.println "CITE DATA!" + citationData

    // Three texts configured:
    Integer expectedSize = 3
    assert citationData.size() == expectedSize

    Set expectedKeySet = ["urn:cts:greekLit:tlg0012.tlg001.test2:", "urn:cts:greekLit:tlg0012.tlg001.testlines:", "urn:cts:test1:test.unclassified.unittest:"]

    assert citationData.keySet()  == expectedKeySet

  }

}
