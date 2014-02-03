package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestDefaultCorpusTokenize extends GroovyTestCase {


    File tiFile = new File( "testdata/testcorpus2/testinventory2.xml")

    TextInventory inv = new TextInventory(tiFile)
    File archiveDir = new File("testdata/testcorpus2/xml")
    File iliadAFile = new File(archiveDir, "A_Iliad_testlines.xml")

    String sepChar = "#"

    File outDir = new File("testdata/testoutput")

    Integer expectedLines = 504

    @Test void testCorpus() {

       outDir.deleteDir()
       outDir.mkdir()
       
       Corpus c = new Corpus(tiFile, archiveDir)
       c.tokenizeRepository(outDir)

       File resultFile = new File(outDir, "tokens.tsv")
       assert resultFile.readLines().size() == expectedLines
        
    }


    @Test void testCorpusWithSpecifiedSystem() {
        outDir.deleteDir()
        outDir.mkdir()

        Corpus c = new Corpus(tiFile, archiveDir)
        DefaultTokenizationSystem tokenSystem = new DefaultTokenizationSystem()
        c.tokenizeRepository(tokenSystem, outDir)

       File resultFile = new File(outDir, "tokens.tsv")
       assert resultFile.readLines().size() == expectedLines
    }
}

