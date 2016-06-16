package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTablesUtil {


  @Test
  void testTablesUtil() {
    File tabs = new File("testdata/il17-tabs.txt")
    String txtUrn = "urn:cts:greekLit:tlg0012.tlg001.msA:17.1"
    TablesUtil tu = new TablesUtil()
    def oneEntry = tu.tabEntryForUrn(tabs, txtUrn )
    Integer expectedColumns = 8

    def cols = oneEntry.split("#")
    assert cols.size() == expectedColumns

  

  }



}
