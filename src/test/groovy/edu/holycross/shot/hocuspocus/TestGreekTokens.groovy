package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test
import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn


/**
*/
class TestGreekTokens extends GroovyTestCase {

    File invFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(invFile)
    File archiveDir = new File("testdata/testcorpus2/xml")

    File iliadAFile = new File(archiveDir, "A_Iliad_testlines.xml")

    File outDir = new File("testdata/testoutput")


    @Test void testGreekTokenizing() {
        TokenizationSystem ts = new HmtGreekTokenization()
        assert ts
        CtsUrn urn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines")
        File testFile = new File(archiveDir, "A_Iliad_testlines.xml")
        Tabulator tab = new Tabulator()
        tab.tabulate(urn,inv,testFile,new File("build"))


        DocumentConfiguration docConf = new HmtGreekPoetry(urn,inv,testFile)

        assert docConf.getDescription() == "Greek document in CHS/HMT conventions."
        assert docConf.getId() == "hmttei"


        docConf.setWorkDir(new File("build"))

        File tokensDir = new File("testdata/greektest")
        if (! tokensDir.exists()) {
            tokensDir.mkdir()
        }
        File tokensFile = new File(tokensDir,"tokens.txt")
        tokensFile.setText("")

        System.err.println "Writing tokens to ${tokensFile} using a ${docConf.getClass()}"
        docConf.writeTokenUrns(tokensFile)


    }
}
