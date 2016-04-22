package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestCtsTtlSeq  {


  File tabFile = new File("testdata/conf2016/tabs/allen-tabs.txt")

  File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")

  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  TextInventory ti = new TextInventory(tiFile)

  CtsTtl ttler = new CtsTtl(ti, conf)

  @Test
  void testPrevNext() {

    System.err.println ttler.turtleizePrevNext(tabFile.getText())

  }


}
