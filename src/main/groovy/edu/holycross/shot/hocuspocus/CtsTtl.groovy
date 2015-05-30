package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory



/** Class managing serialization of a CTS archive as RDF TTL.
*/
class CtsTtl {


  String release = "0.13.0"
  
  Integer WARN = 1
  Integer DEBUGLEVEL = 2
  Integer FRANTIC = 3
  
  Integer debug = 0

  /** RDF prefix declarations */
  String prefixStr = """
@prefix hmt:        <http://www.homermultitext.org/hmt/rdf/> .
@prefix cts:        <http://www.homermultitext.org/cts/rdf/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix dcterms: <http://purl.org/dc/terms/> .

""".toString()

    /** String used to separate columns in tabular files. */
    String separatorValue = "#"

    /** TextInventory object for CTS archive to turtleize.
    * initialized to null. 
    */
    TextInventory inventory 

    /** Constructor with single parameter. 
    * @param ti TextInventory object for a CTS archive.
    */
    CtsTtl(TextInventory ti) {
        this.inventory = ti
    }


    

    /** Translates the contents of a CTS TextInventory to
    * RDF TTL.
    * @param inv TextInventory for the archive.
    * @returns A String of TTL statements.
    */
    String turtleizeInv(TextInventory inv) {
        this.inventory = inv
        return turtleizeInv(false)
    }

    /** Translates the contents of a CTS TextInventory to
    * RDF TTL.
    * @param inv TextInventory for the archive.
    * @param prefix Whether or not to include RDF prefix statements.
    * @returns A String of TTL statements.
    */
    String turtleizeInv(TextInventory inv, boolean prefix) {
        this.inventory = inv
        return turtleizeInv(prefix)
    }

    /** Translates the contents of a CTS TextInventory to
    * RDF TTL.
    * @param inv TextInventory for the archive.
    * @throws Exception if inventory is not defined.
    * @returns A String of TTL statements.
    */
    String turtleizeInv() {
        return turtleizeInv(false)
    }


