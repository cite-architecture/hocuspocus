package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CitationModel
import edu.harvard.chs.cite.CitationTriplet
import edu.harvard.chs.cite.CtsUrn

import org.apache.commons.io.FilenameUtils

import org.xml.sax.InputSource

/**
* Utility class for working with tabulated representations of texts.
*/
class TabUtil {

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


  TabUtil() {
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

}
