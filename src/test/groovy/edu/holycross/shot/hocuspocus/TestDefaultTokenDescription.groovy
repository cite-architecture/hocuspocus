package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestDefaultTokenDescription extends GroovyTestCase {

    @Test void testDescription() {
        DefaultTokenizationSystem ts = new DefaultTokenizationSystem()

        String expectedDescription =  "Default TokenizationSystem, splitting tokens on white space, but making no distinction among non-whitespace characters, and classifying all tokens as 'unclassified'."

        assert ts.getDescription().size() == expectedDescription.size()
    }

}
