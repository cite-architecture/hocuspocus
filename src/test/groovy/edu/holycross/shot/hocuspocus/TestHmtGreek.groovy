package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestHmtGreek extends GroovyTestCase {
    /* Test values: */
    File testOnlineTIFile = new File("testdata/testOnlineCorpusTextInv.xml")
    TextInventory inv = new TextInventory(testOnlineTIFile)
    File archiveDir = new File("testdata/testArchive")
    File iliadAFile = new File(archiveDir, "A_Iliad_testlines.xml")

    File outDir = new File("testdata/testoutput")


    @Test void testInterface() {
        HmtGreek greek = new HmtGreek()
        assert greek.getLanguageCode() == "grc"
        //assert greek.isValid("Mhnin")

        CtsUrn urn = new CtsUrn("urn:cts:test1:test.unclassified.unittest")
    
        File testFile = new File(archiveDir, "no-namespace.xml")
        DocumentConfiguration docConf = new HmtGreekPoetry(urn,inv,testFile)
//        assert docConf.getDescription() == "Unclassified document type."
//        assert docConf.getId() == "unclassified"




        docConf.setWorkDir(new File("build"))

        File tokensDir = new File("testdata/testoutput")
        if (! tokensDir.exists()) {
            tokensDir.mkdir()
        }
        File tokensFile = new File(tokensDir,"tokens.txt")
        tokensFile.setText("")

        docConf.writeTokenUrns(tokensFile)
        System.err.println "Tokens written to ${tokensFile}"

    }
}
