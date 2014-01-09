package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn



/**  Tests generating a TTL representation of a single file
* by first tabulating the file, then turtelizing the tabulated
* representation.
* It writes the UTF-8 output to a file in testdata/testout,
* so the real testing of this as served from a SPARQL endpoint
* could proceed from there.
*/
class TestSingleEditionTtl extends GroovyTestCase {
    File outDir = new File("testdata/testoutput")



    File invFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(invFile)
    File archive = new File("testdata/testcorpus2/xml")

    Corpus c = new Corpus(invFile, archive)

    void testOne() {
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

}
