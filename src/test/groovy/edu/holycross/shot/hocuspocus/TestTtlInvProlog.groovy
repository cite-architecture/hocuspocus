package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTtlInvProlog {




  @Test
  void testPrefix() {

    TextInventory ti = new TextInventory(new File("testdata/testcorpus2/ti2.xml"))
    String ttlWPrefix = CtsTtl.turtleizeInv(ti, true)
    String ttlNoPrefix = CtsTtl.turtleizeInv(ti, false)
    Integer expectedPrefixLines = 4
    Integer actualPrefixLines = 0
    ttlWPrefix.eachLine {
      if (it ==~ '@prefix.+') {
        actualPrefixLines++
      }
    }
    assert actualPrefixLines == expectedPrefixLines
    actualPrefixLines = 0
    ttlNoPrefix.eachLine {
      if (it ==~ '@prefix.+') {
        actualPrefixLines++
      }
    }
    assert actualPrefixLines == 0
  }

}
