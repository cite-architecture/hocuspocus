package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import edu.harvard.chs.cite.VersionType

/** Class managing serialization of a CTS archive as RDF TTL.
*/
class CtsTtl {
  static Integer debug = 0


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
   * @returns A series of valid TTL statements.
   */
   static String ctsNsTtl(ArrayList ctsNsList) {
     StringBuilder reply = new StringBuilder("")
     ctsNsList.each { triple ->
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

  static String textGroupTtl(ArrayList tg) {
    StringBuilder ttl = new StringBuilder()
    CtsUrn urn = new CtsUrn(tg[0])
    String parent = "urn:cts:${urn.getCtsNamespace()}"
    String label = tg[1]

    ttl.append("<${parent}> cts:possesses <${urn}> .\n")
    ttl.append("<${urn}> cts:belongsTo <${parent}> .\n")
    ttl.append("<${urn}> rdf:type cts:TextGroup .\n")
    ttl.append("""<${urn}> dc:terms cts:TextGroup "${label}" .\n""")
    ttl.append("""<${urn}> rdf:label cts:TextGroup "${label}" .\n""")

    return ttl.toString()
  }



  
  static String workTtl(String wk, String parent, TextInventory ti) {
    StringBuilder ttl = new StringBuilder()
    ttl.append("<${parent}> cts:possesses <${wk}> .\n")
    ttl.append("<${wk}> cts:belongsTo <${parent}> .\n")

    String label = ti.workTitle(wk)
    ttl.append("""<${wk}> rdf:label "${label}" .\n""")
    
    String lang = ti.worksLanguages[wk]
    ttl.append("""<${wk}> cts:lang "${lang}" .\n""")
    return ttl.toString()
  }


  static String versionTtl(String vers, String parent, TextInventory ti) {
    StringBuilder ttl = new StringBuilder()
    ttl.append("<${parent}> cts:possesses <${vers}> .\n")
    ttl.append("<${vers}> cts:belongsTo <${parent}> .\n")

    if (ti.typeForVersion(new CtsUrn(vers)) == VersionType.EDITION) {
      ttl.append("<${vers}> rdf:type cts:Edition .\n")
      ttl.append("""<${vers}> rdf:label  ${ti.editionLabel(vers)} .\n""")
      ttl.append("""<${vers}> dcterms:title  ${ti.editionLabel(vers)} .\n""")
      
    } else {
      ttl.append("<${vers}> rdf:type cts:Translation .\n")
      ttl.append("""<${vers}> rdf:label  ${ti.translationLabel(vers)} .\n""")
      ttl.append("""<${vers}> dcterms:title  ${ti.translationLabel(vers)} .\n""")
      ttl.append("""<${vers}> cts:lang "${ti.languageForVersion(vers)}" .\n""")
      
    }
    // test for xml namespace: add if online!
    
    return ttl.toString()
  }

  static String biblTtl(TextInventory ti) {
    StringBuilder ttl = new StringBuilder()
    ti.textgroups.each { tg ->
      ttl.append(textGroupTtl(tg))

      ti.worksForGroup(new CtsUrn(tg[0])).each { wk ->
	ttl.append(workTtl(wk,tg[0],ti))
	ti.versionsForWork(wk).each { v ->
	  ttl.append(versionTtl(v,wk,ti))
	}

      }
    }
    return ttl.toString()
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
    inv.allOnline().each { urnStr ->
      CtsUrn urn = new CtsUrn(urnStr)
      reply.append(biblTtl(inv))
    }





    
/*
    def elementSeen = []
    inv.allOnline().each { u ->
      try {
	// these are always version-level URNs.
	CtsUrn urn = new CtsUrn(u)

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
