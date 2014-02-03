package edu.holycross.shot.hocuspocus

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

/**
*/
class TestLiteralTokenGen extends GroovyTestCase {


    File tiFile = new File( "testdata/testcorpus2/testinventory2.xml")

    TextInventory inv = new TextInventory(tiFile)
    File archiveDir = new File("testdata/testcorpus2/xml")
    File iliadAFile = new File(archiveDir, "A_Iliad_testlines.xml")

    String sepChar = "#"

    File outDir = new File("testdata/testoutput")

    String expectedDescription = "Generates a tokenized edition of a text from the output of a classified tokenization by treating each token as a citable node of the new edition.  Makes ranges in the token edition legible by appending a white space to each token, and include a token with a special value at the end of each citation node in the source edition."

    Integer expectedTokens = 23
    Integer expectedBlocks = 4

    @Test void testConstructor() {
       outDir.deleteDir()
       outDir.mkdir()
       
       LiteralTokenEditionGenerator lteg = new LiteralTokenEditionGenerator()
       assert lteg

       assert lteg.getDescription() == expectedDescription
    }


    @Test void testGenerate() {
      outDir.deleteDir()
      outDir.mkdir()
        

      File tabulatedOutput = new File("testdata/tab-to-tokenize.txt")
      HmtGreekTokenization tokeSys = new HmtGreekTokenization()
      ArrayList tokens = tokeSys.tokenize(tabulatedOutput, sepChar)


      assert tokens.size() == expectedTokens

      //outDir.setWritable(true)
      File tkFile = new File(outDir,"tokenization.txt")
      tokens.each { t ->
	tkFile.append(t[0] + "\t" + t[1] + "\n", "UTF-8")
      }

      LiteralTokenEditionGenerator leg = new LiteralTokenEditionGenerator()
      leg.generate(tkFile, "\t", outDir)

      File editionFile = new File(outDir, "tokenedition.txt")
      File ttlFile = new File(outDir, "tokenToSourceEdition.ttl")

      assert editionFile.readLines().size() == expectedTokens + expectedBlocks

      assert ttlFile.readLines().size() ==  expectedTokens * 2

    }


}

