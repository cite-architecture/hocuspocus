package edu.holycross.shot.hocuspocus


import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail



import edu.harvard.chs.cite.CtsUrn

/**
*
*/
class TestTabGen  {

  File invFile = new File("testdata/testcorpus2016/testinventory-2016.xml")
  TextInventory inv = new TextInventory(invFile)

  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

/*
  void oldOne() {

    XmlTabulator tab = new XmlTabulator()
    CtsUrn txtUrn = new CtsUrn("urn:cts:greekLit:tlg0012.tlg001.testAllen:")
    File txtFile = new File("testdata/testcorpus2016/xml/Iliad-Allen.xml")

    def tabulated = tab.tabulateFile(txtUrn, inv, conf, txtFile)
    System.err.println "Tabulated: " + tabulated
    File allen = new File("build/allen-tabs.txt")

    allen.setText(tabulated, "UTF-8")
    System.err.println "No lines: " + allen.readLines().size()
  }
*/
  @Test
  void testGenTestSet() {
    XmlTabulator tab = new XmlTabulator()
    File log = new File("testdata/output/log.txt")
    log.text = ""
    tab.log = log
    tab.debug = 1

    File ttloutput = new File("testdata/output/testset.ttl")
    def urnsToTab = [
            "urn:cts:greekLit:tlg0012.tlg001.testAllen:",
      "urn:cts:greekLit:tlg0012.tlg001.testAllen.wt:",
      "urn:cts:greekLit:tlg0016.tlg001.grcTest:",
      "urn:cts:greekLit:tlg0016.tlg001.grcTest.wt:",
      //"urn:cts:greekLit:tlg0016.tlg001.grcTokFull:",
      "urn:cts:greekLit:tlg0016.tlg001.engTest:",
      "urn:cts:greekLit:tlg0016.tlg001.engTest.wt:"
    ]

    def filesToTab = [
      "testdata/testcorpus2016/xml/Iliad-Allen.xml",
       "testdata/testcorpus2016/xml/Iliad-Allen-tokens.xml",
      "testdata/testcorpus2016/xml/test-hdt-grc.xml",
    "testdata/testcorpus2016/xml/test-hdt-grc-tok.xml",
     //"testdata/testcorpus2016/xml/fu-hdt-grc-tok.xml",
    "testdata/testcorpus2016/xml/test-hdt-eng.xml",
    "testdata/testcorpus2016/xml/test-hdt-eng-tok.xml"
    ]

    urnsToTab.eachWithIndex { u, i ->
      CtsUrn txtUrn = new CtsUrn(u)
      File txtFile = new File(filesToTab[i])
      //def tabulated = tab.tabulateFile(txtUrn, inv, conf, txtFile)
      //System.err.println "${u} ->\n" + tabulated
      //ttloutput.append(tabulated)
    }

    // inspect contents of log file, then clean up:
    log.delete()
  }
}
