package edu.holycross.shot.hocuspocus


/** Class modelling all the information in a citation
* configuration file.
*/
class CitationConfigurationFileReader {

  /** XML namespace for the CitationConfiguration vocabulary.    */
  static groovy.xml.Namespace hp = new groovy.xml.Namespace("http://chs.harvard.edu/xmlns/hocuspocus")


  /** Parsed root of a citation configure file. */
  groovy.util.Node root

  /** Map of Cts Urns to corresponding CitationModel object.
   * The map should include an entry for every citable work in
   * the inventory.
   */
  LinkedHashMap citationModelMap = [:]

  /** Map of Cts Urns to XML namespace mappings.
   */
  def xmlNamespaceData = [:]

  /** Map of Cts Urns to names of local files. */
  def fileNameMap = [:]

  /** Map of OnlineSettings objects with configuration info */
  def onlineMap = [:]



  /** Constructor with one parameter for configuration file.
  *  @param confFile XML configuration file validating against
  * the CitationConfiguration RNG schema.
  */
  CitationConfigurationFileReader(File confFile)
  throws Exception {
    root = new XmlParser().parseText(confFile.getText("UTF-8"))
    xmlNamespaceData  = collectXmlNamespaceData(root)
    fileNameMap = collectFileNames(root)
    citationModelMap = collectCitationModels(root)
    onlineMap = collectOnlineSettings(root)
  }


  /** Finds name of file in local file system for a
   * text identified by URN.
   * @param urnValue URN value, as a String, of text to find.
   * @returns Name of local file, or null if no match.
   */
  String getFileNameForUrn(String urnValue) {
    String fName = null
    fileNameMap.keySet().each { k ->
      if (k == urnValue) {
	fName = fileNameMap[k]
      }
    }
    return fName
  }
  /** Alternate method name for backward compatibility.
  */
  String onlineDocname(String urn) {
      return getFileNameForUrn(urn)
  }

  /*
82XF and 2cols have a nodeformat attr:

<online urn="urn:cts:aflibre:af.ah.hc82XF:"  type="82XF" docname="achterhuis_82XF.txt" nodeformat="text">

Markdown is simple:
    <online urn="urn:cts:citedemo:easycts.intro.md" type="markdown" docname="intro.md"/>

XML is hairy

 <online
        urn="urn:cts:greekLit:tlg0012.tlg001.test2:"
        type="xml"
        docname="B_Iliad_test2.xml">

        <namespaceMapping
            abbreviation="tei"
            nsURI="http://www.tei-c.org/ns/1.0"/>
        <citationMapping>
            <citation
                label="book"
                scope="/tei:TEI/tei:text/tei:body"
                xpath="/tei:div[@n = '?']">
                <citation
                    label="line"
                    scope="/tei:TEI/tei:text/tei:body/tei:div[@n = '?']"
                    xpath="/tei:l[@n = '?']"/>
            </citation>
        </citationMapping>
    </online>

  */



  /** Compiles a list of version-level URNs for all
   * texts listed as online in the configuration file.
   */
  ArrayList allOnline() {
    return fileNameMap.keySet()
  }

  /** Creates a map of URNs to XML namespace info by
  * reading a parsed XML configuration.  The XML namespace
  * info is a map of abbreviations to full URIs.
  * @param confRoot Root node of parsed document.
  * @returns A map of CTS URNs to XML namespace mappings.
  */
  static LinkedHashMap collectXmlNamespaceData(groovy.util.Node confRoot) {

    def nsMapList = [:]
    confRoot[hp.online].each { doc ->
      def nsMapsForDoc = [:]
      String urn = doc.'@urn'
      doc[hp.namespaceMapping].each { ns ->
        String abbr = ns.'@abbreviation'
        String uri = ns.'@nsURI'
        nsMapsForDoc[abbr] = uri
      }
      nsMapList[urn] = nsMapsForDoc
    }

    return nsMapList
  }

  /** Creates a map of URNs to local file names by reading
  * a parsed XML configuration.
  * @param confRoot Root node of parsed document.
  * @returns A map of CTS URNs to file names.
  */
  static LinkedHashMap collectFileNames(groovy.util.Node confRoot) {
    def fileNameMap = [:]
    confRoot[hp.online].each { conf ->
      fileNameMap[conf.'@urn'] = conf.'@docname'
    }
    return fileNameMap
  }



  static LinkedHashMap collectCitationModels(groovy.util.Node confRoot) {
    def citationData = [:]
    confRoot[hp.online].each { conf ->
      citationData[conf.'@urn'] = new CitationModel(conf)
    }
    return citationData
  }





  static LinkedHashMap collectOnlineSettings(groovy.util.Node confRoot) {
    def onlineMap = [:]
    confRoot[hp.online].each { ol ->
      OnlineSettings settings
      DocumentFormat df = DocumentFormat.getByLabel(ol.'@type') 
      if (ol.'@nodeformat') {
	NodeFormat nf = NodeFormat.getByLabel(ol.'@nodeformat')
	settings = new OnlineSettings(ol.'@docname', df, nf)
      } else {
	settings = new OnlineSettings(ol.'@docname', df)
      }
      onlineMap[ol.'@urn'] = settings
    }
    return onlineMap
  }

  
  /**
   * Finds a CitationModel object for a work identified by URN.
   * @param The URN the work for which to find the CitationModel.
   * @returns A CitationModel derived from the online node corresponding
   * to this URN, or null if no match found.
   */
  /*
    CitationModel getCitationModel(CtsUrn urn)  {
    return getCitationModel(urn.toString())
    }
  */

    /**
    * Finds a CitationModel object for a work identified by URN.
    * @param The URN the work for which to find the CitationModel.
    * @returns A CitationModel derived from the online node corresponding
    * to this URN, or null if no match found.
    */
  /*
    CitationModel getCitationModel(CtsUrn urn)  {
        return getCitationModel(urn.toString())
    }
  */
  /**
   * Finds a CitationModel object for a work identified by URN, as a String.
   * @param The URN, as a String, of the work for which to find
   * the CitationModel.
   * @returns A CitationModel derived from the online node corresponding
   * to this URN, or null if no match found.
   */
  CitationModel getCitationModel(String urnStr)
  throws Exception {
    if (! this.citationModelMap[urnStr]) {
      throw new Exception("CitationConfigurationFileReader: no citaiton model for URN value " + urnStr)
    }
    return this.citationModelMap[urnStr]
  }

  Object getXmlNsData(String urnStr)
  throws Exception {
    if (! this.xmlNamespaceData[urnStr]) {
      throw new Exception("CitationConfigurationFileReader: no xml namespace data for URN value " + urnStr)
    }
    return this.xmlNamespaceData[urnStr]
  }

}
