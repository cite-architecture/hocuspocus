package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

import org.custommonkey.xmlunit.*

/**
*/
class TestXmlUnit  {


  File tabFile = new File("testdata/testcorpus2016/testcorpus2016.tab")

  File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")

  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")

  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  TextInventory ti = new TextInventory(tiFile)

  TtlGenerator ttler = new TtlGenerator(ti, conf)

  @Test
  void testTtlOneFile() {

  }


}
