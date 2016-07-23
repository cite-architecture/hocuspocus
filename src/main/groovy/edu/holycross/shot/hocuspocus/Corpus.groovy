package edu.holycross.shot.hocuspocus


import edu.harvard.chs.cite.CtsUrn

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import org.apache.commons.io.FilenameUtils

/**
 * Class for working with a repository of citable texts stored
 * in a local file system, and documented with a text inventory
 * and a text configuration file.
 *
 */
class Corpus {

  public Integer debug = 0

  /** Character encoding to use for all file output, initialized to
   * default value of UTF-8.    */
  String charEnc = "UTF-8"

  /** TextInventory with entries for all documents in the corpus.
   */
  TextInventory  inventory

  /** Root directory of file system containing archival files.
   */
  File baseDirectory

  /** Citation configuration information. */
  CitationConfigurationFileReader citationConfig

  /** CtsTtl object used to generate RDF statements. */
  CtsTtl ttler

  /** String value defining columns in tabular text format.
   */
  String separatorString = "#"


  String ttlFileName

  /** Constructs a corpus from an archive stored
   * in a local file system, and validates TextInventory
   * contents against a given schema.
   * @param invFile File containing a TextInventory document.
   * @param configFile Citation configuration for the archive.
   * @param baseDir Root directory of editions.
   * @param schemaFile File with RNG Schema for a TextInventory.
   */
  Corpus(File invFile, File configFile, File baseDir, File schemaFile) throws Exception {
    try{
      this.inventory = new TextInventory(invFile)
      this.citationConfig = new CitationConfigurationFileReader(configFile)
      this.ttler = new CtsTtl(inventory, citationConfig)
    } catch (Exception e) {
      throw e
    }

    if (!baseDir.canRead()) {
      throw new Exception("Corpus: cannot read directory ${baseDir}")
    }
    this.baseDirectory = baseDir

    try {
      validateInventory(schemaFile)

    } catch (Exception invException) {
      throw invException
    }

    this.ttlFileName = "cts.ttl"
  }



  /** Constructs a corpus from an archive stored
   * in a local file system.
   * @param invFile File containing a TextInventory document.
   * @param configFile Citation configuration for the archive.
   * @param baseDir Root directory of editions.
   */
  Corpus(File invFile, File configFile, File baseDir) throws Exception {
    try {
      this.inventory = new TextInventory(invFile)
      this.citationConfig = new CitationConfigurationFileReader(configFile)
    } catch (Exception e) {
      throw e
    }

    if (!baseDir.canRead()) {
      throw new Exception("Corpus: cannot read directory ${baseDir}")
    }
    this.baseDirectory = baseDir
    this.ttlFileName = "cts.ttl"

  }


  /** Creates TTL representation of the entire corpus
   *  without TTL prologue and writes it to ttlFileName
   * in outputDir.
   * @param outputDir A writable directory where the TTL file
   * will be written.
   * @throws Exception if unable to write to outputDir.
   */
  void turtleizeRepository(File outputDir) throws Exception {
    turtleizeRepository(outputDir, false)
  }

  /** Creates TTL representation of the entire corpus
   * and writes it to ttlFileName  in outputDir.
   * @param outputDir A writable directory where the TTL file
   * will be written.
   * @param includePrologue True if the file should include ttl prologue.
   * @throws Exception if unable to write to outputDir.
   */
  void turtleizeRepository(File outputDir, Boolean includePrologue) throws Exception {
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
    if (debug > 0) {
      System.err.println "Make file for ${ttlFileName} in ${outputDir}"
    }
    File ttlFile = new File(outputDir, ttlFileName)
    ttlFile.setText("","UTF-8")
    ttlFile.append(this.ttler.turtleizeInv(inventory, citationConfig, includePrologue))
    tabDir.eachFileMatch(~/.*.txt/) { file ->
      ttlFile.append(this.ttler.turtleizeFile(file,false))
    }
  }



  /** Creates a XmlTabulator Object and uses it to tabulate
   * a given document.
   * @param f The source file in the archive to tabulate.
   * @param urn The URN for the file to tabulate.
   * @param outputDir A writeable directory for output files.
   */
  /*
    void tabulateFile(File f, CtsUrn urn, File outputDir) {
    XmlTabulator tab = new XmlTabulator()
    tab.tabulate(urn, inventory, f, outputDir)
    }
  */


