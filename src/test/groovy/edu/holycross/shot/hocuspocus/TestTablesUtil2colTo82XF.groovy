package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTablesUtil2colTo82XF {


  @Test
  void testTablesUtilIndexSeq() {
    File tabs = new File("testdata/2cols/achterhuis_2cols.txt")

    TablesUtil tu = new TablesUtil()
    String str82xf = tu.twoTo82XF(tabs)

    def  lines = str82xf.readLines()
    assert lines.size() == tabs.readLines().size()

  }




}
