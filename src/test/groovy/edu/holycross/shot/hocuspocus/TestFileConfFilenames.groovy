package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestConfFilenames {

  @Test
  void testNsMapping() {

    File confFile = new File("testdata/conf2016/citationconfig3.xml")
    def root = new XmlParser().parseText(confFile.getText("UTF-8"))

    def fileMap = CitationConfigurationFileReader.collectFileNames(root)

    Integer expectedFileNames = 3
    assert fileMap.keySet().size() == expectedFileNames


    String iliadUrn =  "urn:cts:greekLit:tlg0012.tlg001.testlines:"
    String expectedName = "A_Iliad_testlines.xml"
    assert fileMap[iliadUrn] == expectedName
  }


}
