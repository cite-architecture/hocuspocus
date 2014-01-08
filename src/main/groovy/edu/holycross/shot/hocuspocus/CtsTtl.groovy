package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

/*
http://jermdemo.blogspot.de/2009/07/beware-groovy-split-and-tokenize-dont.html
*/


/** Class managing serialization of a CTS archive as RDF TTL.
*/
class CtsTtl {


    def debug = 1

    /** RDF prefix declarations */
    String prefixStr = "@prefix hmt:        <http://www.homermultitext.org/hmt/rdf/> .\n@prefix cts:        <http://www.homermultitext.org/cts/rdf/> .\n@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n@prefix dcterms: <http://purl.org/dc/terms/> .\n"

    /** String used to separate columns in tabular files. */
    String separatorValue = "#"

    /** TextInventory object for CTS archive to turtleize.
    * initialized to null. 
    */
    TextInventory inventory 

    /** Constructor with single parameter. 
    * @param ti TextIvnentory object for a CTS archive.
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

        StringBuffer reply = new StringBuffer()
        if (includePrefix) {
            reply.append(prefixStr)
        }

        // XML namespace information:
        // Naive assumption that only one abbr. per URI
        // in a corpus should be reworked.
        def nsSeen = [:]
        this.inventory.nsMapList.keySet().each { urn ->
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
                String groupStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}"
                String groupLabel = this.inventory.getGroupName(urn)

                String workStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}"

                String workLabel = "${groupLabel}, ${this.inventory.workTitle(workStr)}"


                String ctsNsStr = "urn:cts:${urn.getCtsNamespace()}"

                reply.append("<${u}> cts:belongsTo <${workStr}> .\n")
                reply.append("<${workStr}> cts:possesses <${u}> .\n")

                String versionLang =  this.inventory.languageForVersion(urn)
                String workLang = this.inventory.languageForWork(workStr)
                reply.append("<${u}> dcterms:title " + '"' + this.inventory.versionLabel(u) + '" .\n')
                if (versionLang != workLang) {
                    reply.append("\n<${urn}> rdf:type cts:Translation .\n")
                    reply.append("\n<${urn}>  cts:translationLang " + '"' + versionLang + '" .\n\n')



                } else {
                    reply.append("\n<${urn}> rdf:type cts:Edition .\n")

                }
                reply.append("\n<${urn}>  rdf:label " + '"' + "${workLabel} (${this.inventory.versionLabel(urn)})" + '" .\n\n')

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




    /** Translates the contents of a CTS tabular file to RDF TTL.
    * @param tabFile A File in the cite library's 7-column tabular format.
    * @returns The TTL representation of tabFile's contents.
    */
    String turtleizeTabs(File tabFile) {
        return turtleizeTabs(tabFile, false)
    }

    /** Translates the contents of a CTS tabular file to RDF TTL.
    * @param tabFile A File in the cite library's 7-column tabular format.
    * @param prefix Whether or not to include a declaration of the
    * hmt namespace in the TTL output.
    * @returns The TTL representation of tabFile's contents.
    */
    String turtleizeTabs(File tabFile, boolean prefix) {
        return turtleizeTabs(tabFile.getText("UTF-8"), prefix)
    }

    
    String turtleizeTabs(String stringData, boolean prefix) {
        StringBuffer turtles = new StringBuffer()
        if (prefix) {
            turtles.append(prefixStr)
        }
        boolean foundIt = false
        def seqMaps = [:]
        stringData.eachLine { l ->
            def cols = "${l} ".split(separatorValue)

            if (debug > 0) { System.err.println "CtsTtl: ${l} cols as ${cols}, size ${cols.size()}" }
            if (cols.size() >= 7) {
                turtles.append(turtleizeLine(l) + "\n")
                String urnVal = cols[0]
                String seq = cols[1]
                CtsUrn u
                try {
                    u = new CtsUrn(urnVal)
                    String noPsg = u.getUrnWithoutPassage()
                    if (! seqMaps.keySet().contains(noPsg) ) {
                        seqMaps[noPsg] = [:]
                    }
                    def workMap = seqMaps[noPsg]
                    workMap[seq] = urnVal
                    seqMaps[noPsg] = workMap
                    foundIt = true

                } catch (Exception e) {
                    System.err.println "CtsTtl: failed to get record for ${urnVal}."
                    System.err.println "err ${e}"

                }
                    
            } else {
                System.err.println "CtsTtl: Too few columns! ${cols.size()} for ${cols}"
            }
        }
        if (foundIt) {
        stringData.eachLine { l ->
            def cols = l.split(separatorValue)
            if (cols.size() == 7) {
                String urnVal = cols[0]
                CtsUrn u
                try {
                    u = new CtsUrn(urnVal)
                } catch (Exception e) {
                    System.err.println "CtsTtl: failed to get record for ${urnVal}."
                }
                
                def currMap = seqMaps[u.getUrnWithoutPassage()]
                
                String prev = cols[2]
                String next = cols[3]
                if (currMap[next] != null) {
                    turtles.append("<${urnVal}> cite:next <${currMap[next]}> . \n")
                }
                if (currMap[prev] != null) {
                    turtles.append("<${urnVal}> cite:prev <${currMap[prev]}> . \n")
                }
            } else {
                System.err.println "Wrong size entry: ${cols.size()} cols in ${stringData}"
            }
        }
        }
        if (turtles.toString().size() == 0) {
            System.err.println "CtsTtl: could not turtelize string " + stringData
        }
        return turtles.toString()
    }




