package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestTokenize extends GroovyTestCase {

    File testOnlineTIFile = new File("testdata/testOnlineCorpusTextInv.xml")
    TextInventory inv = new TextInventory(testOnlineTIFile)
    File archiveDir = new File("testdata/testArchive")
    File iliadAFile = new File(archiveDir, "A_Iliad_testlines.xml")

    String sepChar = "#"

    File outDir = new File("testdata/testoutput")

    @Test void testTokenizer() {
        outDir.deleteDir()
        outDir.mkdir()
        
        /* First, generate a tabular file to tokenize: */
        Tabulator tab = new Tabulator()

        CtsUrn urn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines")
        tab.tabulate(urn, inv, iliadAFile, outDir)
        /* 22 lines of text, plus a namespace declaration */
        File tabulatedOutput = new File("testdata/testoutput/A_Iliad_testlines-00001.txt")
        Integer expectedSize = 23
        assert tabulatedOutput.readLines().size() == expectedSize

        /* Then tokenize with default tokenizer */
        DefaultTokenizationSystem tokeSys = new DefaultTokenizationSystem()
        def tokens = tokeSys.tokenize(tabulatedOutput, sepChar)
        System.err.println "Number of tokens: " + tokens.size()
        System.err.println tokens
    }

    @Test void testCorpusTokenizer() {
        outDir.deleteDir()
        outDir.mkdir()

        Corpus c = new Corpus(testOnlineTIFile, archiveDir)
        c.tabulateRepository(outDir)
        DefaultTokenizationSystem tokenSystem = new DefaultTokenizationSystem()
        c.tokenizeInventory(tokenSystem, outDir)
        // test output size: 504?
    }
}

