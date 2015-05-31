package edu.holycross.shot.hocuspocus

// NEEDS TO BE COMPLETED.

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
* Tests automated generation of TTL representing entire corpus of texts.
* Writes output where it can be further tested by loading into a sparql
* endpoint.
* Compare test in TestTtlForEdition where the TTL is "manually" built
* directly from tabulated source.
*/
class TestTtlPrevNext extends GroovyTestCase {

  File outDir = new File("testdata/testoutput")
  File invFile = new File("specs/resources/data/archive1/testinventory.xml")
  TextInventory inv = new TextInventory(invFile)


  // Number P/N statements always = nodes * 2 - 2
  // since no first statement for first node or
  // last statement for last node.
  Integer computePNs(Integer nodes) {
    return(nodes * 2 - 2)
  }
  
  void testPrevNext() {
    CtsTtl ttler = new CtsTtl(inv)

    File tabFile = new File("testdata/testtab.txt")
    String tabStr = tabFile.getText("UTF-8")
    String ttlStr = ttler.turtleizePrevNext(tabStr)

    // P/N statements written 1 per line, so
    // number lines == number P/N statements
    Integer numberSampleNodes = 4
    assert ttlStr.readLines().size() == computePNs(numberSampleNodes)
  }
  

}
