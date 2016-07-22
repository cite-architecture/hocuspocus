package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail



import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestTwoColTabulate{

  File tiFile = new File("testdata/2cols/franktextinventory.xml")

  File confFile = new File("testdata/2cols/franktextconfig.xml")

  File baseDir = new File("testdata/2cols/archive")

  File schemaFile = new File("testdata/schemas/TextInventory.rng")

  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)

  @Test
  void testTabCorpus() {
    File outDir = new File("testdata/output")
    Corpus corp = new Corpus (tiFile,confFile,baseDir,schemaFile)
    //println "For archive ${baseDir}, files in archive: " + corp.filesInArchive()
    //println "For archive ${baseDir}, files in inventory: " + corp.filesInInventory()

    assert corp.filesInArchive().size()  == 1
    assert corp.filesAndInventoryMatch()


    // Clean up all files used in test:
    /*
      ttl.delete()
    */

    // This is sloppily created by Tabulator, and
    // should be properly cleaned up there. :-(
    File tabDir = new File(outDir, "tabFiles")
    tabDir.deleteDir()
  }

}
