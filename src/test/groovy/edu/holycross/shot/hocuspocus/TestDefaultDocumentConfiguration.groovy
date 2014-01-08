package edu.holycross.shot.hocuspocus


import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestDefaultDocumentConfiguration extends GroovyTestCase {

    TextInventory ti  = new TextInventory(new File("testdata/testOnlineCorpusTextInv.xml"))

    /** Tests behavior of interface with meaningless configuration.
    */
    @Test void testInterface() {
        File srcFile = new File("fake")
        CtsUrn docUrn = new CtsUrn("urn:cts:ns:group.work:psg")

        DefaultDocumentConfiguration ddt =  new DefaultDocumentConfiguration(docUrn, ti, srcFile) 

        String expectedDescription = "Unclassified document type."
        assert ddt.getDescription() == expectedDescription

        String expectedId = "unclassified"
        assert ddt.getId() == expectedId

        /* Validation requires specific subclass with validation criteria:
        default document configuration cannot be validated. */
        assert ddt.isValid() == false

        assert shouldFail {
            ddt.writeTokenUrns(new File("fakeoutput"))
        }
    }

    @Test void testRealDocument() {

        File sampleIliadLines = new File ("testdata/testArchive/A_Iliad_testlines.xml")
        CtsUrn urn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines")

        DefaultDocumentConfiguration ddt =  new DefaultDocumentConfiguration(urn, ti, sampleIliadLines)

        File outDir = new File("testdata/testoutput")
        if (! outDir.exists()) {
            outDir.mkdir()
        }
        ddt.workDir = outDir
        ddt.writeTokenUrns("test-tokenize-output.txt")
        // analyze this output...

        
    }
}
