package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.TextInventory
import edu.harvard.chs.cite.CtsUrn

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import org.apache.commons.io.FilenameUtils

/**
* Fundamental class representing the archival version of atext corpus 
* as a collection of XML documents cataloged by a CTS TextInventory.
*/
class Corpus {

    int debug = 2

    String defaultTokensFile = "tokens.tsv"

    /** Character encoding to use for all file output. */
    String charEnc = "UTF-8"

    /** TTL prefix declaration for HMT namespace. */
    String prefix = "@prefix hmt:        <http://www.homermultitext.org/hmt/rdf/> .\n@prefix cts:        <http://www.homermultitext.org/cts/rdf/> .\n@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n@prefix dcterms: <http://purl.org/dc/terms/> .\n"


    /** Map to configure default tokenization system for given ISO language codes. */
    LinkedHashMap languageToTokenSystemMap = ["grc": "edu.holycross.shot.hocuspocus.HmtGreekTokenization"]



    /** Map to configure default token edition generating system for given ISO language codes. */
    LinkedHashMap languageToTokenEditionMap = []

    /** TextInventory with entries for all documents in the corpus. 
    */
    TextInventory  inventory

    /** XML serialization of TextInventory information, in a File.
    */
    File  inventoryXml

    /** Root directory of file system containing archival files.
    */
    File baseDirectory

    /** String value defining columns in tabular text format. 
     */
    String separatorString = "#"

    /** Constructor using a local File object for the corpus' TextInventory.
    * @param invFile File with the corpus' TextInventory.
    * @baseDir Root directory of file system containing archival files.
    * @throws Exception if invFile is not a valid TextInventory, or if
    * archive directory is not readable
    */
    Corpus(File invFile, File baseDir) 
    throws Exception{
        try{
            this.inventory = new TextInventory(invFile)
        } catch (Exception e) {
            throw e
        }

        if (!baseDir.canRead()) {
            throw new Exception("Corpus: cannot read directory ${baseDir}")
        }
        this.baseDirectory = baseDir
        this.inventoryXml = invFile

        try {
            validateInventory()

        } catch (Exception invException) {
            throw invException
        }
    }


    /** Creates TTL representation of the entire corpus 
    * and writes it to a file in outputDir.
    * @param outputDir A writable directory where the TTL file
    * will be written.
    * @throws Exception if unable to write to outputDir.
    */
    void turtleizeRepository(File outputDir) 
    throws Exception {
        if (! outputDir.exists()) {
            try {
                outputDir.mkdir()
            } catch (Exception e) {
                System.err.println "Corpus.turtleizeRepository: could not make directory ${outputDir}"
                throw e
            }
        }
        File tabDir = new File(outputDir, "tabFiles")
        try {
            tabDir.mkdir()
        } catch (Exception e) {
            System.err.println "Corpus.turtleizeRepository: could not make directory ${tabDir}"
            throw e
        }
        this.tabulateRepository(tabDir)

        File ttlFile = new File(outputDir, "corpus.ttl")
        this.ttl(ttlFile, tabDir)
    }


    /** Creates a Tabulator object and uses it to tabulate
    * a given document.
    * @param f The source file in the archive to tabulate.
    * @param urn The URN for the file to tabulate.
    * @param outputDir A writeable directory for output files.
    */
    void tabulateFile(File f, CtsUrn urn, File outputDir) {
        Tabulator tab = new Tabulator()
        tab.tabulate(urn, inventory, f, outputDir)
    }



    /** Creates a tabular representation of every document
    * in the corpus.
    * @param ouputDir A writeable directory where tabulated files
    * will be written.
    */
    void tabulateRepository(File outputDir) {
        urnsInInventory().each { u ->
            CtsUrn urn = new CtsUrn(u)
            File f = new File(baseDirectory, inventory.onlineDocname(urn))
            if (debug > 0) {
                System.err.println "Corpus: tabulating ${urn} with file ${f}..."
            }

            tabulateFile(f, urn, outputDir)
       }
    }



    /** Cycles through all tabular files in a directory,
    * turtleizing each file.
    * Output is written to a file named "cts.ttl" in the same directory.
    * @param outputDir Directory containing tabular format files with names
    * ending in 'txt'.  Must be a writable directory.
    */
    void turtleizeTabs(File outputDir) {
        turtleizeTabs(outputDir, false)
    }


