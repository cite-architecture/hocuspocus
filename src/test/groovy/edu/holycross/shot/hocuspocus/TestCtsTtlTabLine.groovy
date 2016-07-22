package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail



import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestCtsTtlTabLine  {


  File tabFile = new File("testdata/testcorpus2016/tabs/allen-tabs.txt")
  File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")
  TextInventory ti = new TextInventory(tiFile)

  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  CtsTtl ttler = new CtsTtl(ti, conf)

  @Test
  void testThreeLines() {
  String threeLines = """
urn:cts:greekLit:tlg0012.tlg001.testAllen:1.1#1##2#/tei:TEI/tei:text/tei:body/tei:div[@n='1']#<l xmlns="http://www.tei-c.org/ns/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" n="1">Μῆνιν ἄειδε θεὰ Πηληϊάδεω Ἀχιλῆος </l>#/tei:TEI/tei:text/tei:body/tei:div[@n='?']/tei:l[@n='?']# xmlns:tei='http://www.tei-c.org/ns/1.0'
urn:cts:greekLit:tlg0012.tlg001.testAllen:1.2#2#1#3#/tei:TEI/tei:text/tei:body/tei:div[@n='1']#<l xmlns="http://www.tei-c.org/ns/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" n="2">οὐλομένην, ἣ μυρί᾽ Ἀχαιοῖς ἄλγε᾽ ἔθηκε, </l>#/tei:TEI/tei:text/tei:body/tei:div[@n='?']/tei:l[@n='?']# xmlns:tei='http://www.tei-c.org/ns/1.0'
urn:cts:greekLit:tlg0012.tlg001.testAllen:1.3#3#2#4#/tei:TEI/tei:text/tei:body/tei:div[@n='1']#<l xmlns="http://www.tei-c.org/ns/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" n="3">πολλὰς δ᾽ ἰφθίμους ψυχὰς Ἄϊδι προΐαψεν </l>#/tei:TEI/tei:text/tei:body/tei:div[@n='?']/tei:l[@n='?']# xmlns:tei='http://www.tei-c.org/ns/1.0'
"""


//System.err.println "Three-line test:"
//System.err.println ttler.turtleizeLine(threeLines, "http://www.tei-c.org/ns/1.0", "tei",)

}
  @Test
  void testOneLine() {
    String testLine = """urn:cts:greekLit:tlg0012.tlg001.testAllen:1.1#1##2#/tei:TEI/tei:text/tei:body/tei:div[@n='1']#<l xmlns="http://www.tei-c.org/ns/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" n="1">Μῆνιν ἄειδε θεὰ Πηληϊάδεω Ἀχιλῆος </l>#/tei:TEI/tei:text/tei:body/tei:div[@n='?']/tei:l[@n='?']# xmlns:tei='http://www.tei-c.org/ns/1.0'"""

    def oneLineRdf = ttler.turtleizeLine(testLine, "http://www.tei-c.org/ns/1.0", "tei",).readLines()

    def verbs = ["cts:isPassageOf", "cts:hasPassage",
		 "cts:hasSequence", "cts:hasTextContent",
		 "cts:containedBy", "cts:contains",
		 "hmt:xmlOpen", "hmt:xpTemplate"
		]


    oneLineRdf.each {
      println it
    }
    
    // should be one occurrence of each:
    verbs.eachWithIndex { v, i ->
      def matchLines = oneLineRdf.findAll { it ==~ /.+${verbs[i]}.+/ }
      assert matchLines.size() == 1
    }

    // should occur *twice*, since this is a two-tier citation scheme:
    def depthLines = oneLineRdf.findAll {it ==~ /.+cts:citationDepth.+/}
    assert depthLines.size() == 2
  }


}
