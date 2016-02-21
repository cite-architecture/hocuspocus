package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTtlCtsNs {

  @Test
  void testNsMapping() {


    TextInventory ti = new TextInventory(new File("testdata/testcorpus2/ti2.xml"))
    String ttl = CtsTtl.turtleizeInv(ti, false)

    Integer expectedCtsNamespaces = 2
    Integer actualCtsNamespaces = 0

    ttl.eachLine {  l ->
      if (l ==~ /.*cts:fullUri.*/) {
          actualCtsNamespaces++
      }
    }
    assert  actualCtsNamespaces == expectedCtsNamespaces
  }

}
