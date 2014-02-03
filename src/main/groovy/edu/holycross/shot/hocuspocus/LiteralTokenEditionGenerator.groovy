package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn

/*
Tab format is:
URN#CURRCOUNT#PREVCOUNT#NEXTCOUNT#WRAP#TEXT#XP


   // These are the responsibility of some other class:
    //- a file named "inventory-supplement.xml" with a well-formed XML
    // - a file named "tokenedition.ttl" that translates that into a
    // TTL representation

*/



/** Class implementing the TokenEditionGenerator interface.
*/
class LiteralTokenEditionGenerator implements TokenEditionGenerator {


  /** Character encoding to use for reading and writing files. */
  String charEnc = "UTF-8"

  /** String to use when demarking columns of tabular files. */
  String tab = "#"

  /** String to use for null value in columns of tabular files. */
  String nullCol = " "

  /** String to use as text value of token representing end of 
   * source editions' citation block.
   */
  String endBlockMarker = "<br/>"


  /** Name of file with tabular representation of tokenized edition. */
  String tokenEditionName = "tokenedition.txt"

  /** Name of file with TTL statements mapping tokenized edition nodes
   * to node of source edition. */
  String tokenIndexName = "tokenToSourceEdition.ttl"


  /** Empty constructor */
  LiteralTokenEditionGenerator() {
  }

  /** Constructs a line to append at the end of citation units in the
   * source edition.
   * @param baseUrn URN of the citable node in the source edition.
   * @param i Running index count for the node.
   * @returns A string in HMT project's tabular format.
   */
  String endBlock(String baseUrn, Integer i) {
    return ("${baseUrn}.${i}${tab}${i}${tab}${i - 1}${tab}${i + 1}${tab}${nullCol}${tab}${endBlockMarker}${tab}${nullCol}${tab}\n")
  }


  /** Returns a human-readable description of the system
   * for generating token editions derived from and aligned 
   * with a source edition. 
   */
  String getDescription() {
    return "Generates a tokenized edition of a text from the output of a classified tokenization by treating each token as a citable node of the new edition.  Makes ranges in the token edition legible by appending a white space to each token, and include a token with a special value at the end of each citation node in the source edition."
  }


  /** Generates a tokenized edition from a tabulated representation of a text.
   * It creates the following artifacts in outputDirectory:
   * - a file with the tabular representation of
   * the tokenized edition
   * - a file with TTL mapping the citable nodes of the token edition to the
   * source edition.
   * @param inputFile File with the tabular representation of the text.
   * @param separatorStr String used to separate columns in the tabular file.
   * @param outputDirectory A writable directory where output will be created.
   */
  void generate(File inputFile, String separatorStr, File outputDirectory) {
    File outFile = new File(outputDirectory, tokenEditionName)
    File idxFile = new File(outputDirectory, tokenIndexName)

    Integer count = 0    
    Integer prevCount = 0
    Integer prevPrevCount = 0
    String prevText = ""
    String prevUrn = ""

    boolean sawEndBlock = false
    
    String currUrnBase = ""

    inputFile.getText(charEnc).eachLine { l ->
      def cols = l.split("${separatorStr}")
      CtsUrn urn = new CtsUrn(cols[0])
      String baseUrn = urn.getUrnWithoutPassage() + ":" + urn.getPassageNode()

      if (sawEndBlock) {
	if ( prevUrn != "") {
	  outFile.append(endBlock(prevUrn, prevCount))
	}
	prevUrn = baseUrn
	count++;
	prevCount++;
	prevPrevCount++;
	sawEndBlock = false
      }

      if (baseUrn != prevUrn) {
	sawEndBlock = true
      }

      
      //  URN#CURRCOUNT#PREVCOUNT#NEXTCOUNT#WRAP#TEXT#XP
      if (count > 1) {
	idxFile.append("${prevUrn} hmt:tokenizesTo ${prevUrn}.${prevCount} .\n")
	idxFile.append("${prevUrn}.${prevCount} hmt:tokenizedFrom ${prevUrn}.\n")

	outFile.append(prevUrn + ".${prevCount}", charEnc)
	if (prevPrevCount == 0) {
	  outFile.append("${prevCount}${tab}${nullCol}${tab}${count}${tab}${nullCol}${tab}${prevText}${tab}${nullCol}${tab}\n", charEnc)
	} else {
	  outFile.append("${tab}${prevCount}${tab}${prevPrevCount}${tab}${count}${tab}${prevText}${tab}${nullCol}${tab}\n", charEnc)
	}
      }
      
      prevPrevCount = prevCount;
      prevCount = count;
      count++;

      prevText = urn.getSubref1()
    }

    idxFile.append("${prevUrn} hmt:tokenizesTo ${prevUrn}.${prevCount} .\n")
    idxFile.append("${prevUrn}.${prevCount} hmt:tokenizedFrom ${prevUrn}.\n")

    outFile.append(prevUrn + ".${prevCount}", charEnc)
    outFile.append("${tab}${prevCount}${tab}${prevPrevCount}${tab}${nullCol}${tab}${prevText}${tab}${nullCol}${tab}\n", charEnc)
  }

}
