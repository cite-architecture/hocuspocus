package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail



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

  CtsTtl ttler = new CtsTtl(ti, conf)

  @Test
  void testTtlOneFile() {
    System.err.println "Check on shortrun:"
    def runsize = System.getProperty("shortrun")
    System.err.println "${runsize} of type ${runsize.getClass()}"
  }


}
