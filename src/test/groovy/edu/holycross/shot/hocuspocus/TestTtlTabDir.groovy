package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestTtlTabDir  {

  File tiFile = new File("testdata/testpipe/pipelineinv.xml")

  //TextInventory ti = new TextInventory(tiFile)

  File confFile = new File("testdata/testpipe/pipelinecite.xml")

  File baseDir = new File("testdata/testpipe/xml")

  File schemaFile = new File("testdata/testpipe/TextInventory.rng")

  //CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)
  //CtsTtl ttler = new CtsTtl(ti, conf)



  @Test
  void testTabCorpus() {
    File outDir = new File("testdata/output")
    Corpus corp = new Corpus (tiFile,confFile,baseDir,schemaFile)
    System.err.println "In archive? " + corp.filesInArchive().size() + " (${corp.filesInArchive()})"

    System.err.println "In inventory? " + corp.filesInArchive().size() + " (${corp.filesInArchive()})"

    corp.tabulateRepository(outDir)
  }


}
