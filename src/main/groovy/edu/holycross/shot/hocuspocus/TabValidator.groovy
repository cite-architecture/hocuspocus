package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

import org.apache.commons.io.FilenameUtils

import org.xml.sax.InputSource

/**
* Utility class for working with tabulated representations of texts.
*/
class TabValidator{

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


  TabValidator() {
  }

  /** Validates the RDF describing a single node.
  * @returns True if RDF satisfies OHCO2 model.
  */
  static boolean validateNodeRdf(String ttl) {
    ttl.eachLine {
      System.err.println it
    }

    return true
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
