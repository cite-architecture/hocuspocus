package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestConfCitation {

  File confFile = new File("testdata/conf2016/citationconfig3.xml")
  def root = new XmlParser().parseText(confFile.getText("UTF-8"))

  @Test
  void testAbstractMethods() {
    def citationData = CitationConfigurationFileReader.collectCitationModels(root)

    // Three texts configured:
    Integer expectedSize = 3
    assert citationData.size() == expectedSize

    Set expectedKeySet = ["urn:cts:greekLit:tlg0012.tlg001.test2:", "urn:cts:greekLit:tlg0012.tlg001.testlines:", "urn:cts:test1:test.unclassified.unittest:"]
    assert citationData.keySet()  == expectedKeySet
  }


  @Test
  void testRetrieval() {
    CitationConfigurationFileReader config = new CitationConfigurationFileReader(confFile)
    String urnVal = "urn:cts:greekLit:tlg0012.tlg001.test2:"
    CitationModel cm =  config.getCitationModel(urnVal)

    def expectedLabels = ["book", "line"]
    def actualLabels = []
    // each mapping is an arraylsit:
    cm.mappings.each { m ->
      m.each { triple ->
	assert triple instanceof edu.holycross.shot.hocuspocus.CitationTriplet
	actualLabels.add(triple.label)
      }
    }
    assert actualLabels == expectedLabels
  }

}
