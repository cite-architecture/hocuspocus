package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestDefaultLWCset extends GroovyTestCase {

    @Test void testInterface() {
        DefaultLanguageWritingCharset lwcset = new DefaultLanguageWritingCharset()
        assert lwcset.getLanguageCode() == null
        assert lwcset.getCharSet() == "UTF-8"
        assert lwcset.isValidString("Mhnin")
    }

}
