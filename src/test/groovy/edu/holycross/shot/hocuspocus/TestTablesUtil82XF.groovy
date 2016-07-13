package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

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
    assert ocho.readLines().size() == expectedSize

  }


}
