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


  


}
