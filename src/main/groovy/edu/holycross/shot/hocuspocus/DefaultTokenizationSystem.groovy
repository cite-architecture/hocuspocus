package edu.holycross.shot.hocuspocus

import org.apache.commons.io.FilenameUtils
import edu.harvard.chs.f1k.GreekNode

/**
*/
class DefaultTokenizationSystem implements TokenizationSystem {


  Integer debug = 0

  /** Implements required getDescription() method of TokenizationSystem interface.
   */
  String getDescription() {
    return "Default TokenizationSystem, splitting tokens on white space, but making no distinction among non-whitespace characters, and classifying all tokens as 'unclassified'."
  }

  /** Tokenizes a String on white space.  Uses a GreekNode object from the
   * first thousand years of Greek library to collect text nodes from any XML
   * markup in str, then splits the resulting String on white space.
   * @param str The String to tokenize.
   * @returns An ordered list of Strings.
   */
  ArrayList tokenizeString(String str) {
    GreekNode gn = new GreekNode(str)
    return gn.collectText().split(/[ \t\n]+/)
  }    
    
  
  /** Implements the required tokenize() method tokenizing a tabulated representation of a text.
   * In the DefaultTokenizationSystem, the tokenizing algorithm is a simple split on white space.
   * @param inputFile File with the tabular representation of the text.
   * @param separatorStr String used to separate columns in the tabular file.
   * @returns An ordered list of two-item lists consisting of a string and a class.
   */
  ArrayList tokenize(File inputFile, String separatorStr) {
    if (debug > 0) { System.err.println "TOkenizeing " + inputFile  + "with sepchar " + separatorStr}
    def replyList = []
    def taxon = "lexical"
    inputFile.eachLine { l ->
      if (debug > 0) { System.err.println "TOkenizeing " + inputFile }
      def cols = l.split(/${separatorStr}/)

      if (debug > 0) { System.err.println "${cols.size()} cols for input line " + l }
      def urnBase = cols[0]
      if (cols.size() > 5) {
	def stringTokens = tokenizeString(cols[5])
	def tokenCounts = [:]
	stringTokens.each { t ->
	  if (tokenCounts[t]) {
	    tokenCounts[t] = tokenCounts[t] + 1
	  } else {
	    tokenCounts[t] = 1
	  }
	  if (t.size() > 0) {
	    def reply = [urnBase + "@" + "${t}[${tokenCounts[t]}]", taxon]
	    replyList.add(reply )
	  }
	}
      } else {
	System.err.println "DefaultTokenizationSystem: OMIT non-data line: ${l}"
      }
    }
    return replyList
  }


  /** Empty constructor.
   */
  DefaultTokenizationSystem() {
  }

}
