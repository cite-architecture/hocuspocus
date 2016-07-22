package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestCtsTtlXmlNs {


  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  File invFile = new File("testdata/testcorpus2016/testinventory-2016.xml")
  TextInventory inv = new TextInventory(invFile)


  @Test
  void testXmlNsMapping() {
    def root = new XmlParser().parseText(confFile.getText("UTF-8"))
    def nsMappings = CitationConfigurationFileReader.collectXmlNamespaceData(root)

    CtsTtl ttler = new CtsTtl(inv, conf)
    def nsTtl = ttler.xmlNsTtl(nsMappings).readLines()
    def expectedStatements = 7
    assert nsTtl.size() == expectedStatements
    def expectedNamespaceStatements = 6
    def ctsNsStatements = nsTtl.findAll {it ==~ /.+cts:xmlns.+/}
    assert ctsNsStatements.size() == expectedNamespaceStatements
  }

}