    /** Translates the contents of a CTS TextInventory to
    * RDF TTL.
    * @param prefix Whether or not to include RDF prefix statements.
    * @throws Exception if inventory is not defined.
    * @returns A String of TTL statements.
    */
    String turtleizeInv(boolean includePrefix) 
    throws Exception {
        if (this.inventory == null) {
            throw new Exception("CtsTtl: no TextInventory defined.")
        } 

        StringBuilder reply = new StringBuilder()
        if (includePrefix) {
	  reply.append(prefixStr)
        }
	def mapSize =	inventory.nsMapList.keySet().size() 
	if (mapSize > 0) {
	} else {
	  System.err.println "CtsTtl:turtleizeInv:  empty nsMapList!"
	}

        // XML namespace information:
        // Naive assumption that only one abbr. per URI
        // in a corpus should be reworked.
        def nsSeen = [:]
        this.inventory.nsMapList.keySet().each { urn ->
	  if (debug > WARN) {
	    System.err.println "CtsTtl:turtleizeInv: ns map for urn ${urn}"
	  }
	  def nsMap = this.inventory.nsMapList[urn]
	  nsMap.keySet().each { nsAbbr ->
	    if (! nsSeen[nsAbbr]) {
	      reply.append("<${nsMap[nsAbbr]}> cts:abbreviatedBy " + '"' + nsAbbr + '" .\n')
	      nsSeen[nsAbbr] = true
	    }
	    reply.append "<${urn}> cts:xmlns <${nsMap[nsAbbr]}> .\n"
	  }
        }

        def elementSeen = []
        this.inventory.allOnline().each { u ->
	  try {
	    // these are always version-level URNs.
	    CtsUrn urn = new CtsUrn(u)
	    String groupStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}:"
	    String groupLabel = this.inventory.getGroupName(urn)
	    String workStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}:"
	    String workLabel = "${groupLabel}, ${this.inventory.workTitle(workStr)}:"
	    String ctsNsStr = "urn:cts:${urn.getCtsNamespace()}"

	    reply.append("<${u}> cts:belongsTo <${workStr}> .\n")
	    reply.append("<${workStr}> cts:possesses <${u}> .\n")

	    System.err.println "TEST ${urn} for language: " + this.inventory.languageForVersion(urn)
	    String versionLang =  this.inventory.languageForVersion(urn)
	    String workLang = this.inventory.languageForWork(workStr)


	    
	    /* >>>>>>>>>>>> BROKEN CODE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< */
	    System.err.println "TEST ${urn} for language: " + this.inventory.languageForVersion(urn)
	    System.err.println "TEST ${workLang} for language: " + this.inventory.languageForVersion(workStr)
	    // THIS IS FAILING?
	    reply.append("<${urn.toString()}> dcterms:title " + '"' + this.inventory.versionLabel(u) + '" .\n')
	    if (versionLang != workLang) {
	      reply.append("\n<${urn.toString()}> rdf:type cts:Translation .\n")
	      reply.append("\n<${urn.toString()}>  cts:translationLang " + '"' + versionLang + '" .\n\n')

	    } else {
	      reply.append("\n<${urn.toString()}> rdf:type cts:Edition .\n")
	    }

	    reply.append("\n<${urn.toString()}>  rdf:label " + '"' + "${workLabel} (${this.inventory.versionLabel(urn)})" + '" .\n\n')

	    if (! elementSeen.containsAll([workStr])) {
	      reply.append("<${workStr}> rdf:type cts:Work .\n")
	      reply.append("<${workStr}> dcterms:title " + '"' + this.inventory.workTitle(workStr) + '" .\n')
	      reply.append("<${workStr}> cts:belongsTo <${groupStr}> .\n")
	      reply.append("<${groupStr}> cts:possesses <${workStr}> .\n")
	      elementSeen.add(workStr)
	      reply.append("\n<${workStr}> cts:lang " + '"' + this.inventory.languageForWork(workStr) + '" .\n\n')
	      reply.append("\n<${workStr}> rdf:label " + '"' + "${workLabel}" + '" .\n\n')
	    }

	    if (! elementSeen.containsAll([groupStr])) {
	      elementSeen.add(groupStr)
	      reply.append("<${groupStr}> cts:belongsTo <${ctsNsStr}> .\n")
	      reply.append("<${ctsNsStr}> cts:possesses <${groupStr}> .\n")
	      reply.append("<${groupStr}> rdf:type cts:TextGroup .\n")
	      reply.append("<${groupStr}> dcterms:title " + '"' + this.inventory.getGroupName(groupStr) + '" .\n')
	      reply.append("\n<${groupStr}> rdf:label " + '"' + "${groupLabel}" + '" .\n\n')
	    }

	  } catch (Exception e) {
                System.err.println "CtsTtl: exception in turtleizeInv for urn ${u}. ${e}"
            }
        }
        return reply.toString()
    }



  /** Generates ten TTL statements describing the citable text node
   * documented in the single input line tabLine.  In addition to these
   * ten statements, a complete OHCO2 description of a node in RDF requires
   * two further statements identifying next and previous nodes (for a total
   * of twelve RDF statements to document an OHCO2 node).  Those are computed
   * in a separate pass through a tabulated input source that resolves sequence
   * numbers in the input to corresponding URN values for the nodes.
   * @param tabLine A delimited String tabulating a citable text node
   * in the OHCO2 model.
   * @returns A ten-line String composed of ten RDF statements,
   * one TTL-formatted statement per line.
   */
  String turtleizeLine(String tabLine) {
    if (debug > 0 ) { 
      System.err.println "CtsTtl: Turtleize line ||${tabLine}||"
    }
    
    // buffer for TTL output:
    StringBuffer turtles = new StringBuffer()
    
    /* Incredible kludge to handles groovy's String split behavior.
       See note here for explanation:
       http://jermdemo.blogspot.de/2009/07/beware-groovy-split-and-tokenize-dont.html
    */
    def cols = "${tabLine} ".split(separatorValue)
    String urnVal = cols[0]
        if (debug > 0) {
	  System.err.println "COLS are ${cols} with urnVal  " + urnVal 
	}
        String seq = cols[1]
        String prev = cols[2]
        String next = cols[3]
        String xmlAncestor= cols[4]
        String textContent = cols[5]
        String xpTemplate = cols[6].replaceAll(/[ ]+$/,"")
        if (debug > 0) {
            System.err.println "Trimmed xpTemplate back to ||${xpTemplate}||"
        }

	if (debug > 0) {
	  System.err.println "Prev/next cols are " + prev + ":" + next
	}
        CtsUrn urn = null
        try {
            urn = new CtsUrn(urnVal)
        } catch (Exception e) {
            System.err.println "CtsTtl: Could not form URN from ${urnVal} : ${e}"
        }
        if (urn) {
	  String urnBase = urn.getUrnWithoutPassage()

	  String groupStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}:"
	  String groupLabel = this.inventory.getGroupName(urn)
	  String workStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}:"
	  String workLabel = "${groupLabel}, ${this.inventory.workTitle(workStr)}"
	  String label = "${workLabel} (${this.inventory.versionLabel(urnBase)}): ${urn.getPassageComponent()} (${urn})"
	  turtles.append("<${urn.toString()}> rdf:label " + '"' + label + '" .\n')


	  /* explicitly express version hierarchy */
	  /* should percolate up from any point based on RDF from TextInventory. */
	  turtles.append("<${urn.toString()}> cts:belongsTo <${urnBase}> . \n")

	  /* explicitly express citation hierarchy */
	  int max =  urn.getCitationDepth() 
	  String containedUrn = urn.toString()
	  turtles.append("<${urn.toString()}> cts:hasSequence ${seq} .\n")
	  turtles.append("<${urn.toString()}> cts:hasTextContent " + '"""' + textContent + '"""' +  " .\n")
	  turtles.append("<${urn.toString()}> cts:citationDepth ${max} .\n")
	  turtles.append("<${urn.toString()}> hmt:xmlOpen " +  '"' + xmlAncestor + '" .\n')
	  turtles.append("<${urn.toString()}> hmt:xpTemplate "  + '"' + xpTemplate + '" .\n')
	  while (max > 1) {
	    max--;
	    turtles.append("<${urn.toString()}> cts:containedBy <${urnBase}${urn.getPassage(max)}> .\n")
	    turtles.append("<${urnBase}${urn.getPassage(max)}> cts:contains <${urn.toString()}>  .\n")
	    turtles.append("<${urnBase}${urn.getPassage(max)}> cts:citationDepth ${max} .\n")
	  }
        }
        return turtles.toString()
    }

}