    /** Cycles through all tabular files in a directory,
    * first turtleizing each file.  If destructive is true, it
    * then deletes the source file.
    * Output is written to a file named "cts.ttl" in the same directory.
    * @param outputDir Directory containing tabular format files with names
    * ending in 'txt'.  Must be a writable directory.
    * @param destructive True if tab files should be deleted
    * after turtelizing.
    */
    void turtleizeTabs(File outputDir, boolean destructive) {
        turtleizeTabs(outputDir, "${outputDir}/cts.ttl", destructive)
    }

    /** Cycles through all tabular files in a directory,
    * first turtleizing each file.  If destructive is true, it
    * then deletes the source file.
    * Output is written ttlFileName.
    * @param outputDir Directory containing tabular format files with names
    * ending in 'txt'.  Must be a readable directory.
    * @param ttlFileName Name of output file.
    * @param destructive True if tab files should be deleted
    * after turtelizing.
    */ 
    void turtleizeTabs(File outputDir, String ttlFileName, boolean destructive) {
        File  ttl = new File(ttlFileName)
        turtleizeTabs(outputDir, ttl, destructive)
    }


    /** Cycles through all tabular files in a directory,
    * first turtleizing each file.  If destructive is true, it
    * then deletes the source file.
    * Output is written to a file named "cts.ttl" in the same directory.
    * @param outputDir Directory containing tabular format files with names
    * ending in 'txt'.  Must be a writable directory.
    * @param ttl Output file.
    * @param destructive True if tab files should be deleted
    * after turtelizing.
    */ 
    void turtleizeTabs(File outputDir, File ttl, boolean destructive) {

        CtsTtl turtler = new CtsTtl(this.inventory)
        Integer fileCount = 0
        outputDir.eachFileMatch(~/.*.txt/) { tab ->  
            fileCount++;
            if (fileCount == 1) {
                ttl.append(turtler.turtleizeTabs(tab, false), charEnc)
            } else {
                ttl.append(turtler.turtleizeTabs(tab, false), charEnc)
            }
            if (destructive) { 
	      if (debug > 0) { System.err.println "Corpus: deleting file ${tab}" }
	      tab.delete() 
	    }
        }
    }


    /** Writes a RDF TTL representation of the entire CTS repository
    * to a file. First generates TTL for the repository's TextInventory,
    * then tabulates all files in the repository, and turtleizes
    * the resulting tab files. 
    * @param ttlFile Writable output file.
    * @param tabDir Writable directory for generated tab files.
    */
    void ttl(File outputFile, File tabDir) {
        ttl(outputFile, false, tabDir)
    }

    /** Writes a RDF TTL representation of the entire CTS repository
    * to a file. First generates TTL for the repository's TextInventory,
    * then tabulates all files in the repository, and turtleizes
    * the resulting tab files.  All output is written to outputFile.
    * @param ttlFile Writable output file.
    * @param includePrefix Whether or not to including prefix statements
    * in the output RDF.
    * @param tabDir Writable directory for generated tab files.
    */
    void ttl(File ttlFile, boolean includePrefix, File tabDir) {
        if (debug > 0) {
            System.err.println "Ttl'ing to ${ttlFile} after tabbing to ${tabDir}"
        }
        if (includePrefix) {
            ttlFile.append(prefix, charEnc)
        }

        CtsTtl turtler = new CtsTtl(this.inventory)        
        ttlFile.append( turtler.turtleizeInv(), charEnc)

        tabulateRepository(tabDir)
        turtleizeTabs(tabDir, ttlFile, false)
    }  





  void generateTokenEditionForUrn(String urnStr, File workDir) 
  throws Exception {
    CtsUrn urn
    try {
      urn = new CtsUrn(urnStr)
    } catch (Exception e) {
      System.err.println "Corpus: could not make URN from ${urnStr}."
      throw e
    }
    generateTokenEditionForUrn(urn, workDir)
  }

  void generateTokenEditionForUrn(CtsUrn urn, File workDir) 
  throws Exception {
    workDir.deleteDir()
    workDir.mkdir()


    // Tabulate the src edition identified by urn
    File srcFile =     new File(this.baseDirectory, this.inventory.onlineDocname(urn))
    tabulateFile(srcFile, urn, workDir)


    // generate edition using appropriate system
    String className =    tokenEditionSystemForUrn(urn)
    TokenEditionGenerator editionGenerator = Class.forName(className).newInstance()

    File bigTab = new File(workDir, "alltabs.txt")
    // CAT TOGETHER WHOLE FILE SET:
    outputDir.eachFileMatch(~/.*.txt/) { tab ->  
      bigTab.append(tab.getText(charEnc))
      tab.delete()
    }
    editionGenerator.generate(bigTab,  this.separatorString, workDir)
    bigTab.delete()

    // editionGenerator.tokenIndexName
    // editionGenerator.tokenEditionName


    // then ttl it
    


    // then write some frags of TI


    
  }





