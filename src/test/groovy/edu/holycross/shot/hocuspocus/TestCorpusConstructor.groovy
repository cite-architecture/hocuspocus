package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestCorpusConstructor extends GroovyTestCase {


    File tiFile = new File( "testdata/testcorpus/testinventory.xml")
    File xml = new File("testdata/testcorpus/xml")

    /** Tests parameter requirements for constructor
    * with valid CTS TextInventory and readable text archive.
    */
    @Test void testConstructor() {


        Corpus c = new Corpus(tiFile, xml)
        assert c

        File fake = new File("fake-inv-file")
        shouldFail {
            Corpus failedCorpus = new Corpus(fake, dir)

        }
        shouldFail {
            Corpus failedCorpus = new Corpus(inv, fake)
        }
    }


    /** Tests requirement that TextInventory validates
    * against schema.
    */
    @Test void testValidator() {
        Corpus c = new Corpus(tiFile, xml)
        c.validateInventory()

        File badinv = new File("testdata/bogusTI.xml")
        shouldFail {
            Corpus badcorpus = new Corpus(badinv, xml)
        }
    }
}
