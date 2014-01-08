package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestLatin26 extends GroovyTestCase {

    @Test void testInterface() {
        Latin26 greek = new Latin26()
        assert greek.getLanguageCode() == "lat"
        assert greek.isValidString("Valete")
    }
}
