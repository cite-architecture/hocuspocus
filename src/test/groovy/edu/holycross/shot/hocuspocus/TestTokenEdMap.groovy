package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn 

/**
*/
class TestTokenEdMap extends GroovyTestCase {

    
    File invFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(invFile)
    File archiveDir = new File("testdata/testcorpus2/xml")


    String greekUrn = "urn:cts:greekLit:tlg0012.tlg001.testlines"
    String engUrn = "urn:cts:test1:test.unclassified"

    @Test void testMapping() {
        Corpus c = new Corpus(invFile, archiveDir)
        String actualLangCode = c.inventory.languageForVersion(greekUrn)

        String expectedGreek = "edu.holycross.shot.hocuspocus.LiteralTokenEditionGenerator"
        assert c.languageToTokenSystemMap[actualLangCode] == expectedGreek
    }


}
