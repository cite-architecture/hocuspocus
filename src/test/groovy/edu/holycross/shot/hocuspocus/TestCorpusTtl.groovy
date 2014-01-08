package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
* Tests automated generation of TTL representing entire corpus of texts.
* Writes output where it can be further tested by loading into a sparql
* endpoint.
* Compare test in TestTtlForEdition where the TTL is "manually" built
* directly from tabulated source.
*/
class TestCorpusTtl extends GroovyTestCase {


    File outDir = new File("testdata/testoutput")


    String testTIFile = "testdata/testOnlineCorpusTextInv.xml"    
    File invFile = new File(testTIFile)
    File archive = new File("testdata/testArchive")
    Corpus c = new Corpus(invFile, archive)


    void testCorpusTtl() {
        c.ttl(new File(outDir, "testout.ttl"), outDir)
    }

}
