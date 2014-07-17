package org.homermultitext.hocuspocus


import edu.harvard.chs.cite.CtsUrn

/** A class for working with a CTS text in the CITE architecture's
 * tabular representation.
 */
class TabularText {

  Integer debug = 0

  /** Default value separating columns of tabular data.
   */
  String separator = "#"


  // URN, Seq, Prev, Next, contextxpath, text, xpathmodel

  /** Empty constructor */
  TabularText()   {
  }


  /**  Extracts the CTS URN from the tabular representation
   * of a citable node.
   * @param tabLine The tabular data.
   * @returns A CTS URN.
   * @throws Exception if tabLine cannot be parsed, or if the
   * URN data is not a valid CTS URN.
   */
  CtsUrn getUrn(String tabLine) 
  throws Exception {
    CtsUrn urn
    def columns = tabLine.split(separator)
    try {
      urn = new CtsUrn(columns[0])
    } catch (Exception e) {
      System.err.println  "TabularText:getUrn: could not form CTS URN from ${columns[0]}"
      throw e
    }
    return urn
  }


  Integer getSequence(String tabLine)    
  throws Exception {
    Integer seq = null
    def columns = tabLine.split(separator)
    try {
      seq = columns[1].toInteger()

    } catch (Exception e) {
      System.err.println  "TabularText:getUrn: ${columns[1]} not an integer"
      throw e
    }
    return seq

  }



  Integer getPrevSequence(String tabLine)    
  throws Exception {
    Integer seq = null
    def columns = tabLine.split(separator)
    if (columns[2].size() < 1) {
      return seq
    }

    try {
      seq = columns[2].toInteger()

    } catch (Exception e) {
      System.err.println  "TabularText:getUrn: ${columns[2]} not an integer"
      throw e
    }
    return seq
  }






  Integer getNextSequence(String tabLine)    
  throws Exception {
    Integer seq = null
    def columns = tabLine.split(separator)
    if (columns[3].size() < 1) {
      return seq
    }

    try {
      seq = columns[3].toInteger()

    } catch (Exception e) {
      System.err.println  "TabularText:getUrn: ${columns[3]} not an integer"
      throw e
    }
    return seq
  }



  String getText(String tabLine) {
    def columns = tabLine.split(separator)
    if (columns.size() < 6) {
      throw new Exception("TabularText:getText: badly formatted line: ${tabLine}")
    }
    return columns[5]
  }

}