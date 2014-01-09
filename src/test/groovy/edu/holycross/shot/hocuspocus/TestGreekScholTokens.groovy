package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test
import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn


/**
*/
class TestGreekScholTokens extends GroovyTestCase {


    File invFile = new File( "testdata/testcorpustokens/inventory.xml")
    TextInventory inv = new TextInventory(invFile)
    File archive = new File("testdata/testcorpustokens/xml")



    @Test void testGreekScholia() {
        TokenizationSystem ts = new HmtGreekTokenization()
        assert ts

        CtsUrn urn = new CtsUrn("urn:cts:greekLit:tlg5026.msA.chs01")


        File testFile = new File("testdata/testcorpustokens/xml/VenetusA-Scholia-A.xml")
//        Tabulator tab = new Tabulator()
//        tab.tabulate(urn,inv,testFile,new File("build"))

        DocumentConfiguration docConf = new HmtGreekPoetry(urn,inv,testFile)

        System.err.println "DESCR: " +  docConf.getDescription() 
        System.err.println "getID() " +  docConf.getId() 

        docConf.setWorkDir(new File("build"))

        File tokensDir = new File("testdata/greektest")
        if (! tokensDir.exists()) {
            tokensDir.mkdir()
        }
        File tokensFile = new File(tokensDir,"scholtokens.txt")
        tokensFile.setText("")

        System.err.println "Writing tokens to ${tokensFile} using a ${docConf.getClass()}"
        docConf.writeTokenUrns(tokensFile)

//        int expectedLines = 43
//        assert tokensFile.readLines().size() == expectedLines
    }
}
