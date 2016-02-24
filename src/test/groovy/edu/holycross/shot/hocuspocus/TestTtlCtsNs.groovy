package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTtlCtsNs {


  CitationConfigurationFileReader confFile = new CitationConfigurationFileReader(new File("testdata/conf2016/citationconfig3.xml"))


  @Test
  void testNsMapping() {


    TextInventory ti = new TextInventory(new File("testdata/conf2016/testinventory3.xml"))
    String ttl = CtsTtl.turtleizeInv(ti, confFile, false)



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
