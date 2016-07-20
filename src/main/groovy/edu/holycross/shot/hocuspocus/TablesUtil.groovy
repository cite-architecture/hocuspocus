package edu.holycross.shot.hocuspocus

//import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

import org.apache.commons.io.FilenameUtils

import org.xml.sax.InputSource

/**
* Utility class for working with tabulated representations of texts.
*/
class TablesUtil {

  Integer debug = 0

  /** String value to use as column separator in tabular text output.
   */
  def columnSeparator = "#"

  /** Character encoding for current output file.
   */
  def outFileEncoding = "UTF-8"

  /** Character encoding for current input file.
   */
  def inFileEncoding = "UTF-8"


  TablesUtil() {
  }

  /** Sets String value to use as separator in
   *  columns of tabular text output.
   *  @param s The String to use as a column-separator value.
   */
  void setColumnSeparator(String s) {
    this.columnSeparator = s
  }


  /** Gets current value to use as separator in
   *  columns of tabular text output.
   *  @return Current value for column separator.
   */
  String getColumnSeparator() {
    return this.columnSeparator
  }


  /** Finds a single version-level CTS URN in a file in tabulated format.
   * @param tabFile The file to search.
   * @param urn The CTS URN to search for.
   * @returns A line from the tabulated file, or an empty
   * String if no match was found.
   */
  String tabEntryForUrn(File tabFile, CtsUrn urn) {
    return tabEntryForUrn(tabFile, urn.toString())
  }


  // Sequence number, as string, keyed to urns
  /* String urnForSequence(File tabFile, String seq) {
  }
  */

  // hashmap seqIndex from tabfile
  /**
   * Indexes sequence numbers in a tabular file to
   * their identifying URNs.
   * @param tabFile The file to index.
   * @returns A map of sequence numbers to URN values.
   *  
   */
  LinkedHashMap getSequenceIndex(File tabFile) {
    return getSequenceIndex(tabFile, "#")
  }

  /**
   * Indexes sequence numbers in a tabular file to
   * their identifying URNs.
   * @param tabFile The file to index.
   * @param sepStr String value used as column delimiter. 
   * @returns A map of sequence numbers to URN values.
   *  
   */
  LinkedHashMap getSequenceIndex(File tabFile, String sepStr) {
    def  idx = [:]
    tabFile.eachLine  {
      def cols = it.split(sepStr)
      if (cols.size() == 8) {
	// ok
	def urn = cols[0]
	def seq = cols[2]
	idx[seq] = urn

      } else if ((cols.size() == 3) && (cols[0] == "namespace")){
	  //ok, but skip
	
      } else {
	throw new Exception("TablesUtil: invalid row in tabular file: components ${cols}")
      }
    }
    return idx
  }


  /** Converts a two-column source with an ordered listing 
   * of text nodes described as URN-text contents into an equivalent
   * 82XF formatted string that can be used without need to 
   * maintain document order by lines.
   * @param f File to read two-column data from.
   * @returns A String in 82XF format.
   */
  static String twoTo82XF(File f) {
    return twoTo82XF(f, "#")
  }

  /** Converts a two-column source with an ordered listing 
   * of text nodes described as URN-text contents into an equivalent
   * 82XF formatted string that can be used without need to 
   * maintain document order by lines.
   * @param f File to read two-column data from.
   * @param separator String value used for column delimiter.
   * @returns A String in 82XF format.
   */
  static String twoTo82XF(File f, String separator) {
    //println "TablesUtil: twoTo82XF file ${f} has contents"
    //println f.getText()
    return twoTo82XF(f.getText(), separator)
  }
  static String twoTo82XF(String s) {
    return twoTo82XF(s, "#")
  }



  /** Converts a two-column source with an ordered listing 
   * of text nodes described as URN-text contents into an equivalent
   * 82XF formatted string that can be used without need to 
   * maintain document order by lines.
   * @param s String of two-column data.
   * @param separator String value used for column delimiter.
   * @returns A String in 82XF format.
   */
  static String twoTo82XF(String s, String separator) {
    StringBuffer xfdata = new StringBuffer()

    // maps of URN to preceding or following URNs
    def prevToNext = [:]
    def nextToPrev = [:]
    def urnSequence = []
    
    // previously seen URN
    String prevUrn = ""
    /// Read through sequence of lines once to index
    // abitrary URN values to preceding and following URNs
    def sLines = s.readLines()
    sLines.each {
      def cols = it.split(separator)
      String currUrn = cols[0]
      urnSequence.add(currUrn)
      if (prevUrn != "") {
	prevToNext[prevUrn] = currUrn
	nextToPrev[currUrn] = prevUrn
      }
      prevUrn = currUrn
    }

    // Construct 82XF string:
    xfdata.append("URN#Previous#Sequence#Next#Text\n")
    def seq = 0
    sLines.each { l ->
      def cols = l.split(separator)
      if (cols.size() != 2) {
	System.err.println("TablesUtil: error parsing ${l}. Wrong number of columns in ${cols}")
      } else {
	def urn = cols[0]
	def txt = cols[1]
	def prv = ""
	def nxt = ""
	if (nextToPrev[urn]) {
	  prv = nextToPrev[urn]
	}
	if (prevToNext[urn]) {
	  nxt = prevToNext[urn]
	}
	xfdata.append("${urn}#${prv}#${seq}#${nxt}#${txt}\n")
      }
      seq++;
    }
    return xfdata.toString()
  }

