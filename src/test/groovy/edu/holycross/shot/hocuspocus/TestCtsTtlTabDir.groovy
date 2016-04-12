package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestCtsTtlTabDir  {

  File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")

  //TextInventory ti = new TextInventory(tiFile)

  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")

  File baseDir = new File("testdata/testcorpus2016/xml")

  File schemaFile = new File("testdata/conf2016/TextInventory.rng")

  //CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)
  //TtlGenerator ttler = new TtlGenerator(ti, conf)



  @Test
  void testTabCorpus() {
    File outDir = new File("testdata/output")
    Corpus corp = new Corpus (tiFile,confFile,baseDir,schemaFile)
    System.err.println "In archive? " + corp.filesInArchive().size() + " (${corp.filesInArchive()})"

    System.err.println "In inventory? " + corp.filesInArchive().size() + " (${corp.filesInArchive()})"

    //corp.tabulateRepository(outDir)
  }


}
