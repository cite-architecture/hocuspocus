package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestToken2 extends GroovyTestCase {


    File invFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(invFile)
    File archiveDir = new File("testdata/testcorpus2/xml")

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
        ArrayList tokens = tokeSys.tokenize(tabulatedOutput, sepChar)
        Integer expectedTokens = 158
        assert tokens.size() == expectedTokens


	File tokensFile = new File(outDir, "tokenization.tsv")
	tokens.each { pr ->
	  tokensFile.append("${pr[0]}\t${pr[1]}\n")
	}

    }


}