  /** Creates a tabular representation of every document
   * in the corpus.
   * @param ouputDir A writeable directory where tabulated files
   * will be written.  Output files are named "tab[N].txt".
   */
  void tabulateRepository(File outputDir) {
    citationConfig.fileNameMap.keySet().eachWithIndex { urnVal, idx ->
      CtsUrn urn  = new CtsUrn(urnVal)
      File f = new File(baseDirectory, citationConfig.fileNameMap[urnVal])
      File tabFile = new File(outputDir, "tab${idx}.txt")
      if (debug > 0) {
	System.err.println "Tabulate " + urnVal + " from " + f + " to " + tabFile
      }

      
      

      // CtsTtl only knows about internal 8-column format.
      // Way to generate that depends on source document format.
      OnlineSettings settings = citationConfig.onlineMap[urnVal]
      switch (settings.docFormat) {
	
      case DocumentFormat.XML:
      XmlTabulator tabulator = new XmlTabulator()
      String tabData = tabulator.tabulateFile(urn, inventory, citationConfig, f)
      tabFile.setText(tabData,"UTF-8")
      break

      case DocumentFormat.MARKDOWN:
      MdTabulator tabulator = new MdTabulator()
      String tabData = tabulator.mdFileToTabular(f, urnVal)
      tabFile.setText(tabData,"UTF-8")
      break

      case DocumentFormat.O2XF:
      TablesUtil tu = new TablesUtil()
      String tabData = tu.o2xfToEight(f.getText())
      tabFile.setText(tabData,"UTF-8")

      break
      
      case DocumentFormat.TWO_COL:
      TablesUtil tu = new TablesUtil()
      String o2xf = twoTo82XF(f)
      String tabData = tu.o2xfToEight(f)
      tabFile.setText(tabData,"UTF-8")
      break
      }


    }
  }

  /** Validates the XML serialization of the corpus's TextInventory
   * against the an RNG schema for a CITE TextInventory.
   * @throws Exception if the XML does not validate.
   */
  void validateInventory(File textInvSchema)
  throws Exception {
    //URL textInvSchema = new URL("http://www.homermultitext.org/hmtschemas/TextInventory.rng")
    //File textInvSchema = new File(schemaFileName)

    System.setProperty("javax.xml.validation.SchemaFactory:"+XMLConstants.RELAXNG_NS_URI,
		       "com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory");

    def factory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI)
    def schema = factory.newSchema(textInvSchema)

    def validator = schema.newValidator()
    try {
      //validator.validate(inventoryXml)
    } catch (Exception e) {
      throw e
    }
  }

  /** Determines if the set of online documents in the corpus'
   * TextInventory has a one-to-one correspondence with the set of
   * files in the corpus' file storage.
   * @returns True if the two sets are equal, otherwise false.
   */
  boolean filesAndInventoryMatch() {
    def invList = []
    try {
      this.citationConfig.allOnline().each { urn ->
	invList.add(this.citationConfig.onlineDocname(urn))
      }
      def invSet = invList as Set
      def fileSet = this.filesInArchive() as Set
      return (invSet == fileSet)
    } catch (Exception e) {
      throw  e //new Exception("")
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
    this.citationConfig.allOnline().each { urn ->
      invList.add(this.citationConfig.onlineDocname(urn))
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
    this.citationConfig.allOnline().each { urn ->
      invList.add(this.citationConfig.onlineDocname(urn))
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
    this.citationConfig.allOnline().each { urn ->
      onlineList.add(this.citationConfig.onlineDocname(urn))
    }
    return onlineList
  }

  /** Creates a list of CTS URNs for documents marked in the
   * corpus text inventory as online.
   */
  ArrayList urnsInInventory() {
    def onlineList = []
    this.citationConfig.allOnline().each {
      onlineList.add(it)
    }
    return onlineList
  }

  ArrayList filesInDir(File dir) {
    def fileList = []
    def relativeBase = dir.toString()
    dir.eachFileMatch(~/.*.xml/) { file ->
      def stripped = file.toString().replaceFirst(relativeBase,'')
      if (stripped[0] == '/') {
	stripped = stripped.replaceFirst('/','')
      }
      fileList.add(stripped)
    }

    dir.eachFileMatch(~/.*.csv/) { file ->
      def stripped = file.toString().replaceFirst(relativeBase,'')
      if (stripped[0] == '/') {
	stripped = stripped.replaceFirst('/','')
      }
      fileList.add(stripped)
    }

    dir.eachFileMatch(~/.*.txt/) { file ->
      def stripped = file.toString().replaceFirst(relativeBase,'')
      if (stripped[0] == '/') {
	stripped = stripped.replaceFirst('/','')
      }
      fileList.add(stripped)
    }

    dir.eachFileMatch(~/.*.md/) { file ->
      def stripped = file.toString().replaceFirst(relativeBase,'')
      if (stripped[0] == '/') {
	stripped = stripped.replaceFirst('/','')
      }
      fileList.add(stripped)
    }

    return fileList
  }

  /**  Recursively walks through the file system where archival
   * files are kepts, and finds all finds with names ending in '.xml',
   * '.txt', '.csv', or 'md'.
   * @returns A list of file names, with paths relative to the
   * base directory of this corpus' file storage.
   */
  ArrayList filesInArchive() {
    def fileList = filesInDir(baseDirectory)
    baseDirectory.eachDirRecurse() { d ->
      fileList += filesInDir(d)
    }
    return fileList
  }

}
