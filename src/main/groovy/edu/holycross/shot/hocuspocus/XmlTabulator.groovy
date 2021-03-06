package edu.holycross.shot.hocuspocus


import edu.harvard.chs.cite.CtsUrn

import org.apache.commons.io.FilenameUtils

import org.xml.sax.InputSource


import javax.xml.xpath.*

import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Node


import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory

import javax.xml.transform.dom.DOMSource
import javax.xml.transform.dom.DOMResult
import javax.xml.transform.stream.StreamResult



/**
 * XmlTabulator is a utility class for working with canonically citable texts
 * in the OHCO2 model, and converting representations in XML files  to a
 * tabular format.
 *
 *
*/
class XmlTabulator {

  Integer debug = 0

  // add a log file??
  File log

  /** Keep regular expressions that destroy the beautiful formatting
   * of my editor in a separate file. */
  // make static?
  //TabulatorRegEx re

  /** String value to use as column separator in tabular text output.
   */
  def columnSeparator = "#"

  /**
   * Running count of citable nodes written to output.
   */
  int nodesProcessed = 0

  /** Root element of parsed Document to process.
   */
  org.w3c.dom.Element parsedRoot

  /** The CitationModel (an object type from the cite library)
   * for the given document.
   */
  CitationModel cm = null

  /** Base for output file names.
   */
  String outFileBaseName

  /** Current output file.
   */
  File currOutFile

  /** Character encoding for current output file.
   */
  def outFileEncoding = "UTF-8"


  /** Character encoding for current input file.
   */


  def inFileEncoding = "UTF-8"


  // These three lists are loaded up by the collectCitableIds method.
  /** List of numeric indices of comprehensive citation schemes in a document. */
  def schemeIndex = []
  /** List of numeric indices of triplet mappings in a document (one tier of
   * a citation scheme, for example). */
  def tripletIndex = []
  /** List of node IDs above leaf nodes in a citation scheme. */
  def nodeIdLists = []


  // Mapping of prefixes to namespaces:
  /** A map including closures implementing the javax.xml.xpath.Namespace
   * interface. Using this in groovy with
   * "groovyNSClosure as javax.xml.namespace.NamespaceContext"
   * will satisfy that interface.
   * Individual namespace mappings themselves are stored in  nsMap.
   * It includes by default a mapping of the TEI namespace to
   * the prefix abbreviation 'tei' : other mappings can be added with
   * the addNSMapping method().
   */
  def groovyNSClosure =  [
    nsMap : ['tei':'http://www.tei-c.org/ns/1.0'],
    getNamespaceURI : {String nsPrefix -> groovyNSClosure.nsMap[nsPrefix]},
    getPrefix :{String nsUri ->
    groovyNSClosure.nsMap.find {it.value == nsUri}?.key
    },

    // return a clunky java list iterator
    getPrefixes :
    { String nsUri ->
      def findRes = []
      groovyNSClosure.nsMap.findAll {it.value == nsUri}.each {
	findRes.add(it?.key);
      }
      findRes.iterator()
    }
  ]


  /** Adds a single namespace mapping  (prefix -> URI) to the internal
   * implementation of the javax.xml.namespace.NamespaceContext interface.
   * @param prefix Abbreviation for the namespace URI.
   * @param ns The full namespace URI.
   */
  void addNSMapping(String prefix, String ns) {
    def modMap = groovyNSClosure["nsMap"];
    modMap.put(prefix, ns)
    groovyNSClosure["nsMap"] = modMap
  }

  /** Assigns a mapping of abbreviations to namespace URIs to the internal
   *  implementation of the javax.xml.namespace.NamespaceContext interface.
   * @param m The map to use in the NamespaceContext implemenation;  map keys
   * are the namespace abbreviations;  map values are the full namespace URIs.
   */
  void setNSMap(LinkedHashMap m) {
    groovyNSClosure["nsMap"] = m
  }


