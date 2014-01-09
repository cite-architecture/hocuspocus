package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestTtl extends GroovyTestCase {


    File outDir = new File("testdata/testoutput")


    File invFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(invFile)
    File archive = new File("testdata/testcorpus2/xml")

    Corpus c = new Corpus(invFile, archive)

    void testTtlCycle() {
        TextInventory ti = new TextInventory(invFile.getText())
        Tabulator t = new Tabulator()
        CtsUrn urn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testlines")

        File iliadFile = new File(archive, "A_Iliad_testlines.xml")
        t.tabulate(urn, ti, iliadFile, "testdata")


        File tabs = new File("testdata/A_Iliad_testlines-00001.txt")
        CtsTtl ctsTtl = new CtsTtl(ti)
        def ttl = ctsTtl.turtleizeTabs(tabs)

        File ttlTestOut = new File("testdata/testoutput/iliadTtlOutput.ttl")
        ttlTestOut.append(ttl, "UTF-8")
        
    }

    void testInvTtl() {
        outDir.deleteDir()
        outDir.mkdir()
        TextInventory ti = new TextInventory(invFile.getText())
        CtsTtl ctsTtl = new CtsTtl(ti)
        System.err.println "INV TTL:\n" + ctsTtl.turtleizeInv()
    }

    void testCorpusTtl() {
        outDir.deleteDir()
        outDir.mkdir()
        System.err.println "TURTLIZE WHOLE CORPUS INTO " + outDir
        c.ttl(new File(outDir, "testout.ttl"), outDir)
    }

}
