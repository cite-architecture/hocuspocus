package edu.holycross.shot.hocuspocus

class CitationConfigurationFileReader{


  /** XML namespace for the CitationConfiguration vocabulary.    */
  static groovy.xml.Namespace hp = new groovy.xml.Namespace("http://chs.harvard.edu/xmlns/hocuspocus")

  groovy.util.Node root

      // responsible for:
    /*
      1. map of URNs to citation models
      2. map of URNs to XML namespaces since that's what we're actually working with
      3. file name since that's what we're actually working with
    */

  /** Map of Cts Urns to corresponding CitationModel object.
   * The map should include an entry for every citable work in
   * the inventory.
   */
  LinkedHashMap citationModelMap = [:]

  /** Map of Cts Urns to XML namespace mappings.
   */
  def xmlNamespaceData = [:]

  /** Map of Cts Urns to names of local files. */
  String fileNameMap = [:]


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
