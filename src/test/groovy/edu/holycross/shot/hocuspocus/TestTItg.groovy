package edu.holycross.shot.hocuspocus


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail

import edu.harvard.chs.cite.CtsUrn

/** Class to test cite library's CiteCollection class.
*/
class TestTItg {

  @Test
  void testTextGroup() {
    TextInventory ti = new TextInventory(new File("testdata/textinvtests/tiwexemplar.xml"))
    assert ti.textgroups.size() == 1

    CtsUrn expectedUrn = new CtsUrn("urn:cts:greekLit:tlg0012:")
    def tgpair = ti.textgroups[0]
    CtsUrn actualUrn = new CtsUrn(tgpair[0])
    assert expectedUrn.toString() == actualUrn.toString()

    String expectedLabel = "Homer"
    assert expectedLabel == tgpair[1].replaceAll(" ", "")
  }



}