  /** Selects a tokenization edition generator based on the language
   * of a document identified by URN.
   * @param urn URN to map to a tokenization system.
   * @returns A String with the full name of a class
   * implementing the tokenization interface.
   * @throws Exception if cannot determine work level of urn.
   */  
  String tokenEditionSystemForUrn(CtsUrn urn) 
  throws Exception {
    String langCode

    switch(urn.getWorkLevel()) {

    case CtsUrn.WorkLevel.WORK:
    langCode  =  this.inventory.languageForWork(urn)
    break

    case CtsUrn.WorkLevel.VERSION:
    langCode  =  this.inventory.languageForVersion(urn)
    break

    default:
    throw new Exception("Corpus, tokenSystemForUrn:  could not determine work level of URN ${urn}")
    break
    }

    if (this.languageToTokenEditionMap.keySet().contains(langCode)) {
      return this.languageToTokenEditionMap[langCode]
    } else {
      return "edu.holycross.shot.hocuspocus.LiteralTokenEditionGenerator"
    }
  }





  /** Selects a tokenization system based on the language
   * of a document identified by URN.
   * @param urn URN to map to a tokenization system.
   * @returns A String with the full name of a class
   * implementing the tokenization interface.
   * @throws Exception if cannot determine work level of urn.
   */
  String tokenSystemForUrn(CtsUrn urn) {
    String langCode

    switch(urn.getWorkLevel()) {

    case CtsUrn.WorkLevel.WORK:
    langCode  =  this.inventory.languageForWork(urn)
    break

    case CtsUrn.WorkLevel.VERSION:
    langCode  =  this.inventory.languageForVersion(urn)
    break

    default:
    throw new Exception("Corpus, tokenSystemForUrn:  could not determine work level of URN ${urn}")
    break
    }

    if (this.languageToTokenSystemMap.keySet().contains(langCode)) {
      return this.languageToTokenSystemMap[langCode]
    } else {
      return "edu.holycross.shot.hocuspocus.DefaultTokenizationSystem"
    }
  }



  void tokenizeRepository(File outputDir) {
    // check  on match ... perhaps proper logic is
    // to process all "online" files in inventory, and
    // ignore other XML files in archive.
    tabulateRepository(outputDir)

    File tokensFile = new File(outputDir, defaultTokensFile)
    outputDir.eachFileMatch(~/.*.txt/) { tab ->  
      def linesArray = tab.readLines()
      String line2 =  linesArray[1]
      def cols = line2.split(/#/)
      CtsUrn u = new CtsUrn(cols[0])

      String className = tokenSystemForUrn(u)
      TokenizationSystem tokenSystem = Class.forName(className).newInstance()
      def tokenData = tokenSystem.tokenize(tab, this.separatorString)

      tokenData.each {  pair ->
	tokensFile.append( "${pair[0]}\t${pair[1]}\n", charEnc)
      }

    }
  }


  /** First tabulates the entire repository, then uses the resulting
   * tabulated files to tokenize the inventory using the specified
   * Tokenization system, and writes resulting RDF TTL in outputDir.
   * @param tokenSystem TokenizationSystem to apply to the inventory.
   * @param outputDir A writable directory where the output will be created.
   */
  void tokenizeRepository(TokenizationSystem tokenSystem, File outputDir) {
        tokenizeRepository(tokenSystem, outputDir, this.separatorString)

        File tokensFile = new File(outputDir, defaultTokensFile)
        outputDir.eachFileMatch(~/.*.txt/) { tab ->  
	  def tokenData = tokenSystem.tokenize(tab, this.separatorString)
	  if (debug > 0) {
	    System.err.println "Corpus: deleting file ${tab}."
	    Integer lineCount = tokensFile.readLines().size()
	    System.err.println "Tokens file onw ${lineCount} lines."
	  }
	  tab.delete()
        }
    }

    /** First tabulates the entire inventory, then uses the resulting
    * tabulated files to tokenize the inventory using the specified
    * Tokenization system, and writes resulting RDF TTL in outputDir.
    * Tabulated files are deleted after tokenization.
    * @param tokenSystem TokenizationSystem to apply to the inventory.
    * @param outputDir A writable directory where the output will be created.
    * @param dividerString String value used to separator fields of 
    * tabulated files.
    */
    void tokenizeRepository(TokenizationSystem tokenSystem, File outputDir, String dividerString) {
        File tokensFile = new File(outputDir, defaultTokensFile)
        tabulateRepository(outputDir)
        outputDir.eachFileMatch(~/.*.txt/) { tab ->  
	  
	  def tokenData = tokenSystem.tokenize(tab, dividerString)

	  tokenData.each {  pair ->
	    tokensFile.append( "${pair[0]}\t${pair[1]}\n", charEnc)
	  }
	  //tab.delete()
        }
    }


    /** Validates the XML serialization of the corpus's TextInventory 
    * against the published schema for a CITE TextInventory.
    * @throws Exception if the XML does not validate.
    */
    void validateInventory() 
    throws Exception {
        // as an alternative, allow a local copy of schmea ...
        URL TextInvSchema = new URL("http://www.homermultitext.org/hmtschemas/TextInventory.rng")
//        URL TextInvSchema = new URL("file://./testdata/TextInventory.rng")
        System.setProperty("javax.xml.validation.SchemaFactory:"+XMLConstants.RELAXNG_NS_URI,
    "com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory");

        def factory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI)
        def schema = factory.newSchema(TextInvSchema)
        def validator = schema.newValidator()
        try {
            validator.validate(inventoryXml)
        } catch (Exception e) {
            throw e
        }
    }

