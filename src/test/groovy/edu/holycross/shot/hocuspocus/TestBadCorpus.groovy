package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/** Class testing hocuspocus Corpus class, including a number
* of tests for bad input.
*/
class TestBadCorpus extends GroovyTestCase {


    File tiFile = new File( "testdata/corpus/testinventory.xml")
    File xml = new File("testdata/corpus/xml")

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
