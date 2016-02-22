package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestConfXmlNs {

  @Test
  void testNsMapping() {

    File confFile = new File("testdata/newconf/citationconfig3.xml")

/*


    Integer expectedXmlNamespaces = 2
    Integer expectedMappings = 3

    Integer actualXmlNamespaces = 0
    Integer actualMappings = 0
    ttl.eachLine { l ->
      if (l ==~ /.*cts:abbreviatedBy.* /) {
          actualXmlNamespaces++
      } else if (l ==~ / . *cts:xmlns . * /) {
        actualMappings++
      }
    }
    assert actualXmlNamespaces == expectedXmlNamespaces
    assert actualMappings == expectedMappings
    */
  }

}
