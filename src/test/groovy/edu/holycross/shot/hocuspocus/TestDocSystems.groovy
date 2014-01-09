package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestDocSystems extends GroovyTestCase {


    File tiFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(tiFile)


    File archiveDir =  new File("testdata/testcorpus2/xml")
    File iliadAFile = new File(archiveDir, "A_Iliad_testlines.xml")

    File outDir = new File("testdata/testoutput")

    @Test void testDefaults() {
        outDir.deleteDir()
        outDir.mkdir()


        TokenizationSystem ts = new DefaultTokenizationSystem()
        assert ts
        CtsUrn urn = new CtsUrn("urn:cts:test1:test.unclassified.unittest")

        File testFile = new File("testdata/testcorpus2/xml/no-namespace.xml")
        DocumentConfiguration docConf = new DefaultDocumentConfiguration(urn,inv,testFile)
        assert docConf.getDescription() == "Unclassified document type."
        assert docConf.getId() == "unclassified"

        docConf.setWorkDir(outDir)
        File tokensFile = new File(outDir,"tokens.txt")
        tokensFile.setText("")

        docConf.writeTokenUrns(tokensFile)
        System.err.println "Tokens written to ${tokensFile}"
    }

}
