package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.*
import org.apache.commons.io.FilenameUtils

import org.xml.sax.InputSource
/*
Tabulator:  a utility class for converting XML files to a tabular format.
Output is written to a series of one or more local files, with a maximum number
of records in each file defined by the  chunkSize property.

Requires:  java 5 or higher (since it uses the java 5 xpath engine)

*/


import edu.harvard.chs.cts3.*
import javax.xml.xpath.*

import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Node


import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory

import javax.xml.transform.dom.DOMSource
import javax.xml.transform.dom.DOMResult
import javax.xml.transform.stream.StreamResult


/**
* A class working with texts in a CTS, and formatting them in tabular form.
*/
class Tabulator {
    Integer debug = 0

    /** String value to use as column separator in tabular text output.
    */
    def columnSeparator = "#"

    /** 
    * Chunk output into this number of lines to try to send to Google at one shot.
    */
    int chunkSize = 100

    /**
    * Running count of citable nodes written to output.
    */
    int nodesProcessed = 0

    /** Root element of parsed Document to process.
    */
    org.w3c.dom.Element parsedRoot

    /** CitationModel for document to process.
    */
    CitationModel cm = null


    /** Base for output file names.
    */
    String outFileBaseName

    /** Count of output files as parsed input 
    * is chunked into units of chunkSize records.
    */
    int fileCount = 0

    /** Current output file.
    */
    File currOutFile

    /** Character encoding for current output file.
    */
    def outFileEncoding = "UTF-8" 


    /** Character encoding for current input file.
    */
    def inFileEncoding = "UTF-8" 

    // Coordinate these
    def nodeIdLists = []
    def schemeIndex = []
    def tripletIndex = []


