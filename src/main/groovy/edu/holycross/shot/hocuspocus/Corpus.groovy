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

	public Integer debug = 1

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

	/** String value defining columns in tabular text format.
	 */
	String separatorString = "#"


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

	}



	/** Constructs a corpus from an archive stored
	 * in a local file system.
	 * @param invFile File containing a TextInventory document.
	 * @param configFile Citation configuration for the archive.
	 * @param baseDir Root directory of editions.
	 */
	Corpus(File invFile, File configFile, File baseDir) throws Exception {
		try{
			this.inventory = new TextInventory(invFile)
			this.citationConfig = new CitationConfigurationFileReader(configFile)
		} catch (Exception e) {
			throw e
		}

		if (!baseDir.canRead()) {
			throw new Exception("Corpus: cannot read directory ${baseDir}")
		}
		this.baseDirectory = baseDir

	}


	/** Creates TTL representation of the entire corpus
	 * and writes it to a file in outputDir.
	 * @param outputDir A writable directory where the TTL file
	 * will be written.
	 * @throws Exception if unable to write to outputDir.
	 */
	void turtleizeRepository(File outputDir) throws Exception {
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
		/*
		File ttlFile = new File(outputDir, "corpus.ttl")
		this.ttl(ttlFile, tabDir)
		 */
	}



	/** Creates a Tabulator object and uses it to tabulate
	 * a given document.
	 * @param f The source file in the archive to tabulate.
	 * @param urn The URN for the file to tabulate.
	 * @param outputDir A writeable directory for output files.
	 */
	/*
	void tabulateFile(File f, CtsUrn urn, File outputDir) {
	Tabulator tab = new Tabulator()
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
			System.err.println "Tabulate " + urnVal + " from " + f + " to " + tabFile
			Tabulator tabulator = new Tabulator()
			String tabData = tabulator.tabulateFile(urn, inventory, citationConfig, f)
			System.err.println "\n\nFOR " + urn
			System.err.println tabData
			tabFile.setText(tabData,"UTF-8")
		}
	}



	/** Cycles through all tabular files in a directory,
	 * turtleizing each file.
	 * Output is written to a file named "cts.ttl" in the same directory.
	 * @param outputDir Directory containing tabular format files with names
	 * ending in 'txt'.  Must be a writable directory.
	 */
	//  void turtleizeTabs(File outputDir) {
	//    turtleizeTabs(outputDir, false)
	//  }


	/** Cycles through all tabular files in a directory,
	 * first turtleizing each file.  If destructive is true, it
	 * then deletes the source file.
	 * Output is written to a file named "cts.ttl" in the same directory.
	 * @param outputDir Directory containing tabular format files with names
	 * ending in 'txt'.  Must be a writable directory.
	 * @param destructive True if tab files should be deleted
	 * after turtelizing.
	 */
	//void turtleizeTabs(File outputDir, boolean destructive) {
	//turtleizeTabs(outputDir, "${outputDir}/cts.ttl", destructive)
	//}

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
	//void turtleizeTabs(File outputDir, String ttlFileName, boolean destructive) {
	// File  ttl = new File(ttlFileName)
	// turtleizeTabs(outputDir, ttl, destructive)
	//}


	/*
	void turtleizeTabsFast(File outputDir, File ttl, boolean destructive) {
	if (debug > 0) {
	System.err.println "Turtleizing files in ${outputDir}"
	}
	TtlGenerator turtler = new TtlGenerator(this.inventory)
	Integer fileCount = 0
	outputDir.eachFileMatch(~/.*.txt/) { tab ->
	if (debug > 0) { System.err.println "Turtleizing " + tab }
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
	 */

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

	/*
	void turtleizeTabsToFile(File outputDir, File ttl, boolean destructive) {
	if (debug > 0) {
	System.err.println "Turtleizing files in ${outputDir}"
	}

	TtlGenerator turtler = new TtlGenerator(this.inventory)
	outputDir.eachFileMatch(~/.*.txt/) { tab ->
	if (debug > 0) {
	System.err.println "Turtleizing " + tab
	}
	turtler.turtleizeTabsToFile(tab, ttl, charEnc, false)
	if (destructive) {
	if (debug > 0) { System.err.println "Corpus: deleting file ${tab}" }
	tab.delete()
	}
	}
	}
	 */

	/** Writes a RDF TTL representation of the entire CTS repository
	 * to a file. First generates TTL for the repository's TextInventory,
	 * then tabulates all files in the repository, and turtleizes
	 * the resulting tab files.
	 * @param ttlFile Writable output file.
	 * @param tabDir Writable directory for generated tab files.
	 */
	//void ttl(File outputFile, File tabDir) {
	// ttl(outputFile, false, tabDir)
	//}


	/** Writes a RDF TTL representation of the entire CTS repository
	 * to a file. First generates TTL for the repository's TextInventory,
	 * then tabulates all files in the repository, and turtleizes
	 * the resulting tab files.  All output is written to outputFile.
	 * @param ttlFile Writable output file.
	 * @param includePrefix Whether or not to including prefix statements
	 * in the output RDF.
	 * @param tabDir Writable directory for generated tab files.
	 */
	/*  void ttl(File ttlFile, boolean includePrefix, File tabDir) {
		if (debug > 0) {
		System.err.println "Ttl'ing to ${ttlFile} after tabbing to ${tabDir}"
		}

		TtlGenerator turtler = new TtlGenerator(this.inventory)
		ttlFile.append(turtler.turtleizeInv(includePrefix), charEnc)

		tabulateRepository(tabDir)
	//
	turtleizeTabsToFile(tabDir, ttlFile, false)

	}
	 */

	/* * Validates the XML serialization of the corpus's TextInventory
	 * against the published schema for a CITE TextInventory.
	 * @throws Exception if the XML does not validate.
	 */
	/*
	void validateInventory()
	throws Exception {
// as an alternative, allow a local copy of schmea ...
	validateInventory(new File("testdata/schemas/TextInventory.rng"))
	}
	 */

	/** Validates the XML serialization of the corpus's TextInventory
	 * against the an RNG schema for a CITE TextInventory.
	 * @throws Exception if the XML does not validate.
	 */
	//  void validateInventory(File schemaFile)
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
