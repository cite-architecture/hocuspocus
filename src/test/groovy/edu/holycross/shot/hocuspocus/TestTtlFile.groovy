package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestTtlFile  {


  File tabFile = new File("testdata/testcorpus2016/tabs/allen-tabs.txt")

  File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")

  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")

  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  TextInventory ti = new TextInventory(tiFile)

  CtsTtl ttler = new CtsTtl(ti, conf)

  @Test
  void testPrevNext() {

    String ttl = ttler.turtleizeFile(tabFile)
    System.err.println "From tabFile:\n" + ttl
    File ttlOut = new File("build/allen.ttl")
    ttlOut.setText(ttl, "UTF-8")
  }


}
