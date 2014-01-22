package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn 

/**
*/
class TestTokenizationMap extends GroovyTestCase {

    
    File invFile = new File( "testdata/testcorpus2/testinventory2.xml")
    TextInventory inv = new TextInventory(invFile)
    File archiveDir = new File("testdata/testcorpus2/xml")


    String greekUrn = "urn:cts:greekLit:tlg0012.tlg001.testlines"
    String engUrn = "urn:cts:test1:test.unclassified"

    @Test void testMapping() {
        Corpus c = new Corpus(invFile, archiveDir)
        def tokenSystemMap = ["grc" : "edu.holycross.shot.hocuspocus.HmtGreekTokenization"]
        c.languageToTokenSystemMap = tokenSystemMap.clone()

        String actualLangCode = c.inventory.languageForVersion(greekUrn)
        assert (tokenSystemMap.keySet().contains(actualLangCode))

        String expectedGreek = "edu.holycross.shot.hocuspocus.HmtGreekTokenization"
        assert c.languageToTokenSystemMap[actualLangCode] == expectedGreek

        assert "eng" == c.inventory.languageForWork(engUrn)
    }

    @Test void testResolution() {
        Corpus c = new Corpus(invFile, archiveDir)
        def tokenSystemMap = ["grc" : "edu.holycross.shot.hocuspocus.HmtGreekTokenization"]
        c.languageToTokenSystemMap = tokenSystemMap.clone()
        
        String expectedGreek = "edu.holycross.shot.hocuspocus.HmtGreekTokenization"
        assert c.tokenSystemForUrn(new CtsUrn(greekUrn)) == expectedGreek

        String expectedDefault = "edu.holycross.shot.hocuspocus.DefaultTokenizationSystem"
        assert c.tokenSystemForUrn(new CtsUrn(engUrn)) == expectedDefault        

        // OK to change mappings dynamically:
        String fakeSystem = "completely.fake.class.Name"
        def fakeMap = ["eng" : fakeSystem]
        c.languageToTokenSystemMap = fakeMap.clone()
        assert c.tokenSystemForUrn(new CtsUrn(engUrn)) == fakeSystem        

    }

}
