package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestCorpus  {

	File tiFile = new File("testdata/testcorpfunctions/testinventory-2016.xml")

	File confFile = new File("testdata/testcorpfunctions/citationconfig-2016.xml")

	File baseDir = new File("testdata/testcorpfunctions/xml")

	File schemaFile = new File("testdata/conf2016/TextInventory.rng")

	CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)




	@Test
	void testTtlCorpus() {
		File outDir = new File("testdata/output")
		Corpus corp = new Corpus (tiFile,confFile,baseDir,schemaFile)
		assert corp.filesInArchive().size()  == 1
		assert corp.filesInArchive().size()  == 1

		corp.turtleizeRepository(outDir,true)

		File ttl = new File(outDir,"cts.ttl")


    def lines = 0
    def  prefix = 0
    def prev = 0
    def nxt = 0
		ttl.eachLine { l ->
      lines++
      if (l ==~ '@prefix.+') {
        prefix++
      } else if (l ==~ '.+cts:prev.+') {
        prev++
      } else if (l ==~ '.+cts:next.+') {
        nxt++
      }
		}
    System.err.println "SAW " + lines + " lines."
    assert prefix == 4

    assert prev == 1
    assert nxt == 1



		// there should be ONE cts:next and ONE cts:prev
		// ONE abbreviatedBy, ONE cts:Namespace, ONE fullUri
		// THREE cts:possesses ~ belongsTo, dcterms:title
		// ONE cts:lang
		// TWO cts:isPassageOf ~ hasPassage
		// TWO hasSequence, hasTextContent
		// TWO cts:containedBy ~ contains
		// ONE cts:Edition



    // Clean up all files used in test:
		ttl.delete()
		File tabDir = new File(outDir, "tabFiles")
		tabDir.deleteDir()


	}


}
