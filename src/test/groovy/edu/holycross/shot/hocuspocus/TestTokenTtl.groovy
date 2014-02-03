package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestTokenTtl extends GroovyTestCase {


    File outDir = new File("testdata/testoutput")

    String tokenSource = "testdata/tokenSample.ttl"
    File srcFile = new File(tokenSource)
 

    void testOne() {
        TokensTtl tttl = new TokensTtl(File srcFile, String tokenizer)
        System.err.println tttl
    }


    

}
