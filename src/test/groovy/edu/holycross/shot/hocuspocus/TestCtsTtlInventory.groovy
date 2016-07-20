package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

class TestCtsTtlInventory  {


  File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")
  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")
  File baseDir = new File("testdata/testcorpus2016/xml")

  File schemaFile = new File("testdata/conf2016/TextInventory.rng")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)
  TextInventory ti = new TextInventory(tiFile)
  CtsTtl ttler = new CtsTtl(ti, conf)



  @Test
  void testCtsTtlInventory() {
    File outDir = new File("testdata/output")

    String ttl = ttler.turtleizeInv(ti,conf,true)
    
    File ttlOut = new File("testdata/output/test-cts-inventoryTtl.ttl")
    ttlOut.setText(ttl, "UTF-8")

    // inspect  contents, count rdf verbs...
    //System.err.println "From Inventory File:\n" + ttl

    ttlOut.delete()
  }


}
