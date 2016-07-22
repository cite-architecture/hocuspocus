package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTablesUtil82XFto7col {

  @Test
  void testSingleLine() {
    def o2data = """URN#Previous#Sequence#Next#Text
urn:cts:aflibre:aflibre.ah.hc:19420614.h1##1#urn:cts:aflibre:aflibre.ah.hc:19420614.p1#Zondag, 14 Juni 1942
"""
    TablesUtil tu = new TablesUtil()
    String actualSevenCols = tu.o2xfToSeven(o2data)

    String expectedSevelCols = "urn:cts:aflibre:aflibre.ah.hc:19420614.h1#1##urn:cts:aflibre:aflibre.ah.hc:19420614.p1##Zondag, 14 Juni 1942#\n"
    
    assert actualSevenCols == expectedSevelCols
  }


}
