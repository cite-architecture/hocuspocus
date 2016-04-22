package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestCorpus  {

	File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")

	File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")

	File baseDir = new File("testdata/testcorpus2016/xml")

	File schemaFile = new File("testdata/conf2016/TextInventory.rng")

	CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)



/*	@Test
	void testTabCorpus() {
		File outDir = new File("testdata/output")
		Corpus corp = new Corpus (tiFile,confFile,baseDir,schemaFile)
		System.err.println "In archive? " + corp.filesInArchive().size() + " (${corp.filesInArchive()})"

		System.err.println "In inventory? " + corp.filesInArchive().size() + " (${corp.filesInArchive()})"

		corp.tabulateRepository(outDir)
	}
*/

	@Test
	void testTtlCorpus() {
		File outDir = new File("testdata/output")
		Corpus corp = new Corpus (tiFile,confFile,baseDir,schemaFile)
		System.err.println "In archive? " + corp.filesInArchive().size() + " (${corp.filesInArchive()})"

		System.err.println "In inventory? " + corp.filesInArchive().size() + " (${corp.filesInArchive()})"

		corp.turtleizeRepository(outDir,true)
	}


}
