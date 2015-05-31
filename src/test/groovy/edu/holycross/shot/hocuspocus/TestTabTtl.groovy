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
class TestTabTtl extends GroovyTestCase {

  File outDir = new File("testdata/testoutput")
  File invFile = new File("specs/resources/data/archive1/testinventory.xml")
  TextInventory inv = new TextInventory(invFile)

  void testSingleLine() {
    String inputLine  = """urn:cts:greekLit:tlg0012.tlg001.msA:1.2#2#1#3#/tei:TEI/tei:text/tei:body/tei:div[@n = '1']#<l xmlns="http://www.tei-c.org/ns/1.0" n="2"> οὐλομένην· ἡ μυρί' <rs n="urn:cite:hmt:place.place96" type="ethnic"> Ἀχαιοῖς</rs> ἄλγε' ἔθηκεν·</l>#/tei:TEI/tei:text/tei:body/tei:div[@n = '?']/tei:l[@n = '?']"""

    CtsTtl ttler = new CtsTtl(inv)
    String ttlStr = ttler.turtleizeLine(inputLine)
    // For one input line, should get 10 TTL statements.
    // Our code formats one TTL statement per line.
    assert ttlStr.readLines().size() == 10
  }
  

}
