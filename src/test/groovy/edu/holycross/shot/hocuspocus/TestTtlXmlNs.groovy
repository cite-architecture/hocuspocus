package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTtlXmlNs {

  @Test
  void testNsMapping() {


    TextInventory ti = new TextInventory(new File("testdata/testcorpus2/ti2.xml"))
    String ttl = CtsTtl.turtleizeInv(ti, false)
    // There are 3 texts: 1 has 1 NS mapping, 1 has 2, 1 has 0,
    // drawn from 2 distinct XML namespaces, so:
    Integer expectedXmlNamespaces = 2
    Integer expectedMappings = 3

    Integer actualXmlNamespaces = 0
    Integer actualMappings = 0
    ttl.eachLine { l ->
      if (l ==~ /.*cts:abbreviatedBy.*/) {
          actualXmlNamespaces++
      } else if (l ==~ /.*cts:xmlns.*/) {
        actualMappings++
      }
    }
    assert actualXmlNamespaces == expectedXmlNamespaces
    assert actualMappings == expectedMappings
  }

}
