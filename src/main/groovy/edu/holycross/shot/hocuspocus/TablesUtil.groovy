package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.TextInventory
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
	  //ok
      } else {
	throw new Exception("TablesUtil: invalid row in tabular file: components ${cols}")
      }
    }
    return idx
  }
  
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
