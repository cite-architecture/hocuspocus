package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTtlXmlNs {


  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  File invFile = new File("testdata/testcorpus2016/testinventory-2016.xml")
  TextInventory inv = new TextInventory(invFile)


  @Test
  void testXmlNsMapping() {
    def root = new XmlParser().parseText(confFile.getText("UTF-8"))

    def nsMappings = CitationConfigurationFileReader.collectXmlNamespaceData(root)

    TtlGenerator ttler = new TtlGenerator(inv, conf)
    //System.err.println ttler.xmlNsTtl(nsMappings)
  }

}
