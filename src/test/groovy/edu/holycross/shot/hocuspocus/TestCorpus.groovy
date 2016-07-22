package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail



import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestCorpus  {

  File tiFile = new File("testdata/testcorpfunctions/testinventory-2016.xml")
  File confFile = new File("testdata/testcorpfunctions/citationconfig-2016.xml")
  File baseDir = new File("testdata/testcorpfunctions/xml")
  File schemaFile = new File("testdata/schemas/TextInventory.rng")
  CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)


  @Test
  void testTtlCorpus() {
    File outDir = new File("testdata/output")
    Corpus corp = new Corpus (tiFile,confFile,baseDir,schemaFile)
    assert corp.filesInArchive().size()  == 1
    assert corp.filesInArchive().size()  == 1

    corp.turtleizeRepository(outDir,true)

    File ttl = new File(outDir,"cts.ttl")

    // Count occurrences of RDF verbs in output:
    def lines = 0
    def  prefix = 0
    def prev = 0
    def nxt = 0
    def possess = 0
    def belongs = 0
    def dcterms = 0
    def isPsg = 0
    def hasPsg = 0
    def txtContent = 0
    def seq = 0
    def contains = 0
    def containedBy = 0
    def abbr = 0
    def ctsns = 0
    def ctsuri = 0
    def lang = 0
    def ed = 0


    ttl.eachLine { l ->
      lines++;
      if (l ==~ '@prefix.+') {
	prefix++
	  } else if (l ==~ '.+cts:prev.+') {
        prev++;
      } else if (l ==~ '.+cts:next.+') {
        nxt++;

      } else if (l ==~ '.+cts:possesses.+') {
        possess++;
      } else if (l ==~ '.+cts:belongsTo.+') {
        belongs++;
      } else if (l ==~ '.+dcterms:title.+') {
        dcterms++;
      } else if (l ==~ '.+cts:isPassageOf.+') {
        isPsg++;
      } else if (l ==~ '.+cts:hasPassage.+') {
        hasPsg++;
      } else if (l ==~ '.+cts:hasTextContent.+') {
        txtContent++;
      } else if (l ==~ '.+cts:hasSequence.+') {
        seq++;
      }  else if (l ==~ '.+cts:contains.+') {
        contains++;
      } else if (l ==~ '.+cts:containedBy.+') {
        containedBy++;
      } else if (l ==~ '.+cts:abbreviatedBy.+') {
        abbr++;
      } else if (l ==~ '.+cts:Namespace.+') {
        ctsns++;
      } else if (l ==~ '.+cts:fullUri.+') {
        ctsuri++;
      } else if (l ==~ '.+cts:Edition.+') {
        ed++;
      } else if (l ==~ '.+cts:lang.+') {
        lang++;
      }
    }

    assert prefix == 4
    assert possess == 3
    assert belongs == 3
    assert dcterms == 3
    assert isPsg == 2
    assert hasPsg == 2
    assert seq == 2
    assert txtContent == 2
    assert contains == 2
    assert containedBy == 2
    assert prev == 1
    assert nxt == 1
    assert abbr == 1
    assert ctsns == 1
    assert ctsuri == 1
    assert lang == 1
    assert ed == 1

    // Clean up all files used in test:
    ttl.delete()
    // This is sloppily creawted by XmlTabulator, and
    // should be properly cleaned up there. :-(
    File tabDir = new File(outDir, "tabFiles")
    tabDir.deleteDir()
  }
}
