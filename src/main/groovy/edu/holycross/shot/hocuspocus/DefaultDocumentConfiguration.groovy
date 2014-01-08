package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn
import org.apache.commons.io.FilenameUtils

class DefaultDocumentConfiguration implements DocumentConfiguration {

    /** The document we're classifying.
    */
    File document


    /** The LanguageWritingCharset component of this document.
    */
    LanguageWritingCharset lwc



    /* ************************************************************* *
       ** the following properties must be set in order to tokenize **/

    /** An appropriate TokenizationSystem to apply to this document. */
    TokenizationSystem tokenSystem
    
    /** Write-accessible directory for creating temporary files */
    File workDir

    /** A TextInventory with metadata for this document. */
    TextInventory inv
    
    /** A CTS URN for this document. */
    CtsUrn urn

    /* end properties for tokenizing 
    * *************************************************************  */

    /** Constructor specifying all parameters need to tokenize a source
    * document.
    * @param docUrn
    * @param docTi
    * @param doc The document we're classifying.
    */
    DefaultDocumentConfiguration(CtsUrn docUrn, TextInventory docTI, File f) {
        this.urn = docUrn
        this.inv = docTI
        this.document = f
        this.lwc = new DefaultLanguageWritingCharset()
        this.tokenSystem = new DefaultTokenizationSystem()
        this.workDir = new File("/tmp")
    }

    /** Defines the document this type applies to.
    * @param doc The document we're classifying, as a File.
    */
    void setDocument(File doc) {
        this.document = doc
    }

    /** Defines the LanguageWritingCharset component of this document.
    * @param langWritingChars A LanguageWritingCharset object defining
    * properties and behaviors of this document.
    */
    void setLanguageWritingCharset(LanguageWritingCharset langWritingChars) {
        this.lwc = langWritingChars
    }


    /** Describes the type of document.
    * @returns A descriptive String.
    */
    String getDescription() { 
        return "Unclassified document type."
    }


    /** Gets identifier for this type of document.
    * @returns The identifying String.
    */
    String getId() {
        return "unclassified"
    }

    /** Generically, this method determines if the document in question 
    * is valid according to some classification.
    * Since this implementation is for a default classification
    * that knows nothing about the structure of the document in question, 
    * it always returns false.
    * @throws Exception if no document has been defined.
    */
    boolean isValid() {
        if (!this.document) {
            throw new Exception("DefaultDocumentType:  no document defined.")
        }
        /* Default cannot be validated. */
        return false
    }


    /** Uses default configuration to tokenize the document.
    *  First creates a tabulated representation of the source,
    *  then
    *  @param outputFileName Name of file to write in this.workDir.
    *  @throws Exception if either document or accompanying TextInventory
    *  is undefined, or if document cannot be tabulated.
    */
    void writeTokenUrns(String outputFileName)  {
        File outputFile = new File(this.workDir, outputFileName)
        writeTokenUrns(outputFile)
    }

    void writeTokenUrns(File outputTokens) 
    throws Exception {
        if (!this.document) {
            throw new Exception("DefaultDocumentType:  no document defined.")
b        }
        if (!this.inv) {
            throw new Exception("DefaultDocumentType:  no TextInventory to use for tabulation defined.")
        }
        Tabulator tab = new Tabulator()
        File tabularDoc = new File(this.workDir, "tabulatedDoc.txt")
        tabularDoc.setText("")
        try {
            tab.tabulate(this.urn, this.inv, this.document, this.workDir)
        } catch (Exception  e) {
            throw e
        }

        def baseName = FilenameUtils.getName(this.document.getAbsolutePath().replace(/.xml/,''))
        System.err.println "base name ${baseName}"
        def p = ~/${baseName}.*/
        this.workDir.eachFileMatch(p) { f ->
            System.err.println "Tokenizing ${f}"
            def rawTokens = this.tokenSystem.tokenize(f, tab.columnSeparator)

            rawTokens.each { tokenPair ->
                String ctsval = tokenPair[0]
                CtsUrn urn = new CtsUrn(ctsval.replaceAll(/\n/,""))
                String subref = urn.getSubref1()
                subref = subref?.replaceAll(/^[\(\[]/,"")
                subref = subref?.replaceAll(/[.,;?]$/, "")
                outputTokens.append("${ctsval}\t${subref}\n", "UTF-8")
            }
            
        }


    }
}
