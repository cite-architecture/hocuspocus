package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestTtlTab  {


  File tabFile = new File("testdata/testcorpus2016/tabs/allen-tabs.txt")

  File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")

  TextInventory ti = new TextInventory(tiFile)

  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")

  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  CtsTtl ttler = new CtsTtl(ti, conf)

  @Test
  void testOneLine() {


      String testLine = """urn:cts:greekLit:tlg0012.tlg001.testAllen:1.1#1##2#/tei:TEI/tei:text/tei:body/tei:div[@n='1']#<l xmlns="http://www.tei-c.org/ns/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" n="1">Μῆνιν ἄειδε θεὰ Πηληϊάδεω Ἀχιλῆος </l>#/tei:TEI/tei:text/tei:body/tei:div[@n='?']/tei:l[@n='?']# xmlns:tei='http://www.tei-c.org/ns/1.0'"""


    testLine.eachLine {
      println ttler.turtleizeLine(it,"tei", "http://www.tei-c.org/ns/1.0")
    }
  }
/*
  File outDir = new File("testdata/testoutput")
  File invFile = new File("specs/resources/data/archive1/testinventory.xml")
  TextInventory inv = new TextInventory(invFile)

  void testSingleLine() {
    String inputLine  = """urn:cts:greekLit:tlg0012.tlg001.msA:1.2#2#1#3#/tei:TEI/tei:text/tei:body/tei:div[@n = '1']#<l xmlns="http://www.tei-c.org/ns/1.0" n="2"> οὐλομένην· ἡ μυρί' <rs n="urn:cite:hmt:place.place96" type="ethnic"> Ἀχαιοῖς</rs> ἄλγε' ἔθηκεν·</l>#/tei:TEI/tei:text/tei:body/tei:div[@n = '?']/tei:l[@n = '?']#  xmlns:tei='http://www.tei-c.org/ns/1.0'"""

    CtsTtl ttler = new CtsTtl(inv)
    String ttlStr = ttler.turtleizeLine(inputLine)
    // For one input line, should get 11 TTL statements.
    // Our code formats one TTL statement per line.
    assert ttlStr.readLines().size() == 11


    // resulting "textContent" should be valid XML!
    ttlStr.readLines().each { ln ->
      def svo = ln.split(/\s+/)
      if (svo[1] == "cts:hasTextContent") {
	if (svo.size() > 2) {
	  Integer lim = svo.size() - 2
	  String txt = svo[2..lim].join(" ")
	  // eliminate groovy triple quoting...
	  String xmlStr = txt.replaceAll('"""','')
	  // parse and validate....
	}
      }
    }
  }

*/


}
