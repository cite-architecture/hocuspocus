package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestMarkdown82XF {


  @Test
  void testFileTab() {
    File md = new File("testdata/markdown/archive/powered_by_ohco2.md")
    String urn = "urn:cts:aflibre:af.ah.hc:"


    String o2xf =  MdTabulator.mdFileTo82XF(md,urn)
    /*
    println "o2xf is " + o2xf.getClass()
    println "with val " + o2xf
    String lines = o2xf.split(/\n/)
    println "read lines as " + lines.getClass()
    println "with val " + lines */



    def actualUrns = []
    // skip header line:
    def lineCount = 0
    o2xf.eachLine {  l ->
      if (lineCount > 0) {
	def cols = l.split("#")
	if (cols.size() != 5) {
	  throw new Exception ("Wrong size for line " + cols)
	}
	actualUrns.add(cols[0])
      }
      lineCount++
    }

    def expectedUrns = [
    "urn:cts:aflibre:af.ah.hc:1.h1",
    "urn:cts:aflibre:af.ah.hc:1.n1",
    "urn:cts:aflibre:af.ah.hc:1.n2",
    "urn:cts:aflibre:af.ah.hc:1.n3",
    "urn:cts:aflibre:af.ah.hc:1.n4",
    "urn:cts:aflibre:af.ah.hc:1.n5",
    "urn:cts:aflibre:af.ah.hc:1.1.h1",
    "urn:cts:aflibre:af.ah.hc:1.1.1.h1",
    "urn:cts:aflibre:af.ah.hc:1.1.1.n1",
    "urn:cts:aflibre:af.ah.hc:1.1.2.h1",
    "urn:cts:aflibre:af.ah.hc:1.1.2.n1",
    "urn:cts:aflibre:af.ah.hc:1.1.2.n2",
    "urn:cts:aflibre:af.ah.hc:1.1.2.n3",
    "urn:cts:aflibre:af.ah.hc:1.1.2.n4",
    "urn:cts:aflibre:af.ah.hc:1.2.h1",
    "urn:cts:aflibre:af.ah.hc:1.2.1.h1",
    "urn:cts:aflibre:af.ah.hc:1.2.1.n1",
    "urn:cts:aflibre:af.ah.hc:1.2.2.h1",
    "urn:cts:aflibre:af.ah.hc:1.2.2.n1"
    ]

    assert actualUrns.size() == expectedUrns.size()
    assert actualUrns == expectedUrns


    def lines = o2xf.readLines()
    String actualLine = lines[2]
    def parts = actualLine.split(/#/)
    String actualEntry = parts[4]
    String expectedEntry = """The [OHCO2 model of citable text](http://cite-architecture.github.io/ohco2/) can be implemented in many ways.  The `hocuspocus` library can create a directed graph in RDF for repositories of texts available in local files in any of the following formats:"""
    assert actualEntry == expectedEntry
  }


}
