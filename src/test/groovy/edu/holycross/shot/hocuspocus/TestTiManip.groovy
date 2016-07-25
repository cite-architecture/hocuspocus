package edu.holycross.shot.hocuspocus


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.CtsUrn

/** Class to test cite library's CiteCollection class.
*/
class TestTiManip{

  TextInventory ti = new TextInventory(new File("testdata/textinvtests/tiwexemplar.xml"))

//urn:cts:greekLit:tlg0012.tlg001.testlines.lextokens:



  @Test
  void testBooleans() {
    CtsUrn exemplarUrn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines.lextokens:")
    assert ti.urnInInventory(exemplarUrn)
    System.err.println ("Found URN at level " + exemplarUrn.workLevel)

    CtsUrn versionUrn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines:")
    assert ti.urnInInventory(versionUrn)
    System.err.println ("Found URN at level " + versionUrn.workLevel)

    CtsUrn workUrn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001:")
    assert ti.urnInInventory(workUrn)
    System.err.println ("Found URN at level " + workUrn.workLevel)

    CtsUrn groupUrn = new CtsUrn("urn:cts:greekLit:tlg0012:")
    assert ti.urnInInventory(groupUrn)
    System.err.println ("Found URN at level " + groupUrn.workLevel)


    CtsUrn absent = new CtsUrn("urn:cts:madeUp:noGroup.noWork:")
    assert shouldFail {
      assert absent.urnInInventory(exemplarUrn)
    }

  }




}