    /** A map including closures implementing the javax.xml.xpath.Namespace interface.
    * The namespace mappings themselves are stored in the map nsMap, and
    * includes by default a mapping of the TEI namespace to the prefix abbreviation
    * 'tei' : other mappings can be added with the addNSMapping method().
    */
    def groovyNSClosure =  [
        // Mapping of prefixes to namespaces:
        nsMap : ['tei':'http://www.tei-c.org/ns/1.0'],

        // The three methods defined by the javax.xml.namespace.NamespaceContext
        // interface, as closures:
        getNamespaceURI : {String nsPrefix -> groovyNSClosure.nsMap[nsPrefix]} ,

        getPrefix :{String nsUri -> 
            groovyNSClosure.nsMap.find {it.value == nsUri}?.key
        } ,

        // return a clunky java list iterator
        getPrefixes :
            {String nsUri ->
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


    Tabulator() {
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

        if (debug > 0) { System.err.println "COLLECTING CITABLES WITH " + citeSchemes}
        citeSchemes.eachWithIndex { citeScheme, schemeNum ->
            // We can get IDs for all citable nodes in two passes:
            //
            // [1] collect every terminal node above the 
            // leaf level of the hierarchy
            citeScheme.eachWithIndex { triplet, idx ->
                if (debug > 0) { 
                    System.err.println "use triplet ${triplet} of class ${triplet.getClass()}"
                    if (debug > 8)
                    System.err.println "It has terminal pattern " + triplet.getTerminalNodePattern()
                }
                if (triplet.allowsTerminalNode()) {
                    if (debug > 0) { System.err.println "... allows terminal nodes"}
                } else {
                    if (debug > 0) { System.err.println "... no terminal nodes at level ${idx}"}
                }
                if (triplet.allowsTerminalNode()) {

                    // then we need to process it, so record by index numbers
                    // what scheme and triplet we use
                    this.schemeIndex.leftShift(schemeNum)
                    this.tripletIndex.leftShift(idx)

                    def xp = triplet.containerXpath() + triplet.terminalNodeXpath()

                    if (debug > 0) { System.err.println "GET NODES FOR ${xp}" }
                    def nodesForTerminal = nodeIdsForXpath(xp,parsedRoot)
                    this.nodeIdLists.leftShift(nodesForTerminal)

                    if (debug > 0) {
                        println "collectCitableIds: collect nodes for xp ${xp} yields ${nodeIdLists.size()} lists, first of which contains ${nodeIdLists[0].size()} objects"
                        println "nodeList index ${nodeIdLists.size() -1} : indexed to cite scheme  ${schemeNum}, triplet ${idx}" 
                    }

                } else {
                    if (debug > 0) {
                        println "\t... no terminal nodes at level ${idx}" 
                    }
                }
            }
        }
        //
        //[2] collect all leaf nodes of the citation tree:
        this.cm.getXpathList().eachWithIndex { xp, xpNum ->
            if (debug > 0) {
                System.err.println "XPATH: ${xp} at num ${xpNum}"
                System.err.println "Using scheme index " + this.schemeIndex
            }

            // record index for scheme number, and leaf-node triplet
            this.schemeIndex.leftShift(xpNum)
            def cs = citeSchemes[xpNum]
            this.tripletIndex.leftShift(cs.size() - 1)
            this.nodeIdLists.leftShift(nodeIdsForXpath(xp,parsedRoot))
            
        }
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


    /**
    * Writes a tabulated representation of a canonically citable XML file.
    * @param urn CTS URN of the document to tabulate.
    * @param inv TextInventory with information about how to cite the document in question.
    * @param srcFile Source file to tabulate.
    * @param outputDir Writable directory where tabular output will go.
    */
    public void tabulate(CtsUrn urn, TextInventory inv, File srcFile, File outputDir) 
    throws java.io.FileNotFoundException,IOException,SecurityException,Exception  {
        this.outFileBaseName = FilenameUtils.getName(srcFile.getAbsolutePath()).replace(/.xml/,'')
        if (debug > 0) {
            System.err.println "TABULATING urn ${urn} to fileBase " + outputDir + " using base file name " + this.outFileBaseName
        }

        this.cm = inv.getCitationModel(urn)
        if (cm) {
            if (debug > 0) {System.err.println "CitationModel is ${cm}"}
        } else {
            System.err.println "Could not find citation model for urn ${urn}!"
            throw new Exception("Tabulator:  could not find citation model for ${urn}")
        }
        def nsMaps = inv.getNsMapList()
        def oneMap = nsMaps[urn.toString()]

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
            this.fileCount++;
            String leadingZeros = ""
            if (this.fileCount >= 10000) {
            } else if (this.fileCount >= 1000) {
                leadingZeros = "0"
            } else if (this.fileCount >= 100) {
                leadingZeros = "00"
            } else if (this.fileCount >= 10) {
                leadingZeros = "000"
            } else {
                leadingZeros = "0000"
            }

            def newFName = "${outFileBaseName}-${leadingZeros}${this.fileCount}.txt"
            if (debug > 0) {System.err.println "Tabulate to " + newFName + "when file count is " + this.fileCount}
            this.currOutFile = new File(outputDir, newFName)
            currOutFile.setText("") 
            /* Begin by blindly adding every namespace record in the service to our output buffer: */
            oneMap?.keySet().each {
                /* We're trying to write this pattern:
               namespace#ABBR#FULLURI#LABEL 
               */
                this.currOutFile.append("namespace${columnSeparator}${it}${columnSeparator}${oneMap[it]}${columnSeparator}\n",this.outFileEncoding)
            }


            if (debug > 0) { println "STEP 1: collecting all node IDs" }
            collectCitableIds()
  
            // Now walk through entire document, and check for nodes with ids
            // contained in our citableNodes list
              tabFromTree(parsedRoot, urn, outputDir)

        } else {
            System.err.println "NO PARSEABLE ROOT FOR FILE ${f}"
        }
    }
    // end of tabulate method

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
        StringBuffer buff = new StringBuffer(n.getAttribute(citeAttr))
        n = n.getParentNode()

        def ancestors = triplet.getScopePattern()
        def ancestorParts = ancestors.split(/\\//)
        // Total number of ancestor elements to find.
        // Use this as an index to walk back trhough
        // the ancestorParts array.
        def lastIndex = ancestorParts.size() - 1

        boolean done = false
        while (!done) {
            def part = ancestorParts[lastIndex]
            if (part ==~ /.+['"]?['"].+/) {
                citeAttr = scheme[tripletIndex].getLeafVariableAttribute()
                def nodeVal = n.getAttribute(citeAttr)
                if (debug > 0) {
                    println "on node ${n.getLocalName()}"
                    println "nodeVal = " + nodeVal
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




    /** Creates an ordered tabular representation of all nodes in a given (sub)document
    * with nodeIds appearing in the list of desired nodes.  Walks the tree of all nodes
    * below n, and checks their nodeId against idList:  if the nodeId appears there,
    * adds a line for that node to the accumulating buffer, buff.
    * @param n The node to tabulate.
    * @param urn Urn (without reference component) of document being tabulated.
    */
    private void tabFromTree(Node n, CtsUrn urn, File outputDir) {
        // we need to do xpath transforms in this method ...
        TransformerFactory tf = TransformerFactory.newInstance()
        Transformer xform = tf.newTransformer()
        xform.setOutputProperty("omit-xml-declaration", "yes")

        // total matching nodes in all our lists:
        int nodeMax = 0
        this.nodeIdLists.each {
            nodeMax =+ it.size()  + this.nodesProcessed
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


                    def record = "${urn}:${refVal}${columnSeparator}${this.nodesProcessed}${columnSeparator}${prevCount}${columnSeparator}${nextCount}${columnSeparator}${explicitAncPath}${columnSeparator}${nodeText}${columnSeparator}${ancestors}${leafPatt}\n" 
                    
                    if ((this.nodesProcessed % this.chunkSize) == 0) {
                        this.fileCount++;
            String leadingZeros = ""
            if (this.fileCount >= 10000) {
            } else if (this.fileCount >= 1000) {
                leadingZeros = "0"
            } else if (this.fileCount >= 100) {
                leadingZeros = "00"
            } else if (this.fileCount >= 10) {
                leadingZeros = "000"
            } else {
                leadingZeros = "0000"
            }
                        this.currOutFile = new File(outputDir, "${this.outFileBaseName}-${leadingZeros}${this.fileCount}.txt")
if (debug > 0) {                        System.err.println "NEW FILE with chunk size ${this.chunkSize}: ${this.outFileBaseName}-${this.fileCount}.txt"}

                    }

                    this.currOutFile.append(record)
                }
                // end: if we found a match


            }
            // end:  check each index

            // continue recursion to check rest of tree
            tabFromTree(kid, urn, outputDir)
        }
        
    }





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
        StringBuffer buff = new StringBuffer()

        CitationTriplet triplet = scheme[tripletIndex]
        def ancestors = triplet.getScopePattern()
        def ancestorParts = ancestors.split(/\\//)
        // Total number of ancestor elements to find.
        // Use this as an index to walk back trhough
        // the ancestorParts array.
        def lastIndex = ancestorParts.size() - 1

        if (debug > 0) {
            println "fillAncestorPath: node ${currNode.getLocalName()} with triplet ${triplet}" 
            println "Looking for ${tripletIndex} citaion values to substitute in, and have scheme ${scheme}."
        }
        

        // Cycle through all ancestor elements in the XPath template, from nearest back to root.
        // Check if the template for each element includes the citation variable expression '?'
        // If it does, snag that value from the appropriate attribute of currNode, and substitute
        // it into our results.
        boolean done = false
        while (!done) {
            def part = ancestorParts[lastIndex]
            
            if (part ==~ /.+['"]?['"].+/) {
                def citeAttr = scheme[tripletIndex].getLeafVariableAttribute()
                def nodeVal = currNode.getAttribute(citeAttr)
                part = part.replace(/?/,nodeVal)
                tripletIndex--;
                if (debug > 0) {
                    println "\tSubstitute in value ${nodeVal} for part " + part;
                    println "Decremenet triplet index to ${tripletIndex}"
                }
            }
            buff.insert(0, "/${part}")
            if (debug > 0) {
                println "${lastIndex}: ${part} --> ${buff.toString()} for node ${currNode.getLocalName()}"
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
