package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory


/* This is the column layout of the HMT 7-column format we want to produce as one
form of output of this class can generate:

NODEURN#SEQ#PREV#NEXT#XML#TEXTCONTENTS#XML
*/

/**
* 
*/
class EditionGenerator {

    def debug = 1

    EditionGenerator() {
    }

    void generateEdition(ArrayList tokens, File outDir, String outFileName, String editionExtension, TextInventory ti) {

        File tokenTtl = new File(outDir, outFileName)

        File msgs 
        if (debug > 0) {
            msgs =  new File(outDir, "msgs.ttl")
        }

        CtsTtl ctsTtl = new CtsTtl(ti)

        String currNoPassage = ""
        String prevNoPassage = ""
        String prevPassageNode = ""


        Integer currSeq = 0
        Integer prevSeq = 0
        Integer prevprevSeq = 0

        String currText = ""
        String prevText = ""

        String urnBase = ""
        String tokenUrn = ""
        String alignedUrnStr = ""
        CtsUrn urn 
        CtsUrn alignedUrn 

        tokens.each { t ->
            def urnStr = t[0]
            try {
                urn = new CtsUrn(urnStr)


            } catch (Exception e) {
                System.err.println "EditionGenerator: could not form urn from ${urnStr}"
            }


            currSeq++;
            prevText = currText
            currText = urn.getSubref1()
            
            currNoPassage = urn.getUrnWithoutPassage()
            if (prevNoPassage == "") {
                urnBase = "${currNoPassage}${editionExtension}"
            } else {
                urnBase = "${prevNoPassage}${editionExtension}"
            }
            
            prevprevSeq = prevSeq
            prevSeq = currSeq
                

            if (prevPassageNode == "")  {
                tokenUrn = "${urnBase}:${urn.getPassageNode()}.${prevSeq}"
            } else {
                tokenUrn = "${urnBase}:${prevPassageNode}.${prevSeq}"

            }
            prevPassageNode = urn.getPassageNode()
            
            if ((debug > 0) && (prevText != "" )) { 
                msgs.append("${prevText} AT ${tokenUrn}\n", "UTF-8") 
            }
            if (currNoPassage == prevNoPassage) {
                String pVal = ""
                if (prevprevSeq > 0) {
                    pVal = "${prevprevSeq}"
                }
                if ((prevSeq > 0) && (prevText != "") ) {
                    String continueLine = "${tokenUrn}#${prevSeq}#${pVal}#${currSeq}##${prevText}##\n" 
                    tokenTtl.append(ctsTtl.turtleizeTabs(continueLine, false), "UTF-8")
                    tokenTtl.append("<${tokenUrn}> hmt:alignsWith <${alignedUrn}@${prevText}> .\n", "UTF-8")
                    tokenTtl.append("<${alignedUrn}@${prevText}> hmt:hasAligned <${tokenUrn}>  .\n", "UTF-8")

                }
                
            } else {
                if (debug > 0) { 
                    msgs.append("Ed.Gen.: NEW WORK: " + currNoPassage + " at curr ${currSeq} and prev ${prevSeq}\n") 
                }

                if (prevSeq > 0) {
                    /* there was an earlier work we need to complete: */
                    urnBase = "${prevNoPassage}${editionExtension}"
                    tokenUrn = "${urnBase}:${urn.getPassageNode()}.${currSeq}"

                    if (prevText != "") {
                        String appendLine =  "${tokenUrn}#${prevSeq}#${prevprevSeq}###${prevText}##\n\n"
                        tokenTtl.append(ctsTtl.turtleizeTabs(appendLine, false), "UTF-8")
                        tokenTtl.append("<${tokenUrn}> hmt:alignsWith <${alignedUrn}@${prevText}> .\n", "UTF-8")
                        tokenTtl.append("<${alignedUrn}@${prevText}> hmt:hasAligned <${tokenUrn}>  .\n", "UTF-8")
                    }
                }

                prevNoPassage = currNoPassage
                currSeq = 0
                prevSeq = 0
                prevprevSeq = 0
            }

            alignedUrn = new CtsUrn("urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}.${urn.getVersion()}:${urn.getPassageNode()}")
        }

        urnBase = "${prevNoPassage}${editionExtension}"
        tokenUrn = "${urnBase}:${urn.getPassageNode()}.${currSeq + 1}"
        if (debug > 0) { msgs.append("And append ${currText} AT ${tokenUrn}\n", "UTF-8") }
        String theVeryLastLine = "${tokenUrn}#${currSeq + 1}#${currSeq}###${currText}##\n"
        if (debug > 0) { msgs.append ("using ${theVeryLastLine}\n", "UTF-8") } 
        tokenTtl.append(ctsTtl.turtleizeTabs(theVeryLastLine, false), "UTF-8")
        tokenTtl.append("<${tokenUrn}> hmt:alignsWith <${urn}> .\n", "UTF-8")
        tokenTtl.append("<${urn}> hmt:hasAligned <${tokenUrn}>  .\n", "UTF-8")
    }

}

