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



/** Class implementing the AnalyticalEditionGenerator interface by
 * tokenizing a text.
 */
class TokenizedAnalysisEditionGenerator implements AnalyticalEditionGenerator {

  Integer debug = 0

  String srcUrnName = ""

  String versionExtension = "_tokens"

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


  String getUrnName() {
    return srcUrnName + versionExtension
  } 

  /** Empty constructor */
  TokenizedAnalysisEditionGenerator() {
  }

  /** Formats a line for tabular representation of text.
   * @param baseUrn URN of the citable node in the source edition.
   * @param i Running index count for the node.
   * @returns A string in HMT project's tabular format.
   */
  String formatLine(String baseUrn, String prev, String curr, String nxt, String tokenValue) {
    return ("${baseUrn}.${curr}${tab}${curr}${tab}${prev}${tab}${nxt}${tab}${nullCol}${tab}${tokenValue}${tab}${nullCol}${tab}\n")
  }


  /** Returns a human-readable description of the system
   * for generating token editions derived from and aligned 
   * with a source edition. 
   */
  String getDescription() {
    return "Generates a tokenized edition of a text from the output of a classified tokenization by treating each token as a citable node of the new edition.  Makes ranges in the token edition legible by appending a white space to each token, and include a token with a special value at the end of each citation node in the source edition."
  }


  void generate(File inputFile, String separatorStr, File outputDirectory, String outputFileName) {
    this.tokenEditionName = "${outputFileName}-tokenEdition.txt"
    this.tokenIndexName = "${outputFileName}-tokenToSrcIndex.ttl"
    generate(inputFile, separatorStr, outputDirectory)
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

    //File kludge = new File("/tmp/logtkanalysis.txt")
    //kludge.text = ""
    if (debug > 0) {
      System.err.println "LiteralTokenEditionGenerator:generate:  input ${inputFile}"
    }
    File outFile = new File(outputDirectory, tokenEditionName)
    File idxFile = new File(outputDirectory, tokenIndexName)

    Integer count = 0    
    Integer prevCount = 0
    Integer prevPrevCount = -1
    String prevText = ""
    String prevUrn = ""
    String prevSrc = ""

    boolean sawEndBlock = false
    String currUrnBase = ""
    inputFile.getText(charEnc).eachLine { l ->
      def cols = l.split("${separatorStr}")
      CtsUrn urn
      try {
	urn = new CtsUrn(cols[0])
      } catch (Exception e) {
	System.err.println "TokenizedAnalysisEditionGenerator: omitting data line ${l} from source edition."
      }

      if (urn) {
	this.srcUrnName = urn.getUrnWithoutPassage()
	String baseUrn = this.getUrnName() + ":" + urn.getPassageNode()
	if (debug > 1) {
	  //kludge.append( "Set baseUrn to ${baseUrn} at prevText ${prevText}\n", "UTF-8")
	}

	if (sawEndBlock) {
	  if ( prevUrn != "") {
	    //kludge.append ("Appending EOL\n", "UTF-8")
	    outFile.append(formatLine(prevUrn, "${prevPrevCount}", "${prevCount}", "${count}", endBlockMarker), charEnc)
	    prevPrevCount++;
	  }
	  prevUrn = baseUrn
	  
	  count++;
	  prevCount++;
	  sawEndBlock = false
	}
	
	if (baseUrn != prevUrn) {
	  if (debug > 0) {
	    System.err.println "Base URN ${baseUrn} differs from previous ${prevUrn}\n"
	    //kludge.append("Base URN ${baseUrn} differs from previous ${prevUrn} at prev text ${prevText} \n", "UTF-8")
	  }
	  sawEndBlock = true
	}
	

	if (count > 1) {
	  idxFile.append("${prevSrc} hmt:tokenizesTo ${prevUrn}.${prevCount} .\n")
	  idxFile.append("${prevUrn}.${prevCount} hmt:tokenizedFrom ${prevSrc}.\n")

	  if (prevPrevCount == 0) {
	    //kludge.append ("Appending ${prevText}\n", "UTF-8")
	    outFile.append(formatLine(prevUrn, nullCol, "${prevCount}", "${count}", prevText), charEnc)
	  } else {
	    //kludge.append ("Appending ${prevText}\n", "UTF-8")
	    outFile.append(formatLine(prevUrn, "${prevPrevCount}", "${prevCount}", "${count}", prevText), charEnc)
	  }
	}
	
	prevPrevCount = prevCount;
	prevCount = count;
	count++;

	prevText = urn.getSubref1()
	prevSrc = this.srcUrnName + ":" + urn.getPassageComponent()
      }
    }
    
    idxFile.append("${prevSrc} hmt:tokenizesTo ${prevUrn}.${prevCount} .\n")
    idxFile.append("${prevUrn}.${prevCount} hmt:tokenizedFrom ${prevSrc}.\n")

    //kludge.append ("Tack on ${prevText} followed by EOL\n", "UTF-8")      
    outFile.append(formatLine(prevUrn, "${prevPrevCount}", "${prevCount}", "${count}", prevText), charEnc)
    outFile.append(formatLine(prevUrn, "${prevCount}", "${count}", nullCol, endBlockMarker), charEnc)
  }

}
