package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestMarkdownTwoColumn {


  @Test
  void testFileTab() {
    File md = new File("testdata/markdown/archive/powered_by_ohco2.md")

    String urn = "urn:cts:aflibre:af.ah.hc:"
    String twocols = MdTabulator.mdFileToTwoColumns(md,urn)
    def lines = twocols.readLines()

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

    def actualUrns = []
    lines.each {
      def cols = it.split(/#/)
      actualUrns.add(cols[0])
    }
    assert expectedUrns == actualUrns
    String actualLine = lines[1]
    def parts = actualLine.split(/#/)
    String actualEntry = parts[1]
    String expectedEntry = """The [OHCO2 model of citable text](http://cite-architecture.github.io/ohco2/) can be implemented in many ways.  The `hocuspocus` library can create a directed graph in RDF for repositories of texts available in local files in any of the following formats:"""

    assert actualEntry == expectedEntry
  }


}
