package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import edu.harvard.chs.cite.VersionType

/** Class managing serialization of a CTS archive as RDF TTL.
*/
class CtsTtl {
	static Integer debug = 0


	/* RDF prefix declarations. */
	static String prefixString = """
@prefix hmt:        <http://www.homermultitext.org/hmt/rdf/> .
@prefix cts:        <http://www.homermultitext.org/cts/rdf/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix dcterms: <http://purl.org/dc/terms/> .
""".toString();

	static String ctsNsAbbreviations = """
	<http://www.homermultitext.org/hmt/rdf> cite:abbreviatedBy "hmt" .
	<http://www.homermultitext.org/hmt/rdf> rdf:type cite:DataNs .
	<urn:cts:greekLit:> rdf:type cts:Namespace .
	<urn:cts:greekLit:> cts:fullUri <http://chs.harvard.edu/cts/ns/> .
	"""

	/* TextInventory object for CTS archive to turtleize. */
	TextInventory inventory
	CitationConfigurationFileReader citationConfig

	String separatorValue = "#"

	/** Constructor with two parameters.
	 * @param ti TextInventory object for a CTS archive.
	 * @param conf CitatioConfigurationReader object for a CTS archive.
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




	/** Translates the contents of a CTS tabular file to RDF TTL.
	 * @param tabFile A File in the cite library's 7-column tabular format.
	 * @param prefix Whether or not to include a declaration of the
	 * hmt namespace in the TTL output.
	 * @returns The TTL representation of tabFile's contents.
	 */
	/* String turtleizeTabs(String stringData, boolean prefix) {
		StringBuffer turtles = new StringBuffer()
		if (prefix) {
			turtles.append(prefixStr)
		}


		boolean foundIt = false
		stringData.eachLine { l ->
			def cols = "${l} ".split(separatorValue)

			if (debug > 0) { System.err.println "CtsTtl: ${l} cols as ${cols}, size ${cols.size()}" }
			if (cols.size() >= 7) {
				turtles.append(turtleizeLine(l) + "\n")
			} else if (cols.size() == 4) {
				// Ignore namespace declaration in 4 columns
			} else {
				System.err.println "CtsTtl: Too few columns! ${cols.size()} for ${cols}"
			}

		}

		turtles.append(turtleizePrevNext(stringData))


		if (turtles.toString().size() == 0) {
			System.err.println "CtsTtl: could not turtleize string " + stringData
		}
		return turtles.toString()
	}
	*/


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


	String turtleizeValues(CtsUrn urn,String seq,String prev,String next,String xmlAncestor,String textContent,String xpTemplate,String xmlNs) {
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

	String turtleizeLine(String tabLine, String ns, String nsabbr) throws Exception {
		// buffer for TTL output:
		StringBuffer turtles = new StringBuffer()

		/* Incredible kludge to handles groovy's String split behavior.
		See note here for explanation:
		http://jermdemo.blogspot.de/2009/07/beware-groovy-split-and-tokenize-dont.html
		 */
		def cols = "${tabLine} ".split(separatorValue)

		if (cols.size() < 8) {
			throw new Exception("CtsTtl:turtleizeLine: wrong number of columns in ${tabLine}")
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

	/* Methods for generating TTL fragments and complete files */


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
		inv.allOnline().each { urnStr ->
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
		System.err.println "PREVNEXT DATA: " + pnData
		ttl.append(pnData)

		return ttl.toString()
	}

}
