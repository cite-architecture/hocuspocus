package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestCtsTtlInvProlog {


  CitationConfigurationFileReader confFile = new CitationConfigurationFileReader(new File("testdata/conf2016/citationconfig3.xml"))

  @Test
  void testPrefix() {

    TextInventory ti = new TextInventory(new File("testdata/testcorpus2016/testinventory-2016.xml"))
    String ttlWPrefix = TtlGenerator.turtleizeInv(ti, confFile, true)
    String ttlNoPrefix = TtlGenerator.turtleizeInv(ti, confFile, false)
    Integer expectedPrefixLines = 4
    Integer actualPrefixLines = 0
    ttlWPrefix.eachLine {
      if (it ==~ '@prefix.+') {
        actualPrefixLines++
      }
    }
    assert actualPrefixLines == expectedPrefixLines
    actualPrefixLines = 0
    ttlNoPrefix.eachLine {
      if (it ==~ '@prefix.+') {
        actualPrefixLines++
      }
    }
    assert actualPrefixLines == 0
  }

}
