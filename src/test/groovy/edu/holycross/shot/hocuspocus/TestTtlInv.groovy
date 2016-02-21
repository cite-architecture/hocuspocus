package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTtlInv {

  @Test
  void testEmpty() {
  assert shouldFail {
      TextInventory ti = new TextInventory(new File("testdata/testcorpus2/emptyTi.xml"))
    }
  }
  @Test
  void testPrefix() {
    TextInventory ti = new TextInventory(new File("testdata/testcorpus2/ti2.xml"))

        System.err.println "TI has map list " + ti.nsMapList
    //String ttl = CtsTtl.turtleizeInv(ti, true)
    //System.err.println ttl
  }

}
