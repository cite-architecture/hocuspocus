package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTablesUtil82XF {


  @Test
  void testTablesUtilIndexSeq() {
    File tabs = new File("testdata/il17-tabs.txt")

    def expectedSize = 760

    TablesUtil tu = new TablesUtil()
    def seqIdx = tu.getSequenceIndex(tabs)
    assert seqIdx.size() == expectedSize

    String testIndex = "759"
    String expectedUrnVal = "urn:cts:greekLit:tlg0012.tlg001.msA:17.761"
    assert seqIdx[testIndex] == expectedUrnVal


    // tst conversion to 82XF of single line
    // tst conversion to 82XF of initial line
    // tst conversion to 82XF of final line
    // tst that namespace decl is ignored
    // thro error otherwise?

    // test indexing of urn by seq


  }


  @Test
  void test82XFConversion() {
    File tabs = new File("testdata/il17-tabs.txt")
    TablesUtil tu = new TablesUtil()

    def expectedSize = 760
    String ocho = tu.sevenTo82XF(tabs)
    def ochoLines = ocho.readLines()
    assert ochoLines.size() == expectedSize



    String expectedFirst = """urn:cts:greekLit:tlg0012.tlg001.msA:17.1#urn:cts:greekLit:tlg0012.tlg001.msA:17.1#1#urn:cts:greekLit:tlg0012.tlg001.msA:17.3#<l xmlns="http://www.tei-c.org/ns/1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" n="1" part="N"> οὐδ᾽ ἔλαθ᾽ <persName full="yes" instant="false" n="urn:cite:hmt:pers.pers23">Ἀτρέος</persName> υἱὸν ἀρηΐφιλον <persName full="yes" instant="false" n="urn:cite:hmt:pers.pers119">Μενέλαον</persName></l>"""
  def actualFirst = ochoLines[0]
  assert actualFirst == expectedFirst

  }


}
