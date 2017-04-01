package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail



import edu.harvard.chs.cite.CtsUrn

class TestCtsTtlCycle  {


  File tabFile = new File("testdata/testcorpus2016/derivs/testcorpus2016.tab")
  File editionTabFile = new File("testdata/testcorpus2016/derivs/iliadEdition.txt")
  File exemplarTabFile = new File("testdata/testcorpus2016/derivs/iliadExemplar.txt")

  File tiFile = new File("testdata/testcorpus2016/testinventory-2016.xml")
  File confFile = new File("testdata/testcorpus2016/citationconfig-2016.xml")

  File ttlOut = new File("testdata/output/test-cts-editionTtl.ttl")

	/*
  @Test
  void testTtlExemplarFile() {

    CitationConfigurationFileReader conf = new CitationConfigurationFileReader(confFile)
    TextInventory ti = new TextInventory(tiFile)
    CtsTtl ttler = new CtsTtl(ti, conf)
    String ttl = ttler.turtleizeFile(exemplarTabFile,true)
    //System.err.println "From tabFile:\n" + ttl
    File ttlOut = new File("testdata/output/test-exemplar-file.ttl")
    ttlOut.setText(ttl, "UTF-8")

		//    System.err.println "EXMAINE:\n"  + ttl

    // inspect contents, count RDF verbs:


    ttlOut.delete()
	}
	*/

  @Test
  void testTtl2ColFile2() {

  	File twoColTiFile = new File("testdata/textinvtests/testinventory-cwb.xml")
	  File twoColConfFile = new File("testdata/conf2016/citationconfig-cwb.xml")
	  File twocolTabFile = new File("testdata/tabdir/latin_liad-2col.txt")

    CitationConfigurationFileReader conf = new CitationConfigurationFileReader(twoColConfFile)
    TextInventory ti = new TextInventory(twoColTiFile)
    CtsTtl ttler = new CtsTtl(ti, conf)
    String ttl = ttler.turtleizeFile(twocolTabFile,true)
    //System.err.println "From tabFile:\n" + ttl
    File ttlOut = new File("testdata/output/cwb-test-2col-file.ttl")
    ttlOut.setText(ttl, "UTF-8")

    //System.err.println "EXAMINE:\n"  + ttl

    // inspect contents, count RDF verbs:


//    ttlOut.delete()
	}


}
