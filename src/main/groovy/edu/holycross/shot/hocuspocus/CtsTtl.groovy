package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn


/** Class managing serialization of a CTS tabular data as RDF TTL.
* See the complementary XmlTabulator class to serialization a
* set of XML files to CTS 7-column tabular format.
*/
class CtsTtl {

  /** RDF prefix declarations. */
  static String prefixString = """
@prefix hmt:        <http://www.homermultitext.org/hmt/rdf/> .
@prefix cts:        <http://www.homermultitext.org/cts/rdf/> .
@prefix rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix dcterms:    <http://purl.org/dc/terms/> .
""".toString();

  /** TextInventory object for CTS archive to turtleize. */
  TextInventory inventory
  /** Citation configuration for CTS archive to turtleize. */
  CitationConfigurationFileReader citationConfig
  /** String value used as column separator in CTS 7-column data.*/
  String separatorValue = "#"

  /** Constructor with two parameters.
   * @param ti TextInventory object for a CTS archive.
   * @param conf CitationConfigurationReader object for a CTS archive.
   */
  CtsTtl(TextInventory ti, CitationConfigurationFileReader conf) {
    inventory = ti
    citationConfig = conf
  }

  /** Composes TTL string for unique mappings of abbreviations for
   * CTS namespaces to full URI.
   * @param ctsNsList A list of CTS namespace triplets.
   * @returns A series of valid TTL statements.
   */
  static String ctsNsTtl(ArrayList ctsNsList) {
    StringBuilder reply = new StringBuilder("")
    ctsNsList.each { triple ->
      reply.append("<urn:cts:${triple[0]}:> rdf:type cts:Namespace .\n")
      reply.append("<urn:cts:${triple[0]}:> cts:fullUri  <urn:cts:${triple[1]}> .\n")
      reply.append("<urn:cts:${triple[0]}:> rdf:label  " + '"""'+ triple[2].replaceAll(/\n/,'') + '""" .\n')
    }
    return reply.toString()
  }

  /** Composes TTL string for unique mappings of abbreviations for
   * XML namespaces to full URI.
   * @param namespaceMapList A map of text URNs to a mapping
   */
  static String xmlNsTtl(LinkedHashMap namespaceMapList) {
    StringBuilder reply = new StringBuilder()
    Set nsAbbrTtl = []
    namespaceMapList.keySet().each { urn ->
      def nsMap = namespaceMapList[urn]
      nsMap.keySet().each { nsAbbr ->
	nsAbbrTtl.add("<${nsMap[nsAbbr]}> cts:abbreviatedBy " + '"' + nsAbbr + '" .\n')
	nsAbbrTtl.add "<${urn}> cts:xmlns <${nsMap[nsAbbr]}> .\n"
      }
    }
    nsAbbrTtl.each {
      reply.append(it)
    }
    return reply.toString()
  }
  

  /** Composes RDF statements for a list of TextGroups.
   * @param tg List of URNs, as String values, identifying
   * TextGroups to describe in RDF.
   * @returns String of TTL statements.
   */
  static String textGroupTtl(ArrayList tg) {
    StringBuilder ttl = new StringBuilder()
    CtsUrn urn = new CtsUrn(tg[0])
    String parent = "urn:cts:${urn.getCtsNamespace()}"
    String label = tg[1]

    ttl.append("<${parent}> cts:possesses <${urn}> .\n")
    ttl.append("<${urn}> cts:belongsTo <${parent}> .\n")
    ttl.append("<${urn}> rdf:type cts:TextGroup .\n")
    ttl.append("<${urn}> dcterms:title  " + '"""' + label.replaceAll(/\n/,'') + '"""  .\n')
    ttl.append("<${urn}> rdf:label " + '"""' + label.replaceAll(/\n/,'') + '""" .\n')
    return ttl.toString()
  }


