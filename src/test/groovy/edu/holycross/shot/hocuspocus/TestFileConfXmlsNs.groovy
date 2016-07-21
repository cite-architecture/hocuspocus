package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestFileConfXmlNs {

  @Test
  void testNsMapping() {

    File confFile = new File("testdata/conf2016/citationconfig3.xml")
    def root = new XmlParser().parseText(confFile.getText("UTF-8"))

    def nsMappings = CitationConfigurationFileReader.collectXmlNamespaceData(root)

    // Three docs, 2 mapped to tei, 1 not mapped.
    Integer expectedDocs = 3
    Integer expectedMapped = 2
    Integer expectedNull  = 1
    Integer expectedTei = 2

    assert nsMappings.size() == 3

    Integer actualMapped = 0
    Integer actualNull = 0
    Integer actualTei = 0
    nsMappings.keySet().each {
      def nsMap = nsMappings[it]
      if (nsMap.size() == 0 ) {
        actualNull++

      } else {
        actualMapped++
        def urnKey = nsMap.keySet()
        if (urnKey[0] == "tei")  {
          actualTei++
        }
      }
    }
    assert actualMapped == expectedMapped
    assert actualNull == expectedNull
    assert actualTei == expectedTei
  }

}
