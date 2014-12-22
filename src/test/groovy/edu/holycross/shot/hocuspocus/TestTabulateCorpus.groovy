package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/** Class testing hocuspocus Corpus class.
*/
class TestTabulateCorpus extends GroovyTestCase {

    File tiFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(tiFile)
    File archiveDir = new File("testdata/testcorpus2/xml")
    File iliadAFile = new File(archiveDir, "A_Iliad_testlines.xml")

    File outDir = new File("testdata/testoutput")



    @Test void testOneText() {
        outDir.deleteDir()
        outDir.mkdir()

        Corpus c = new Corpus(tiFile, archiveDir)
        CtsUrn urn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines")
        c.tabulateFile(iliadAFile, urn, outDir)

        File tabulatedOutput = new File("testdata/testoutput/A_Iliad_testlines.txt")
        // 22 lines of made up text plus a line for namespace declaration:
        int expectedSize = 23
	
        assert tabulatedOutput.readLines().size() == expectedSize
    }


    /*
    @Test void testFileChunking() {
        outDir.deleteDir()
        outDir.mkdir()

        Tabulator tab = new Tabulator()
        //tab.setChunkSize(2)
        assert tab

        CtsUrn urn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines")
        tab.tabulate(urn, inv, iliadAFile, outDir)
        Integer expectedFileCount = 12
        def actualList = outDir.list({d, f-> f ==~ /.*.txt/ } as FilenameFilter).toList() 

	System.err.println "Corpus is ${inv} and ${outDir}"
        assert actualList.size() == expectedFileCount
    }

    */
    
    @Test void testCorpus() {
        outDir.deleteDir()
        outDir.mkdir()

        Corpus c = new Corpus(tiFile, archiveDir)
        c.tabulateRepository(outDir)
        def actualList = outDir.list({d, f-> f ==~ /.*.txt/ } as FilenameFilter
  ).toList() 
        def expectedSet = ["A_Iliad_testlines.txt", "B_Iliad_test2.txt", "no-namespace.txt"] as Set
        assert (actualList as Set) == expectedSet
    }


}