  /** Composes RDF for a specified work in a TextInventory.
   * @param wk URN, as a String value, for the work to document.
   * @param parent URN, as a String value, for the parent TextGroup.
   * @param ti TextInventory in which the work appears.
   * @returns A String of TTL statements.
   */
  static String workTtl(String wk, String parent, TextInventory ti) {
    StringBuilder ttl = new StringBuilder()
    ttl.append("<${parent}> cts:possesses <${wk}> .\n")
    ttl.append("<${wk}> cts:belongsTo <${parent}> .\n")
    
    String label = ti.workTitle(wk)
    ttl.append("<${wk}> rdf:label " + '"""' + label.replaceAll(/\n/,'') + '""" .\n')
    ttl.append("<${wk}> dcterms:title " + '"""' + label.replaceAll(/\n/,'') + '""" .\n')
    
    String lang = ti.worksLanguages[wk]
    ttl.append("""<${wk}> cts:lang "${lang}" .\n""")
    return ttl.toString()
  }


  /** Composes RDF for a specified version (edition or
   * translation) in a TextInventory.
   * @param vers URN, as a String value, for the version to document.
   * @param parent URN, as a String value, for the parent Work.
   * @param ti TextInventory in which the version appears.
   * @returns A String of TTL statements.
   */
  static String versionTtl(String vers, String parent, TextInventory ti) {
    StringBuilder ttl = new StringBuilder()
    ttl.append("<${parent}> cts:possesses <${vers}> .\n")
    ttl.append("<${vers}> cts:belongsTo <${parent}> .\n")
    
    if (ti.typeForVersion(new CtsUrn(vers)) == VersionType.EDITION) {
      ttl.append("<${vers}> rdf:type cts:Edition .\n")
      ttl.append("<${vers}> rdf:label " +   '"""' + ti.editionLabel(vers).replaceAll(/\n/,'') + '""" .\n')
      ttl.append("<${vers}> dcterms:title " +   '"""' + ti.editionLabel(vers).replaceAll(/\n/,'') + '""" .\n')
      
    } else {
      ttl.append("<${vers}> rdf:type cts:Translation .\n")
      ttl.append("<${vers}> rdf:label " +  '"""' + ti.translationLabel(vers).replaceAll(/\n/,'') + '""" .\n')
      ttl.append("<${vers}> dcterms:title  " +  '"""' + ti.translationLabel(vers).replaceAll(/\n/,'') + '""" .\n')
      ttl.append("""<${vers}> cts:lang "${ti.languageForVersion(vers)}" .\n""")
      
    }
    return ttl.toString()
  }


  /** Composes RDF for a specified exemplar in a TextInventory.
   * @param exempl URN, as a String value, for the exemplar to document.
   * @param parent URN, as a String value, for the parent version.
   * @param ti TextInventory in which the version appears.
   * @returns A String of TTL statements.
   */
  static String exemplarTtl(String exempl, String parent, TextInventory ti) {
    StringBuilder ttl = new StringBuilder()

    CtsUrn exUrn = new CtsUrn(exempl)
    ttl.append("<${parent}> cts:possesses <${exempl}> .\n")
    ttl.append("<${exempl}> cts:belongsTo <${parent}> .\n")
    ttl.append("<${exempl}> rdf:type cts:Exemplar .\n")
    ttl.append("<${exempl}> rdf:label " + '"""' +   ti.exemplarLabel(exUrn).replaceAll(/\n/,'') + '"""  .\n')
    ttl.append("<${exempl}> dcterms:title " + '"""' +   ti.exemplarLabel(exUrn).replaceAll(/\n/,'') + '"""  .\n')
    
    return ttl.toString()
  }