  /** Returns the java LinkedHashMap representation of the
   * all namespace mappings, as required for the
   * javax.xml.namespace.NamespaceContext interface.
   */
  LinkedHashMap getNSMaps () {
    return groovyNSClosure["nsMap"] as LinkedHashMap
  }


  /** Constructor with no parameters.
   */
  XmlTabulator() {
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


  /**
   * Creates an unordered list of nodeId values for all nodes
   * resulting from applying an xpath expression to a source node.
   * @param xpString The Xpath expression to apply.
   * @param queryRoot The Element to which to apply the Xpath expression.,
   * @return An unordered list of integeres (nodeId values).
   */
  def nodeIdsForXpath(String xpString, org.w3c.dom.Element queryRoot) {
    def idList = []
    def xpath = XPathFactory.newInstance().newXPath()
    def nsc = groovyNSClosure as javax.xml.namespace.NamespaceContext
    xpath.setNamespaceContext(nsc)
    def foundNodes = xpath.evaluate( xpString, queryRoot, XPathConstants.NODESET)
    def limit = foundNodes.getLength()
    if (debug > 1) {
      String msg =  "nodeIdsForxpath:  for " + xpString + ", found " + limit + " nodes.\n"
      System.err.println msg
      log.append(msg)
    }

    def done = false
    def cnt = 0
    while (!done) {
      if (cnt < limit) {
	def keyVal = foundNodes.item(cnt).getNodeIndex()
	idList = idList.plus(keyVal)

      } else {
	done = true
      }
      cnt++;
    }
    return idList
  }


  /**
   * Creates an unordered list of nodeIds for every citable node in
   * a parsed document.
   * @return An unordered list of nodeId values for every citable
   * node contained in docRoot.
   */
  private def collectCitableIds() {
    def citeSchemes = this.cm.getMappings()
    this.schemeIndex = []
    this.tripletIndex = []
    this.nodeIdLists = []
    citeSchemes.eachWithIndex { citeScheme, schemeNum ->
      // We can get IDs for all citable nodes in two passes:
      //
      // [1] collect every terminal node above the
      // leaf level of the hierarchy
      citeScheme.eachWithIndex { triplet, idx ->
	if (triplet.allowsTerminalNode()) {
	  // then we need to process it, so record by index numbers
	  // what scheme and triplet we use
	  this.schemeIndex.leftShift(schemeNum)
	  this.tripletIndex.leftShift(idx)

	  def xp = triplet.containerXpath() + triplet.terminalNodeXpath()
	  def nodesForTerminal = nodeIdsForXpath(xp,parsedRoot)
	  this.nodeIdLists.leftShift(nodesForTerminal)

	} else {
	  if (debug > 1) {
	    String msg = "\t... no terminal nodes at level ${idx}\n"
	    System.err.println  msg
	    log.append(msg)
	  }
	}
      }
    }

    //
    //[2] collect all leaf nodes of the citation tree:
    this.cm.getXpathList().eachWithIndex { xp, xpNum ->
      // record index for scheme number, and leaf-node triplet
      this.schemeIndex.leftShift(xpNum)
      def cs = citeSchemes[xpNum]
      this.tripletIndex.leftShift(cs.size() - 1)
      this.nodeIdLists.leftShift(nodeIdsForXpath(xp,parsedRoot))

      if (debug > 1) {
	String msg =  "XPATH: ${xp} at num ${xpNum}\nUsing scheme index " + this.schemeIndex + "\nYielded cite scheme " + cs + "\nCollected node ids " + nodeIdsForXpath(xp,parsedRoot) + "\n"
	System.err.println msg
	log.append(msg)
      }

    }
  }


  // exceptions:
  // 1. no citation model
  //
  /** Converts one XML file to tabular representation.
   * @param txtUrn Urn of document to tabulate.
   * @param inv TextInventory cataloging the document.
   * @param confFile Configuration object mapping URN to file and citation scheme.
   * @param txtFile The XML file to tabulate.
   */
  String tabulateFile(CtsUrn txtUrn,
		      TextInventory inv,
		      CitationConfigurationFileReader confFile,
		      File txtFile)
  throws Exception {
    StringBuilder tabularText = new StringBuilder()
    if (debug > 0) {
      System.err.println "Logging for file ${txtFile} = urn ${txtUrn}\n"

    }

    this.cm = confFile.getCitationModel(txtUrn.toString())

    StringBuilder nsMapBldr = new StringBuilder("")

    try {
      LinkedHashMap xmlNs = confFile.getXmlNsData(txtUrn.toString())
      // CHECK FOR UNIQUENESS?
      //Begin by blindly adding every namespace record in the service to our output buffer:
      xmlNs?.keySet().each {
	nsMapBldr.append " xmlns:${it}='" + xmlNs[it] + "'"
	// We're trying to write this pattern:
	//  namespace#ABBR#FULLURI#LABEL
	String oneLine = "namespace${columnSeparator}${it}${columnSeparator}${xmlNs[it]}${columnSeparator}\n".toString()
	tabularText.append(oneLine)
      }

    } catch (Exception e) {
      System.err.println "XmlTabulator:tabulateFile:  no namespace data on " + txtFile
      System.err.println "Continuing to tabulate anyway."
    }


    // Create parsed document from local file source:
    def docBuilderFac = DocumentBuilderFactory.newInstance()
    docBuilderFac.setNamespaceAware(true)
    def docBuilder = docBuilderFac.newDocumentBuilder()

    FileReader fr = new FileReader(txtFile)

    // docBuilder is not smart about character encodings, but we
    // can work around that if we construct an InputSource
    InputSource is = new InputSource(fr)
    is.setEncoding(this.inFileEncoding)

    try {
      parsedRoot = docBuilder.parse(is).documentElement
    } catch (Exception e) {
      System.err.println "Failed to parse stream from ${txtFile}"
      throw e
    }

    if (parsedRoot) {
      parsedRoot.normalize()

      //def newFName = "${outFileBaseName}.txt"
      //this.currOutFile = new File(outputDir, newFName)
      //currOutFile.setText("")

      if (debug > 0) {
	// add date stamp here?
	String msg = "STEP 1: collecting all node IDs\n"
	System.err.println msg
	log.append(msg)
      }
      collectCitableIds()

      // Now walk through entire document, and check for nodes with ids
      // contained in our citableNodes list
      tabularText.append(tabStringFromTree(parsedRoot, txtUrn, nsMapBldr.toString(), tabularText.toString()))

    } else {
      System.err.println "NO PARSEABLE ROOT FOR FILE ${f}"
    }

    return tabularText.toString()
  }

  /**
   * Retrieves the text identified by urn, and writes
   * a representation of the text in tabular format to one or more files
   * with base file name fileBase, using character encoding chEncoding.
   * @param urn The URN of the text to retrieve.
   * @param inv
   * @param fileBase The base name to use for output files.
   * @param chEncoding The character encoding to use.
   */


  /*
  public void tabulate(CtsUrn urn, TextInventory inv, File srcFile, String outputDir) throws Exception {
    try {
      File outDir = new File(outputDir)
      tabulate(urn,inv,srcFile,outDir)
      if (! outDir.canWrite()) {
	throw new Exception("Cannot get write permission to ${outDir}")
      }
    } catch (Exception e) {
      System.err.println "Could not create file ${outputDir}"
      throw e
    }
  }

  */

  /**
   * Writes a tabulated representation of a canonically citable XML file.
   * @param urn CTS URN of the document to tabulate.
   * @param inv TextInventory with information about how to cite the document in question.
   * @param srcFile Source file to tabulate.
   * @param outputDir Writable directory where tabular output will go.
   */

  /*
  public void tabulate(CtsUrn urn, TextInventory inv, File srcFile, File outputDir)
  throws java.io.FileNotFoundException,IOException,SecurityException,Exception  {
    this.outFileBaseName = FilenameUtils.getName(srcFile.getAbsolutePath()).replace(/.xml/,'')
    if (debug > 0) {
      System.err.println "XmlTabulator: TABULATING urn ${urn} to fileBase " + outputDir + " using base file name " + this.outFileBaseName
    }

    this.cm = inv.getCitationModel(urn)
    if (cm) {
      if (debug > 0) {System.err.println "CitationModel is ${cm}"}
    } else {
      System.err.println "Could not find citation model for urn ${urn}!"
      throw new Exception("XmlTabulator:  could not find citation model for ${urn}")
    }
    def nsMaps = inv.getNsMapList()
    def oneMap = nsMaps[urn.toString()]

    StringBuilder nsMapBldr = new StringBuilder()
    oneMap?.keySet().each {
      nsMapBldr.append " xmlns:${it}='" + oneMap[it] + "'"
    }

    // Create parsed document from local file source:
    def docBuilderFac = DocumentBuilderFactory.newInstance()
    docBuilderFac.setNamespaceAware(true)
    def docBuilder = docBuilderFac.newDocumentBuilder()


    FileReader fr = new FileReader(srcFile)
    // docBuilder is not smart about character encodings, but we
    // can work around that if we construct an InputSource
    InputSource is = new InputSource(fr)
    is.setEncoding(this.inFileEncoding)
    try {
      parsedRoot = docBuilder.parse(is).documentElement
    } catch (Exception e) {
      System.err.println "Failed to parse stream from ${srcFile}"
      throw e
    }

    if (parsedRoot) {
      parsedRoot.normalize()

      def newFName = "${outFileBaseName}.txt"
      this.currOutFile = new File(outputDir, newFName)
      currOutFile.setText("")
      // Begin by blindly adding every namespace record in the service to our output buffer:
      //      oneMap?.keySet().each {
	// We're trying to write this pattern:
	// namespace#ABBR#FULLURI#LABEL

	this.currOutFile.append("namespace${columnSeparator}${it}${columnSeparator}${oneMap[it]}${columnSeparator}\n",this.outFileEncoding)
      }


      if (debug > 0) { println "STEP 1: collecting all node IDs" }
      collectCitableIds()

      // Now walk through entire document, and check for nodes with ids
      // contained in our citableNodes list
      tabFromTree(parsedRoot, urn, nsMapBldr.toString(), outputDir)

    } else {
      System.err.println "NO PARSEABLE ROOT FOR FILE ${f}"
    }
  }
  // end of tabulate method
*/




  /** Creates passage component of a CTS URN by finding reference values for every
   * level of citation hierarchy, and creating dot-separated string from them.
   * Walks back through all the ancestor elements in the Citation Scheme's Xpath
   * template, and through the successive parent nodes of currNode. If the Xpath template
   * includes the citation variable '?', looks up the actual value from the DOM, and prepends
   * it to the results buffer.
   * @param scheme Citation scheme that applies to the text currNode comes from.
   * (A citation scheme is a list of CitationTriplet objects.)
   * @param tripletIndex Index into scheme of which CitationTriplet applies to currNode.
   * @param currNode The Node that will be the leaf of the resulting XPath expression.
   * @return The String value for the passage component of a CTS URN for citable node n.
   */
  private String fillRefValue(ArrayList scheme, int tripletIndex, Node n) {
    CitationTriplet triplet = scheme[tripletIndex]
    def citeAttr = triplet.getLeafVariableAttribute()
    // Initialize buffer for return value with citation value
    // for leaf node.
    StringBuffer buff = new StringBuffer()
    if (n instanceof  org.apache.xerces.dom.DeferredTextImpl) {
    } else {
      buff.append(n.getAttribute(citeAttr))
    }
    n = n.getParentNode()

    def ancestors = triplet.getScopePattern()
    def ancestorParts =  TabulatorRegEx.splitAncestors(ancestors)
    // Total number of ancestor elements to find.
    // Use this as an index to walk back trhough
    // the ancestorParts array.
    def lastIndex = ancestorParts.size() - 1
    boolean done = false
    while (!done) {
      def part = ancestorParts[lastIndex]
      //      if (part ==~ re.citationPattern) {
      if (part ==~ TabulatorRegEx.citationPattern) {
	citeAttr = scheme[tripletIndex].getLeafVariableAttribute()
	def nodeVal = n.getAttribute(citeAttr)
	if (debug > 1) {
	  String msg =  "on node ${n.getLocalName()}\nnodeVal = " + nodeVal + "\n"
	  System.err.println msg
	  log.append(msg)
	}
	tripletIndex--;
	buff.insert(0,"${nodeVal}.")
      }

      if (lastIndex == 1) {
	done  = true
      } else {
	n = n.getParentNode()
      }
      lastIndex--;
    }
    return buff.toString()
  }

  private String tabStringFromTree(Node n,
				   CtsUrn urn,
				   String xmlNsDecls,
				   String compositeString) {



    // total matching nodes in all our lists:
    int nodeMax = 0
    this.nodeIdLists.each { nlist ->
      nodeMax = nodeMax + nlist.size()  + this.nodesProcessed
    }

    def kids = n.getChildNodes()
    def kidsLimit = kids.getLength()
    def count = 0
    while (count < kidsLimit) {
      def kid = kids.item(count)
      count++;
      // compare node against each list to see if
      // it matches:
      nodeIdLists.eachWithIndex { nl, idx ->
	if (nl.contains(kid.getNodeIndex())){
	  if (debug > 1) {
	    String msg = "\nCitable node to process: ${kid.getNodeIndex()}\nComposite is " + compositeString + "\n"

	    System.err.println msg
	    log.append(msg)
	  }
	  this.nodesProcessed++;

	  // Keep track of node counting
	  def prevCount = ""
	  def nextCount = ""
	  if (this.nodesProcessed > 1) { prevCount = this.nodesProcessed - 1 }
	  if (this.nodesProcessed <= nodeMax) { nextCount = this.nodesProcessed + 1 }
	  String record = formatRecord(urn, n, kid, idx, prevCount.toString(), nextCount.toString(), xmlNsDecls)
	  compositeString += record
	}
	// end: if we found a match
      }
      // end:  check each index
      //
      // continue recursion to check rest of tree
      compositeString =  tabStringFromTree(kid, urn, xmlNsDecls, compositeString)
    }
    return compositeString
  }

  /** Formats a single line for a tabular representation of a citable node.
   */
  String formatRecord(CtsUrn urn, Node parent, Node kid, Integer idx,  String prevCount, String nextCount, String xmlNsDecls) {
    // De-reference scheme and triplet indices to load relevant triplet :
    def schemes = this.cm.getMappings()
    if (debug > 1) {
      String msg = "At index ${idx}, schemes are " + schemes + "\n\tschemeIndexes are " + schemeIndex + "\n\ttripletIndexes are " + tripletIndex + "\n"
      System.err.println msg
      log.append(msg)
    }
    def sIdx = this.schemeIndex[idx]
    def currentScheme = schemes[sIdx]
    def tIdx = this.tripletIndex[idx]
    def currentTriplet = currentScheme[tIdx]



    // we need to do xpath transforms in this method ...
    TransformerFactory tf = TransformerFactory.newInstance()
    Transformer xform = tf.newTransformer()
    xform.setOutputProperty("omit-xml-declaration", "yes")

    // 2. get text content of node
    // Man, the w3c DOM makes you work to do something obvious!
    StreamResult res = new StreamResult(new StringWriter())
    DOMSource ds = new DOMSource(kid)
    xform.transform(ds,res)
    def nodeText = res.getWriter().toString().replaceAll(/\n/,'')

    // make sure resulting text will be parseable as one record per
    // line with fields delineated by columnSeparator:
    nodeText = nodeText.replaceAll(/${columnSeparator}/,' ')
    nodeText = nodeText.replaceAll(/[ \t\n\r]+/," ")

    // 3.get ancestor and leaf patterns for node

    def ancestors = currentTriplet.getScopePattern()
    def leafPatt = currentTriplet.getLeafPattern()

    // 4. get ancestor path
    def explicitAncPath =  fillAncestorPath(currentScheme,tIdx, parent)

    // 5. get  value for passage reference
    // to get the refVal, we need to check both leaf node URNs,
    // and any possible terminal nodes
    def refVal = fillRefValue(currentScheme, tIdx, kid)

    // 6. Supplied in xmlNsDecls parameter: XML namespace declarations
    // Composite
    if (debug > 0) {
      String msg = "At " + urn + refVal + ": ${this.nodesProcessed} nodes processed\n"
      System.err.println (msg)
      log.append(msg)
    }

    return "${urn}${refVal}${columnSeparator}${this.nodesProcessed}${columnSeparator}${prevCount}${columnSeparator}${nextCount}${columnSeparator}${explicitAncPath}${columnSeparator}${nodeText}${columnSeparator}${ancestors}${leafPatt}${columnSeparator}${xmlNsDecls}\n"
  }



  /** Creates an ordered tabular representation of all nodes in a given (sub)document
   * with nodeIds appearing in the list of desired nodes.  Walks the tree of all nodes
   * below n, and checks their nodeId against idList:  if the nodeId appears there,
   * adds a line for that node to the accumulating buffer, buff.
   * @param n The node to tabulate.
   * @param urn Urn (without reference component) of document being tabulated.
   */

  /*
  private void tabFromTree(Node n, CtsUrn urn, String xmlNsDecls, File outputDir) {
    // we need to do xpath transforms in this method ...
    TransformerFactory tf = TransformerFactory.newInstance()
    Transformer xform = tf.newTransformer()
    xform.setOutputProperty("omit-xml-declaration", "yes")

    // total matching nodes in all our lists:
    int nodeMax = 0
    this.nodeIdLists.each { nlist ->
      nodeMax = nodeMax + nlist.size()  + this.nodesProcessed
    }

    if (debug > 1) {
      System.err.println "nodeMax is ${nodeMax} for nodeIdLists "  + nodeIdLists
    }


    def kids = n.getChildNodes()
    def kidsLimit = kids.getLength()
    def count = 0
    while (count < kidsLimit) {
      def kid = kids.item(count)
      count++;
      // compare node against each list to see if
      // it matches:
      nodeIdLists.eachWithIndex { nl, idx ->


	if (nl.contains(kid.getNodeIndex())){
	  // Dereference scheme and triplet indices to load relevant triplet :
	  def schemes = this.cm.getMappings()

	  if (debug > 2) {
	    println "tabfromTree: cycling nodeIlist at index ${idx}"
	    println "Size of scheme list is " + schemes.size()
	    println "Size of tripletIndex is " + tripletIndex.size()
	  }

	  def sIdx = this.schemeIndex[idx]
	  def currentScheme = schemes[sIdx]
	  def tIdx = this.tripletIndex[idx]
	  def currentTriplet = currentScheme[tIdx]

	  this.nodesProcessed++;

	  if (debug > 1) {
	    println "tabFromTree: with index ${idx}: currentTriplet ${currentTriplet}"
	    println "\tnode matches in list ${idx} (total ${this.nodesProcessed})"
	    println "using triplet ${currentTriplet}"
	  }


	  // Need to construct 5 fields for output:

	  // 1. keep track of node counting
	  def prevCount = ""
	  def nextCount = ""
	  if (this.nodesProcessed > 1) { prevCount = this.nodesProcessed - 1 }

	  if (this.nodesProcessed <= nodeMax) { nextCount = this.nodesProcessed + 1 }

	  // 2. get text content of node
	  // Man, the w3c DOM makes you work to do something obvious!
	  StreamResult res = new StreamResult(new StringWriter())
	  DOMSource ds = new DOMSource(kid)
	  xform.transform(ds,res)
	  def nodeText = res.getWriter().toString().replaceAll(/\n/,'')




	  // make sure resulting text will be parseable as one record per
	  // line with fields delineated by columnSeparator:
	  nodeText = nodeText.replaceAll(/${columnSeparator}/,' ')
	  nodeText = nodeText.replaceAll(/[ \t\n\r]+/," ")


	  // 3.get ancestor and leaf patterns for node
	  def ancestors = currentTriplet.getScopePattern()
	  def leafPatt =currentTriplet.getLeafPattern()


	  // 4. get ancestor path
	  def explicitAncPath =  fillAncestorPath(currentScheme,tIdx, n)

	  // 5. get  value for passage reference
	  // to get the refVal, we need to check both leaf node URNs,
	  // and any possible terminal nodes
	  def refVal = fillRefValue(currentScheme, tIdx, kid)

	  // 6. Supplied in xmlNsDecls parameter: XML namespace declarations

	  // Composite
	  def record = "${urn}${refVal}${columnSeparator}${this.nodesProcessed}${columnSeparator}${prevCount}${columnSeparator}${nextCount}${columnSeparator}${explicitAncPath}${columnSeparator}${nodeText}${columnSeparator}${ancestors}${leafPatt}${columnSeparator}${xmlNsDecls}\n"


	  this.currOutFile = new File(outputDir, "${this.outFileBaseName}.txt")
	  this.currOutFile.append(record)
	}
	// end: if we found a match


      }
      // end:  check each index

      // continue recursion to check rest of tree
      tabFromTree(kid, urn, xmlNsDecls, outputDir)
    }

  }
  */

  /** Constructs an XPath expression from currNode back to document root by filling
   * correct citation values into the xpath template expressions in a citation triplet.
   * Walks back through all the ancestor elements in the Citation Scheme's Xpath
   * template, and through the successive parent nodes of currNode. If the Xpath template
   * includes the citation variable '?', looks up the actual value from the DOM, and substitutes
   * it into the results buffer.
   * @param scheme Citation scheme that applies to the text currNode comes from.
   * (A citation scheme is a list of CitationTriplet objects.)
   * @param tripletIndex Index into scheme of which CitationTriplet applies to currNode.
   * @param currNode The Node that will be the leaf of the resulting XPath expression.
   * @return An Xpath expression (as a String) including correct citation values for the
   * the given citable node.
   */
  private String fillAncestorPath (ArrayList scheme, int tripletIndex, Node currNode) {
    // Buffer for return value
    StringBuilder buff = new StringBuilder()

    CitationTriplet triplet = scheme[tripletIndex]
    def ancestors = triplet.getScopePattern()
    def ancestorParts = TabulatorRegEx.splitAncestors(ancestors)
    // Total number of ancestor elements to find.
    // Use this as an index to walk back trhough
    // the ancestorParts array.
    def lastIndex = ancestorParts.size() - 1

    if (debug > 1) {
      String msg = "fillAncestorPath: node ${currNode.getLocalName()} with triplet ${triplet}\nLooking for ${tripletIndex} citaion values to substitute in, and have scheme ${scheme}.\n"
      System.err.println  msg
      log.append(msg)
    }

    // Cycle through all ancestor elements in the XPath template, from nearest back to root.
    // Check if the template for each element includes the citation variable expression '?'
    // If it does, snag that value from the appropriate attribute of currNode, and substitute
    // it into our results.
    boolean done = false
    while (!done) {
      def part = ancestorParts[lastIndex]
      if (part ==~ TabulatorRegEx.citationPattern) {
	def citeAttr = scheme[tripletIndex].getLeafVariableAttribute()
	def nodeVal = currNode.getAttribute(citeAttr)
	part = part.replace(/?/,nodeVal)
	tripletIndex--;
	if (debug > 1) {
	  String msg = "\tSubstitute in value ${nodeVal} for part " + part + "\nDecrement triplet index to ${tripletIndex}\n"

	  System.err.println  msg
	  log.append(msg)
	}
      }
      buff.insert(0, "/${part}")
      if (debug > 1) {

	String msg = "${lastIndex}: ${part} --> ${buff.toString()} for node ${currNode.getLocalName()}"
	System.err.println msg
	log.append(msg)
      }
      if (lastIndex == 1) {
	done  = true
      } else {
	currNode = currNode.getParentNode()
      }
      lastIndex--;
    }
    return buff.toString()
  }

}
