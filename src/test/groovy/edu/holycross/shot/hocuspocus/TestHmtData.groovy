package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestHmtData extends GroovyTestCase {

    /* Test values: */
    File testOnlineTIFile = new File("testdata/testOnlineCorpusTextInv.xml")
    TextInventory inv = new TextInventory(testOnlineTIFile)
    File archiveDir = new File("testdata/testArchive")
    File iliadAFile = new File(archiveDir, "A_Iliad_testlines.xml")

    File outDir = new File("testdata/testoutput")

    @Test void testTokenizer() {
        outDir.deleteDir()
        outDir.mkdir()
        Tabulator t = new Tabulator()
        CtsUrn urn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines")
        t.tabulate(urn, inv, iliadAFile, outDir)
        // compare generic tests..
        /* 22 lines of text, plus a namespace declaration */
        int expectedSize = 23
        File tabOutput = new File(outDir, "A_Iliad_testlines-00001.txt")
        assert tabOutput.readLines().size() == expectedSize

        String sepChar = "#"
        DefaultTokenizationSystem tokeSys = new DefaultTokenizationSystem()
        def tokens = tokeSys.tokenize(tabOutput, sepChar)

        System.err.println "Number of tokens: " + tokens.size()
        expectedSize = 139
//        assert tokens.size() == expectedSize
    }
}