  /** Converts internal seven-column format to
   * 82XF format.
   * @param s String in seven-column internal format.
   * @returns String in five-column 82XF format.
   */
  String sevenTo82XF(File f) {
    return sevenTo82XF(f,"#")
  }
  
  String sevenTo82XF(File f, String separator) {
    StringBuilder bldr = new StringBuilder()
    def idx = getSequenceIndex(f)
    f.eachLine {
      def cols = it.split(separator)
      if (cols.size() == 8) {
	String urn = cols[0]
	String seq = cols[1]
	String prevUrn = idx[cols[2]]
	String nextUrn = idx[cols[3]]
	String textVal = cols[5]
	bldr.append("${urn}${separator}${prevUrn}${separator}${seq}${separator}${nextUrn}${separator}${textVal}\n")

      } else if ((cols.size() == 3) && (cols[0] == "namespace")){
	//ok, but skip
      } else {
	throw new Exception("TablesUtil: invalid row in tabular file: components ${cols}")
      }
    }
    return bldr.toString()
  }
  
  /** Formats an entry in a tabular file identified by
   * sequence string in 82XF format.
   * @param taFile The file to retrieve data from.
   * @param seqStr Sequence number, as a String. 
   * @returns A 5-column record in 82FX format.
   */

    /*String seqStringTo82FX(File tabFile, String seqStr) {
    def idx = getSequenceIndex(tabFile)
    String raw = idx[seqStr]
    return sevenTo82XF(raw)
    // and continue on...
    }*/
  

  
  /** Finds a single line in a tabular file identified
   * by a given URN value.
   * @param tabFile The file to search.
   * @param urnStr The URN value to search for.
   * @returns A single line of the tabular file.
   */
  String tabEntryForUrn(File tabFile, String urnStr) {
    String entry  = ""
    entry = tabFile.readLines().find { ln ->
      String q = urnStr + "#.*"
      ln ==~ /${q}/
    }
    return entry
  }



  String tabEntryForUrn(String tabSrc, CtsUrn urn) {
    return tabEntryForUrn(tabSrc, urn.toString())
  }

  String tabEntryForUrn(String tabSrc, String urnStr) {
    String entry  = ""
    entry = tabSrc.readLines().find { ln ->
      String q = urnStr + "#.*"
      ln ==~ /${q}/
    }
    return entry
  }


  ArrayList tabEntriesForUrns(String tabSrc, ArrayList urnList) {
    def entries = []
    urnList.each { u ->
      entries.add(tabEntryForUrn(tabFile,u.toString()))
    }
    return entries
  }

  /** Finds version-level CTS URNs in a file in tabulated format.
   * @param tabFile The file to search.
   * @param urnList A list of CTS URNs to search for.
   * @returns A list of lines from the tabulated file.
   */
  ArrayList tabEntriesForUrns(File tabFile, ArrayList urnList) {
    def entries = []
    urnList.each { u ->
      entries.add(tabEntryForUrn(tabFile,u.toString()))
    }
    return entries
  }

  ArrayList tabEntriesForUrns(ArrayList stringList, ArrayList urnList) {
    def entries = []
    stringList.each { u ->
      entries.add(tabEntryForUrn(tabFile,u.toString()))
    }
    return entries
  }


  ArrayList tabEntriesForDirectory(File tabulatedDir, ArrayList urnList) {
    def total = []

    def tabList = tabulatedDir.list({d, f-> f ==~ /.*.txt/ } as FilenameFilter )?.toList()
    tabList.each { f ->
      File tabFile = new File(tabulatedDir, f)
      def subtotal = []
      subtotal = tabEntriesForUrns(tabFile, urnList)
      subtotal.each { entry ->
	if (entry != null) {
	  total.add(entry)
	}
      }
    }
    return total
  }


  



  
/*

We should get this comment the heck out of here:

# Required for Tabulation

- cts:abbreviatedBy
- cts:belongsTo
- cts:citationDepth
- cts:containedBy
- cts:contains
- cts:fullUri
- cts:hasPassage
- cts:hasSequence
- cts:hasTextContent
- cts:isPassageOf
- cts:lang
- cts:next
- cts:possesses
- cts:prev
- dcterms:title
- rdf:label
- rdf:type

# Corpus Dependent

- cts:hasSubref
- cts:isSubrefOf
- cts:translationLang
- cts:xmlns
- cts:xmlnsabbr
- hmt:xmlOpen
- hmt:xpTemplate
*/
}
