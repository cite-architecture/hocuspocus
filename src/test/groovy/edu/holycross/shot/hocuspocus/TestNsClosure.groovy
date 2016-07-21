package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test


import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestNsClosure extends GroovyTestCase {



    void testNsMapping() {
      Tabulator t = new Tabulator()

      String expectedKey = "tei"
      String expectedValue = "http://www.tei-c.org/ns/1.0"

      def nsMaps = t.getNSMaps()
      // Only one predefined namespace: tei
      assert nsMaps.size() == 1
      assert nsMaps.keySet()[0] == expectedKey
      assert nsMaps[expectedKey] == expectedValue
    }

}