    /** Translates the contents of a single line from a tabular
    * input file into RDF TTL.
    * @param tabLine A String with a single line of 7-column 
    * tabular data.
    * @returns A String with the TTL expression of the line's 
    * contents.
    */
    String turtleizeLine(String tabLine) {
        if (debug > 0 ) { 
        System.err.println "CtsTtl: Turtleize line ||${tabLine}||"
        } 
        StringBuffer turtles = new StringBuffer()
        /* Incredible kludge! Handles groovy split behavior... */
        def cols = "${tabLine} ".split(separatorValue)
        String urnVal = cols[0]
        if (debug > 0) { System.err.println "COLS are ${cols} with urnVal is  " + urnVal }
        String seq = cols[1]
        String prev = cols[2]
        String next = cols[3]
        String xmlAncestor= cols[4]
        String textContent = cols[5]
        String xpTemplate = cols[6].replaceAll(/[ ]+$/,"")
        if (debug > 0) {
            System.err.println "Trimmed xpTemplate back to ||${xpTemplate}||"
        }


        CtsUrn urn = null
        try {
            urn = new CtsUrn(urnVal)
        } catch (Exception e) {
            System.err.println "CtsTtl: Could not form URN from ${urnVal}: ${e}"
        }
        if (urn) {
            String urnBase = urn.getUrnWithoutPassage()

//                CtsUrn urnStrip = new CtsUrn(urnBase)
                String groupStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}"
                String groupLabel = this.inventory.getGroupName(urn)

                String workStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}"

                String workLabel = "${groupLabel}, ${this.inventory.workTitle(workStr)}"


            String label = "${workLabel} (${this.inventory.versionLabel(urnBase)}): ${urn.getPassageComponent()} (${urn})"
            turtles.append("<$urn> rdf:label " + '"' + label + '" .\n')


            /* explicitly express version hierarchy */
            /* should percolate up from any point based on RDF from TextInventory. */
            turtles.append("<${urn}> cts:belongsTo <${urnBase}> . \n")

            /* explicitly express citation hierarchy */
            int max =  urn.getCitationDepth() 
            String containedUrn = urn.toString()
            turtles.append("<${urnVal}> cts:hasSequence ${seq} .\n")
            turtles.append("<${urnVal}> cts:hasTextContent " + '"""' + textContent + '"""' +  " .\n")
            turtles.append("<${urnVal}> cts:citationDepth ${max} .\n")
            turtles.append("<${urnVal}> hmt:xmlOpen " +  '"' + xmlAncestor + '" .\n')
            turtles.append("<${urnVal}> hmt:xpTemplate "  + '"' + xpTemplate + '" .\n')
            while (max > 1) {
                max--;
                turtles.append("<${containedUrn}> cts:containedBy <${urnBase}:${urn.getPassage(max)}> .\n")
                turtles.append("<${urnBase}:${urn.getPassage(max)}> cts:contains <${containedUrn}>  .\n")


                turtles.append("<${urnBase}:${urn.getPassage(max)}> cts:citationDepth ${max} .\n")
                containedUrn = "${urnBase}:${urn.getPassage(max)}"
            }
        }
        return turtles.toString()
    }

}