  /** Composes RDF description of works hierarchy
   * for all text groups in a TextInventory.
   * @param ti The TextInventory to document in RDF.
   * @returns A String of TTL statements.
   */
  static String biblTtl(TextInventory ti) {
    StringBuilder ttl = new StringBuilder()
    ti.textgroups.each { tg ->
      ttl.append(textGroupTtl(tg))
      ti.worksForGroup(new CtsUrn(tg[0])).each { wk ->
	ttl.append(workTtl(wk,tg[0],ti))
	ti.versionsForWork(wk).each { v ->
	  ttl.append(versionTtl(v,wk,ti))
	  ti.exemplarsForVersion(v).each { ex ->
	    ttl.append(exemplarTtl(ex,v,ti))
	  }
	}
      }
    }
    return ttl.toString()
  }


  
  String turtleizeValues(CtsUrn urn, String seq, String prev, String next, String xmlAncestor, String textContent, String xpTemplate, String xmlNs) {
    StringBuilder turtles = new StringBuilder()

    String urnBase = urn.getUrnWithoutPassage()
    String groupStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}:"
    String groupLabel = this.inventory.getGroupName(urn)
    String workStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}:"
    String workLabel = "${groupLabel}, ${this.inventory.workTitle(workStr)}"
    String label = "${workLabel} (${this.inventory.versionLabel(urnBase)}): ${urn.getPassageComponent()} (${urn})"

    turtles.append("<${urn.toString()}> rdf:label " + '"""' +  label.replaceAll(/\n/,'') + '""" .\n')

    /* explicitly express version hierarchy */
    /* should percolate up from any point based on RDF from TextInventory. */
    turtles.append("<${urn}> cts:isPassageOf <${urnBase}> . \n")
    turtles.append("<${urnBase}> cts:hasPassage <${urn}> . \n")

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
    return turtles.toString()
  }



  String turtleizeLines(String tabs, String ns, String nsabbr) throws Exception {
    StringBuilder turtles = new StringBuilder()
    tabs.readLines().each { l ->
      turtles.append(turtleizeLine(l, ns, nsabbr))
    }
    return turtles.toString()
  }

  /** Generates complete set of TTL statements describing a citable text node
   * documented in the single input line tabLine.
   * @param tabLine A String in 7-column CTS tabular format.
   * @param ns Full namespace value for the CTS namespace.
   * @param nsabbr Abbreviation for namespace, as used in CTS URN strings.
   * @returns A ten-line String composed of ten RDF statements,
   * one TTL-formatted statement per line.
   */
  String turtleizeLine(String tabLine, String ns, String nsabbr) throws Exception {
    // buffer for TTL output:
    StringBuffer turtles = new StringBuffer()
    
    /* Incredible kludge to handles groovy's String split behavior.
       See note here for explanation:
       http://jermdemo.blogspot.de/2009/07/beware-groovy-split-and-tokenize-dont.html
    */
    def cols = "${tabLine} ".split(separatorValue)
    
    if (cols.size() < 8) {
      throw new Exception("CtsTtl:turtleizeLine: wrong number of columns (${cols.size()}) in #${tabLine}#")
    } else {
      String urnVal = cols[0]
      String seq = cols[1]
      String prev = cols[2]
      String next = cols[3]
      String xmlAncestor= cols[4]
      String textContent = cols[5]
      String xpTemplate = cols[6].replaceAll(/[ ]+$/,"")
      String xmlNs = cols[7]
      CtsUrn urn = null
      try {
	urn = new CtsUrn(urnVal.replaceAll(/[\s]+/, ''))
      } catch (Exception e) {
	System.err.println "CtsTtl: Could not form URN from ##${urnVal}## : ${e}"
      }
      if (urn) {
	turtles.append("<${urn}> cts:xmlns <${ns}> .\n")
	turtles.append("""<${urn}> cts:xmlnsabbr "${nsabbr}" .\n""")
	
	turtles.append(turtleizeValues(urn,seq,prev,next,xmlAncestor,textContent,xpTemplate,xmlNs))
      } else {
	System.err.println "CtsTtl:turtleizeLine: could not form URN from " + urnVal
      }
    }
    return turtles.toString()
  }



  /**  Composes RDF statements for cts:prev and cts:next
   * relations defined in a tabular data source.
   * It first reads through the source to map URNs for citable
   * nodes to the integer sequence, keeping the mappings segregated
   * by the text they belong to. The structure is a map of maps:
   * work-level URNs are keys to get a map for that work of
   * sequence numbers to citable node URNs.
   * @param tabData Tabular-formatted data source.
   * @returns RDF statements in TTL expressing prev/next
   * relations of all citable nodes in the source data.
   */
  String turtleizePrevNext(String tabData) {
    // 1. Collect maps of sequences, keyed by work:
    def seqMapsForWorks = [:]
    tabData.eachLine { l ->
      def cols = "${l} ".split(separatorValue)
      if (cols.size() >= 7) {
	String urnVal = cols[0]
	String seq = cols[1]
	CtsUrn u
	try {
	  u = new CtsUrn(urnVal)
	  String workNoPsg = u.getUrnWithoutPassage()
	  if (! seqMapsForWorks.keySet().contains(workNoPsg) ) {
	    seqMapsForWorks[workNoPsg] = [:]
	  }
	  def currentMapping =  seqMapsForWorks[workNoPsg]
	  currentMapping[seq] = urnVal
	  seqMapsForWorks[workNoPsg] = currentMapping
	  
	} catch (Exception e) {
	  System.err.println "CtsTtl: failed to get record for ${urnVal}."
	  System.err.println "err ${e}"
	}
	

      } else {
	// ??
      }
    }

    // 2. Now generate P/N statements
    StringBuilder ttl = new StringBuilder()
    tabData.eachLine { l ->
      def cols = "${l} ".split(separatorValue)
      if (cols.size() >= 7) {
	String urnVal = cols[0]
	String seq = cols[1]
	String prv = cols[2]
	String nxt = cols[3]
	CtsUrn u
	try {
	  u = new CtsUrn(urnVal)
	  String workNoPsg = u.getUrnWithoutPassage()
	  LinkedHashMap currentMapping =  seqMapsForWorks[workNoPsg]
	  if ((prv != "") && (currentMapping[prv] != null)) {
	    ttl.append( "<${u}> cts:prev <${currentMapping[prv]}> .\n")
	  }
	  if ( (nxt != "") && (currentMapping[nxt] != null)) {
	    ttl.append( "<${u}> cts:next <${currentMapping[nxt]}> .\n")
	  }
	}  catch (Exception e) {
	  System.err.println "CtsTtl: failed to get record for ${urnVal}."
	  System.err.println "err ${e}"
	}
      }
    }
    return ttl.toString()
  }
  
  ///////////////////////////////////////////////////////////////////
  ///// Methods for generating TTL fragments and complete files



  /** Translates the contents of the TextInventory to
   * RDF TTL.
   * @returns A String of TTL statements.
   */
  String turtleizeInv() throws Exception {
    return turtleizeInv(inventory, citationConfig, true)
  }

  /** Translates the contents of a CTS TextInventory to
   * RDF TTL.
   * @param inv TextInventory to turtleize.
   * @param includePrefix Whether or not to include RDF prefix statements.
   * @throws Exception if inventory is not defined.
   * @returns A String of TTL statements.
   */
  static String turtleizeInv(TextInventory inv, CitationConfigurationFileReader config, boolean includePrefix) throws Exception {
    
    StringBuilder reply = new StringBuilder()
    if (includePrefix) {
      reply.append(prefixString)
    }
    def mapSize =   config.xmlNamespaceData.keySet().size()
    if (mapSize < 1) {
      System.err.println "CtsTtl:turtleizeInv:  no texts were mapped to XML namespaces."
    }
    // XML namespace information
    reply.append(xmlNsTtl(config.xmlNamespaceData))
    // CTS namespace information
    reply.append(ctsNsTtl(inv.ctsnamespaces))
    // Bibliographic hierarchy
    config.allOnline().each { urnStr ->
      CtsUrn urn = new CtsUrn(urnStr)
      reply.append(biblTtl(inv))
    }
    return reply.toString()
  }

  /** Translates the contents of a single tabuled file to
   * RDF TTL.
   * @param tabFile tabulaged file to turtleize
   * @param includePrefix Whether or not to include RDF prefix statements.
   * @throws Exception if inventory is not defined.
   * @returns A String of TTL statements.
   */
  String turtleizeFile(File tabFile, boolean includePrefix) {
    //StringBuilder ttl =  new StringBuilder(turtleizeInv())
    StringBuilder ttl =  new StringBuilder("")

    String ns = ""
    String nsabbr = ""
    tabFile.eachLine { l ->
      if (l ==~ /namespace.+/) {
	// use this.separatorValue !
	def cols = l.split(/#/)
	ns = cols[2]
	nsabbr = cols[1]
      } else {
	ttl.append(turtleizeLine(l,ns,nsabbr))
      }
    }

    String pnData = turtleizePrevNext(tabFile.getText())
    ttl.append(pnData)
    return ttl.toString()
  }
}
