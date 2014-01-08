package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn
import org.apache.commons.io.FilenameUtils


class HmtGreekPoetry extends DefaultDocumentConfiguration implements DocumentConfiguration {
    boolean debug = false
    String lexurnbase = "urn:cite:perseus:lextoken"
    String numurnbase = "urn:cite:hmt:numerictoken"
    String nameurnbase = "urn:cite:hmt:namedentitytoken"
    String literalurnbase = "urn:cite:hmt:literaltoken"


    static String id = "hmttei"
    static String description = "Greek document in CHS/HMT conventions."


    /** The LanguageWritingCharset component of this document.
    */
    LanguageWritingCharset lwc


    /* ** the following properties must be set in order to tokenize ** 
    *
    */
    /** An appropriate TokenizationSystem to apply to this document. */
    TokenizationSystem tokenSystem
    
    /** Write-accessible directory for creating temporary files */
    File workDir

    /** A TextInventory with metadata for this document. */
    TextInventory inv
    
    /** A CTS URN for this document. */
    CtsUrn urn
    /* end properties for tokenizing 
    * ****************************** */

    File document
    HmtGreekPoetry(CtsUrn docUrn, TextInventory docTI, File f) {
            super(docUrn, docTI, f)
            
            this.urn = docUrn
            this.inv = docTI
            this.document = f
            this.lwc = new DefaultLanguageWritingCharset()
            this.tokenSystem = new HmtGreekTokenization()
            this.workDir = new File("/tmp")
        }


    String getDescription() {
        return description
    }

    String getId() {
        return id
    }

    boolean isValid() {
        //...
        return true
    }
    
    void writeTokenUrns(File turtleFile) {
        turtleFile.append("@prefix lex:        <http://data.perseus.org/rdfverbs/> .\n", "UTF-8")
        turtleFile.append("@prefix hmt:        <http://homermultitext.org/hmt/rdfverbs/> .\n", "UTF-8")


        if (!this.document) {
            throw new Exception("DefaultDocumentType:  no document defined.")
b        }
        if (!this.inv) {
            throw new Exception("DefaultDocumentType:  no TextInventory to use for tabulation defined.")
        }
        Tabulator tab = new Tabulator()
        tab.tabulate(this.urn, this.inv, this.document,this.workDir)
        
        def baseName = FilenameUtils.getName(this.document.getAbsolutePath().replace(/.xml/,''))

        if (debug) { System.err.println "base name ${baseName}"}
        def p = ~/${baseName}.*/
        this.workDir.eachFileMatch(p) { f ->
            System.err.println "Tokenizing ${f}"
            def rawTokens = this.tokenSystem.tokenize(f, tab.columnSeparator)

            rawTokens.each { tokenPair ->
                String ctsval =  tokenPair[0].replaceAll(/\n/,"")

                CtsUrn urn = new CtsUrn(ctsval)
                if (debug) {System.err.println urn}
                String subref = urn.getSubref1()
                if (subref) {
                    subref = subref.replaceAll(/^[\(\[]/,"")
                    subref = subref.replaceAll(/[.,;?]$/, "")
                }
                switch (tokenPair[1]) {
                    case "lexical":
                        turtleFile.append("${lexurnbase}.${subref} lex:occursIn ${ctsval} .\n", "UTF-8")
                        break

                    case "numeric":
                        turtleFile.append("${numurnbase}.${subref} lex:occursIn ${ctsval} .\n", "UTF-8")
                    break

/*                    case "namedEntity":
                        turtleFile.append("${nameurnbase}.${subref} lex:occursIn ${ctsval} .\n", "UTF-8")
                    break
*/

                    case "waw":
                        turtleFile.append("${literalurnbase}.${subref} lex:occursIn ${ctsval} .\n", "UTF-8")
                    break

                    default : 
                        def val = tokenPair[1]
                        def partList = val.split(":")
                        if ((partList.size() == 2) && (partList[0] == 'namedEntity') ) {
                            turtleFile.append("${nameurnbase}.${partList[1]} lex:occursIn ${ctsval} .\n", "UTF-8")
                            System.err.println "${nameurnbase}.${partList[1]} lex:occursIn ${ctsval} ."

                        } else {
                            System.err.println "Unrecognized token type: ${tokenPair[1]}"
                        }
                        break
                }

            }
            
        }


    }

}
