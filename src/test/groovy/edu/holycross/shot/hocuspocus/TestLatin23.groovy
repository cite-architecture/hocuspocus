package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestLatin23 extends GroovyTestCase {

    @Test void testInterface() {
        Latin23 greek = new Latin23()
        assert greek.getLanguageCode() == "lat"
        assert greek.isValidString("ualete")
    }
}
