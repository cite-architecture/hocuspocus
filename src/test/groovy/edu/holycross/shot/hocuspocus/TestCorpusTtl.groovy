package edu.holycross.shot.hocuspocus

// NEEDS TO BE COMPLETED.

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



    File invFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(invFile)
    File archive = new File("testdata/testcorpus2/xml")

    Corpus c = new Corpus(invFile, archive)

    File outDir = new File("testdata/testoutput")

    void testCorpusTtl() {
        c.ttl(new File(outDir, "testout.ttl"), outDir)
    }

}
