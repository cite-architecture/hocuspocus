package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestEdGen extends GroovyTestCase {


    File invFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(invFile)
    File archiveDir = new File("testdata/testcorpus2/xml")
    File outDir = new File("testdata/testoutput")

    String docUrn = "urn:cts:greekLit:tlg0012.tlg001.testlines"
    String expectedUrn = "urn:cts:greekLit:tlg0012.tlg001.testlines_tokens"

    @Test void testUrnName() {
        outDir.deleteDir()
        outDir.mkdir()
        TokenizedAnalysisEditionGenerator leg = new TokenizedAnalysisEditionGenerator()
	leg.srcUrnName = docUrn
	assert leg.getUrnName() == expectedUrn
    }


}