    /** Determines if the set of online documents in the corpus'
    * TextInventory has a one-to-one correspondence with the set of
    * .xml files in the corpus' file storage.
    * @returns True if the two sets are equal, otherwise false.
    */
    boolean filesAndInventoryMatch() {
        def invList = []
        try {
            this.inventory.allOnline().each { urn ->
                invList.add(this.inventory.onlineDocname(urn))
            }
            def invSet = invList as Set
            def fileSet = this.filesInArchive() as Set
            return (invSet == fileSet)
        } catch (Exception e) {
            throw new Exception("")
        }
    }

    /** Creates a list of all files categorized as "online" in the
    * corpus TextInventory, but not appearing as a file in the file system.
    * @returns A (possibly empty) list of file names.
    */
    ArrayList inventoriedMissingFromArchive() {
        def missing = []
        def fileSet = this.filesInArchive()
        def invList = []
        this.inventory.allOnline().each { urn ->
            invList.add(this.inventory.onlineDocname(urn))
        }

        invList.each { urn ->
            if (urn in fileSet) {
            } else {
                missing.add(urn)
            }
        }
        return missing
    }


    /** Creates a list of all .xml files in the archive lacking a 
    * corresponding "online" entry in the corpus TextInventory.
    * @returns A (possibly empty) list of file names.
    */
    ArrayList filesMissingFromInventory() {
        def missing = []
        def invList = []
        this.inventory.allOnline().each { urn ->
            invList.add(this.inventory.onlineDocname(urn))
        }

        this.filesInArchive().each { urn ->
            if (urn in invList) {
            } else {
                missing.add(urn)
            }
        }
        return missing
    }

    /** Creates a list of file paths relative to the archive root
    * for documents marked in the corpus text inventory as online.
    */
    ArrayList filesInInventory() {
        def onlineList = []
        this.inventory.allOnline().each { urn ->
            onlineList.add(this.inventory.onlineDocname(urn))
        }
        return onlineList
    }

    /** Creates a list of CTS URNs for documents marked in the 
    * corpus text inventory as online.
    */
    ArrayList urnsInInventory() {
        def onlineList = []
        this.inventory.allOnline().each {
            onlineList.add(it)
        }
        return onlineList
    }

    /**  Recursively walks through the file system where archival
    * files are kepts, and finds all finds with names ending in '.xml'.
    * @returns A list of file names, with paths relative to the 
    * base directory of this corpus' file storage.
    */
    ArrayList filesInArchive() {
        def fileList = []
        def relativeBase = baseDirectory.toString()
        
        baseDirectory.eachFileMatch(~/.*.xml/) { file ->  
            def stripped = file.toString().replaceFirst(relativeBase,'')
            if (stripped[0] == '/') {
                stripped = stripped.replaceFirst('/','')
            }
            fileList.add(stripped)
        }

        baseDirectory.eachDirRecurse() { d ->  
            d.eachFileMatch(~/.*.xml/) { file ->  
                def stripped = file.toString().replaceFirst(relativeBase,'')
                if (stripped[0] == '/') {
                    stripped = stripped.replaceFirst('/','')
                }
                fileList.add(stripped)
            }  
        }  
        return fileList
    }
}

