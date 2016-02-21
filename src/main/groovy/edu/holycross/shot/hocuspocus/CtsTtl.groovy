package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory



/** Class managing serialization of a CTS archive as RDF TTL.
*/
class CtsTtl {
  static Integer debug = 1


  /** RDF prefix declarations. */
  static String prefixString = """
@prefix hmt:        <http://www.homermultitext.org/hmt/rdf/> .
@prefix cts:        <http://www.homermultitext.org/cts/rdf/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix dcterms: <http://purl.org/dc/terms/> .

""".toString()


  static String ctsNsAbbreviations = """
<http://www.tei-c.org/ns/1.0> cts:abbreviatedBy "tei" .
<http://www.homermultitext.org/hmt/rdf> cite:abbreviatedBy "hmt" .
<http://www.homermultitext.org/hmt/rdf> rdf:type cite:DataNs .

<urn:cts:greekLit:> rdf:type cts:Namespace .
<urn:cts:greekLit:> cts:fullUri <http://chs.harvard.edu/cts/ns/> .
"""

  /** TextInventory object for CTS archive to turtleize.
   */
   TextInventory inventory

   /** Constructor with single parameter.
   * @param ti TextInventory object for a CTS archive.
   */
   CtsTtl(TextInventory ti) {
       inv = ti
   }

   /** Composes TTL string for unique mappings of abbreviations for
   * CTS namespaces to full URI.
   * @param ctsNsList A list of CTS namespace triplets.
   */
   static String ctsNsTtl(ArrayList ctsNsList) {
     StringBuilder reply = new StringBuilder("")
     ctsNsList.each { triple ->
       System.err.println "TRIPLE: " + triple
       reply.append("<urn:cts:${triple[0]}:> rdf:type cts:Namespace .\n")
       reply.append("<urn:cts:${triple[0]}:> cts:fullUri  <urn:cts:${triple[1]}> .\n")
       reply.append("<urn:cts:${triple[0]}:> rdf:label  " + '"""'+ triple[2] + '""" .\n')
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

   /** Translates the contents of a CTS TextInventory to
    * RDF TTL.
    * @param inv TextInventory to turtleize.
    * @param includePrefix Whether or not to include RDF prefix statements.
    * @throws Exception if inventory is not defined.
    * @returns A String of TTL statements.
    */
   static String turtleizeInv(TextInventory inv, boolean includePrefix) throws Exception {

     StringBuilder reply = new StringBuilder()
     if (includePrefix) {
       reply.append(prefixString)
     }
     def mapSize =   inv.nsMapList.keySet().size()
     if (mapSize < 1) {
       System.err.println "CtsTtl:turtleizeInv:  no texts were mapped to XML namespaces."
     }
    // XML namespace information
    reply.append(xmlNsTtl(inv.nsMapList))
    // CTS namespace information
    reply.append(ctsNsTtl(inv.ctsnamespaces))


/*
    def elementSeen = []
    inv.allOnline().each { u ->
      try {
	// these are always version-level URNs.
	CtsUrn urn = new CtsUrn(u)
	String groupStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}:"
	String groupLabel = inv.getGroupName(urn)
	String workStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}:"
	String workLabel = "${groupLabel}, ${inv.workTitle(workStr)}:"
	String ctsNsStr = "urn:cts:${urn.getCtsNamespace()}"

	reply.append("<${u}> cts:belongsTo <${workStr}> .\n")
	reply.append("<${workStr}> cts:possesses <${u}> .\n")

	if (debug > 0) {
	  System.err.println "TEST ${urn} for language: " + inv.languageForVersion(urn)
	}
	String versionLang =  inv.languageForVersion(urn)
	String workLang = inv.languageForWork(workStr)

*/

	/* >>>>>>>>>>>> BROKEN CODE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< */
/*	if (debug > 0) {
	  System.err.println "TEST ${urn} for language: " + inv.languageForVersion(urn)
	  System.err.println "TEST ${workLang} for language: " + inv.languageForVersion(workStr)
	}


	// THIS IS FAILING?
	reply.append("<${urn.toString()}> dcterms:title " + '"' + inv.versionLabel(u) + '" .\n')
	if (versionLang != workLang) {
	  reply.append("\n<${urn.toString()}> rdf:type cts:Translation .\n")
	  reply.append("\n<${urn.toString()}>  cts:translationLang " + '"' + versionLang + '" .\n\n')

	} else {
	  reply.append("\n<${urn.toString()}> rdf:type cts:Edition .\n")
	}

	reply.append("\n<${urn.toString()}>  rdf:label " + '"' + "${workLabel} (${inv.versionLabel(urn)})" + '" .\n\n')

	if (! elementSeen.containsAll([workStr])) {
	  reply.append("<${workStr}> rdf:type cts:Work .\n")
	  reply.append("<${workStr}> dcterms:title " + '"' + inv.workTitle(workStr) + '" .\n')
	  reply.append("<${workStr}> cts:belongsTo <${groupStr}> .\n")
	  reply.append("<${groupStr}> cts:possesses <${workStr}> .\n")
	  elementSeen.add(workStr)
	  reply.append("\n<${workStr}> cts:lang " + '"' + inv.languageForWork(workStr) + '" .\n\n')
	  reply.append("\n<${workStr}> rdf:label " + '"' + "${workLabel}" + '" .\n\n')
	}

	if (! elementSeen.containsAll([groupStr])) {
	  elementSeen.add(groupStr)
	  reply.append("<${groupStr}> cts:belongsTo <${ctsNsStr}> .\n")
	  reply.append("<${ctsNsStr}> cts:possesses <${groupStr}> .\n")
	  reply.append("<${groupStr}> rdf:type cts:TextGroup .\n")
	  reply.append("<${groupStr}> dcterms:title " + '"' + inv.getGroupName(groupStr) + '" .\n')
	  reply.append("\n<${groupStr}> rdf:label " + '"' + "${groupLabel}" + '" .\n\n')
	}

      } catch (Exception e) {
	System.err.println "CtsTtl: exception in turtleizeInv for urn ${u}. ${e}"
      }
    }*/
    return reply.toString()
  }
}
