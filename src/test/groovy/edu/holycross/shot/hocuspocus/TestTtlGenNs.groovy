package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTtlGenNs {

  File tiFile = new File("testdata/testpipe/pipelineinv.xml")
  TextInventory ti = new TextInventory(tiFile)

  File confFile = new File("testdata/testpipe/pipelinecite.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)




  @Test
  void testNsMapping() {
    TtlGenerator ttler = new TtlGenerator(ti, conf)
    File tabDir = new File("testdata/testpipe/tabs")
    File outDir = new File("testdata/output")
    ttler
  }

}
