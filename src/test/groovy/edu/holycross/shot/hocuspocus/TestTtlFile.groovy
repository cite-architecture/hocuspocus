package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestTtlFile  {


  File tabFile = new File("testdata/testcorpus2016/derivs/testcorpus2016.tab")

  File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")

  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")

  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  TextInventory ti = new TextInventory(tiFile)

  TtlGenerator ttler = new TtlGenerator(ti, conf)

  @Test
  void testTtlOneFile() {

    String ttl = ttler.turtleizeFile(tabFile)
    System.err.println "From tabFile:\n" + ttl
    File ttlOut = new File("testdata/testcorpus2016/testcorpus2016.ttl")
    ttlOut.setText(ttl, "UTF-8")
  }


}
