package edu.holycross.shot.hocuspocus


import edu.harvard.chs.cite.CtsUrn


import groovy.xml.StreamingMarkupBuilder


/**
* Class modelling a TextInventory.
* Methods for finding artifacts in the TextInventory appear in two forms:
* <ul>
* <li>an internal "private" method returning a TextInventory class data structure.
* These methods have 'Data' in their names.</li>
* <li>a public method returning URN values or String values</li>
* </ul>
* The internal 'Data' methods should not be used:  internal data structures may
* change.  The URN and String methods are stable.  They will not change their signature
* and should produce identical results even if the underlying implementation changes.
*/
class TextInventory {


    /** Debugging level. */
    public Integer debug = 0

    /** List of data values that violate the TextInventory definition. */
    def errorList = []

    /** Character encoding to use when representing TextInventory as
    * a File object.
    */
    String enc = "UTF-8"

    /** XML namespace for the TextInventory vocabulary.    */
    static groovy.xml.Namespace ti = new groovy.xml.Namespace("http://chs.harvard.edu/xmlns/cts")

    /** XML namespace for the CTS vocabulary.    */
    static groovy.xml.Namespace ctsns = new groovy.xml.Namespace("http://chs.harvard.edu/xmlns/cts")

    /** The TextInventory ID. In an XML serialization validating against the
    * TextInventory schema,  this is the <code>tiid</code>
    * attribute on the root element.
    */
    //def tiId

    /** The version of the TextInventory schema this inventory validates against.
    */
    def tiVersion

    /** A list of namespace triplets.  Each CTS namespace
    * is comprised of a namespace abbreviation, a full
    * URI, and a description.  These can be read from an
    * XML TextInventory file.
    */
    def ctsnamespaces = []

    /** A list of textgroup structures.
    * Each textgroup structure is comprised of a CTS urn and a label.
    * Although the XML schema for a TextInventory allows multiple
    * labels for a textgroup, this data structure keeps only one.
    *  These can be read from an XML TextInventory file.
    */
    def textgroups = []

    /** A list of work structures.
    * Each work structure is comprised of
    * a CTS URN, a title, and a parent (textgroup) URN.
    * (A full work-level URN can therefore be composed by joining the
    * work-level URN and the work ID).
    * Although the XML schema for a TextInventory allows multiple
    * titles for a work, this data structure keeps only one.
    * These can be read from an XML TextInventory file.
    */
    def works = []


    /**  A list of version-level structures.
    * Each version-level structure is comprised of
    * a CTS URN for the version, a label, a boolean flag indicating
    * whether or not this version is online, and a parent (work) URN.
    * These can be read from an XML TextInventory file.
    */
    def editions = []

    /**  A list of version-level structures.
    * Each version-level structure is comprised of
    * a CTS URN for the version, a label, a boolean flag indicating
    * whether or not this version is online, and a parent (work) URN.
    * These can be read from an XML TextInventory file.
    */
    def translations = []

    /**  A list of exemplar-level structures.
    * Each exemplar-level structure is comprised of
    * a CTS URN for the exemplar, a label, a boolean flag indicating
    * whether or not this version is online, and a parent (version-level) URN.
    * These can be read from an XML TextInventory file.
    */
    def exemplars = []

    /** Map of work-level Cts Urns to ISO three-letter language codes.
    * This map can be read from an XML TextInventory file.
    */
    def worksLanguages = [:]

    /** Map of version-level Cts Urns to ISO three-letter language codes.
    * This map can be read from an XML TextInventory file.
    */
    def translationLanguages = [:]


    /** Generates listing of data validation errors.
    * @returns String with list of validation errors.
    */
    String errorListToText() {
        StringBuffer eList = new StringBuffer("${this.errorList.size()} invalid data values found:\n")
        this.errorList.each {
            eList.append("\t${it}\n")
        }
        return eList.toString()
    }

    /** Constructs a TextInventory from a CTS GetCapabilities request.
    * @param capsUrl URL of the GetCapabilities request.
    * @throws Exception if invalid data values found.
    */
    TextInventory (URL capsUrl)
    throws Exception {
      def capsText = capsUrl.getText("UTF-8")
      groovy.util.Node capsRoot = new XmlParser().parseText(capsText)

      def repl = capsRoot[ctsns.reply][0]
      def tiRoot = repl[ti.TextInventory][0]

      try {
	this.initFromParsedTextInv(tiRoot)
      } catch (Exception e) {
	throw e
      n}
    }


    /** Constructs a TextInventory from a string serialization
    * of an TextInventory as XML.
    * @param str The String of XML text representing the TextInventory.
    * @throws Exception if invalid data values found.
    */
    TextInventory (String str)
    throws Exception {
        try {
	  this.initFromTextInvString(str)
        } catch (Exception e) {
	  throw e
        }
    }

    /** Constructs a TextInventory from a File object.
    * @param f File with XML validating against the XML schema of a TextInventory.
    * @throws Exception if invalid data values found.
    */
    TextInventory (File f)
    throws Exception {
      try {
	this.initFromTextInvString(f.getText(enc))
      } catch (Exception e) {
	throw e
      }
    }


    /** Constructs a TextInventory from the root node
    * of a parsed XML inventory.
    * @param docRoot Node giving the root of the parsed XML.
    */
    TextInventory(groovy.util.Node docRoot) {
        try {
	  this.initFromParsedTextInv(docRoot)
        } catch (Exception e) {
	  System.err.println "There were errors initializing the inventory:"
	  this.errorList.each {
	    System.err.println "\t${it}"
	  }
        }
    }

    /** Constructs an empty TextInventory object.
    */
    TextInventory() {
    }


  
  /** Determines whether a version is an edition or
    * translation.
    * @param urnStr URN, as a String, of the version to examine.
    * @returns A VersionType value.
    * @throws Exception if urnStr is not a valid CTS URN string.
    */
  VersionType typeForVersion(String urnStr)
  throws Exception {
    try {
      def u = new CtsUrn(urnStr)
      return typeForVersion(urnStr)
    } catch (Exception e) {
      throw e
    }
  }


  /** Determines whether a version is an edition or
   * translation.
   * @param urnStr URN, as a String, of the version to examine.
   * @returns A VersionType value.
   */
  VersionType typeForVersion(CtsUrn urn) {
    def edLabel = editionLabel(urn)
    def xlatLabel = translationLabel(urn)
    if (edLabel) {
      if (debug > 1) {
	println "typeForVersion: edLabel ${edLabel}, so return "  + VersionType.EDITION
      }
      return VersionType.EDITION
    } else if (xlatLabel) {
      return VersionType.TRANSLATION
    } else {
      return null
    }
  }


  /** Determines if a URN represents a text in the inventory.
   * @param urn The URN to test.
   * @returns true if the URN is in the inventory.
   */
  boolean urnInInventory(CtsUrn urn) {
    def nsStruct = this.ctsnamespaces.find { it[0] == urn.getCtsNamespace()}

    switch (urn.getWorkLevel()) {

    case CtsUrn.WorkLevel.VERSION:

    switch (typeForVersion(urn)) {
    case VersionType.EDITION:
    CtsUrn noPsg = new CtsUrn(urn.getUrnWithoutPassage())
    String ed = noPsg.reduceToVersion()
    
    def edStruct = this.editions.find {it[0] == ed}
    String tg = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}:"
    def tgStruct = this.textgroups.find {it[0] == tg}
    String wk = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}:"
    def wkStruct = this.works.find {it[0] == wk }
    return ((nsStruct != null) && (tgStruct != null) && (wkStruct != null))
    break


    case VersionType.TRANSLATION:
    String trans = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}.${urn.getVersion()}:"
    def transStruct = this.translations.find {it[0] == trans}
    String tg = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}:"
    def tgStruct = this.textgroups.find {it[0] == tg}
    String wk = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}:"
    def wkStruct = this.works.find {it[0] == wk }
    return ((nsStruct != null) && (tgStruct != null) && (wkStruct != null))
    break

    default:
    if (debug > 1) {
      System.err.println "TextInventory:urnInInventory: for urn ${urn}, type is " + typeForVersion(urn)
    }
    break
    }

    case CtsUrn.WorkLevel.WORK:
    String tg = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}:"
    def tgStruct = this.textgroups.find {it[0] == tg}
    String wk = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}:"
    def wkStruct = this.works.find {it[0] == wk }
    boolean answer = ((nsStruct != null) && (tgStruct != null) && (wkStruct != null))
    return answer
    
    break

    case CtsUrn.WorkLevel.GROUP:
    String tg = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}:"
    def tgStruct = this.textgroups.find {it[0] == tg}
    return ((nsStruct != null) && (tgStruct != null))
    break
    
    default:
    System.err.println "Level " + urn.getWorkLevel()
    return true
    break
    }
    return false
  }



    /** Finds ISO language code for a specified version of a work.
    * @param urnStr URN value of the work in question.
    * @returns ISO language code.
    * @throws Exception if urnStr is not a valid CTS URN value.
    */
    String languageForVersion(String urnStr)
    throws Exception {
        try {
            CtsUrn urn = new CtsUrn(urnStr)
            return languageForVersion(urn)

        } catch (Exception e) {
            throw e
        }
    }



    /** Finds ISO language code for a specified version of a work.
    * @param urnStr URN value of the work in question.
    * @returns ISO language code.
    */
    String languageForVersion(CtsUrn urn) {
        if (debug > 0)  {
            System.err.println "TextInventory:languageForVersion: ${urn} at work level " + urn.getWorkLevel()
        }

        switch (urn.getWorkLevel()) {
            case CtsUrn.WorkLevel.WORK :
                return languageForWork(urn)
            break

            case CtsUrn.WorkLevel.VERSION :

	    if (debug > 0)  {
	      System.err.println "${urn} at version level  is type" + typeForVersion(urn)
            }

	    switch (typeForVersion(urn)) {

	    case VersionType.TRANSLATION:
	    return translationLanguages[urn.toString()]
	    break

	    case VersionType.EDITION:
	    return languageForWork(urn)
	    break

	    default:
	    //
	    break
            }
            break

            default :
	    //
	    break
        }
    }





    /** Finds the language code for a notional work.
    * @param urnStr A CtsUrn string identifying the work.
    * @returns A 3-letter language code, or null if no
    * work was found for the requested urn.
    * @throws Exception if urnStr is not a valid CTS URN string.
    */
    String languageForWork(String urnStr)
    throws Exception {
        try {
            CtsUrn urn = new CtsUrn(urnStr)
            String workStr = "urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}:"
            return worksLanguages[workStr]

        } catch (Exception e) {
            throw e
        }
    }

    /** Finds the language code for a notional work.
    * @param urn CtsUrn identifying the work.
    * @returns A 3-letter language code, or null if no
    * work was found for the requested urn.
    */
    String languageForWork(CtsUrn urn) {
        return worksLanguages["urn:cts:${urn.getCtsNamespace()}:${urn.getTextGroup()}.${urn.getWork()}:"]
    }

    /** Finds value of online element's docname attribute.
    * @param urnStr The URN, as a String, of the document to look up.
    * @returns The docname attribute of the online element, or null
    * if none found.
    * @throws Exception if urnStr is not a valid CtsUrn.
    */

    /// MOVE TO CITATION CONFIG
  /*
    String onlineDocname(String urnStr) {
        CtsUrn urn = new CtsUrn(urnStr)
        return onlineDocname(urn)
    }
  */


    /** Finds value of online element's docname attribute.
    * @param urn The URN of the document to look up.
    * @returns The docname attribute of the online element, or null
    * if none found.
    * @throws Exception if urnStr is not a valid CtsUrn.
    */
    /// MOVE TO CITATION CONFIG
  /*
    String onlineDocname(CtsUrn urn) {
        return onlineMap[urn.toString()]
    }
  */




    /** Finds work-level URNs for all works
     * available online for the textgroup of a given CTS URN.
    * @param u A CTS URN to check for.
    * @returns A (possibly empty) List of work-level data structures.
    */
    def worksForGroup(CtsUrn u) {
        def workUrns =  []
        worksDataForGroup("urn:cts:${u.getCtsNamespace()}:${u.getTextGroup()}:").each {
            workUrns.add(it[0])
        }
        return workUrns
    }


    /** Finds work-level URNs for all works
     * available online for the textgroup of a given CTS URN.
    * @param u A CTS URN to check for.
    * @returns A (possibly empty) List of work-level data structures.
    * @throws Exception if s is not a valid CtsUrn String
    */
    def worksForGroup(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return worksForGroup(u)
        } catch (Exception e) {
            throw e
        }
    }

    /** Gets the full URI of the CTS Namespace identified by
    * @param label Unique label of the CTS Namespace.
    * @returns The URI for the CTS Namespace, or null if none
    * found.
    * @throws Exception if more than one CTS Namespace has the
    * requested label.
    */
    String getNamespaceUri(String label) {
        def ctsNsMatches = this.ctsnamespaces.findAll {it[0] == label}
        switch (ctsNsMatches.size()) {
            case 0:
                return null
            break

            case 1:
                def ctsNs = ctsNsMatches[0]
                return ctsNs[1]
                break

                default :
                    throw new Exception("CTS Namespaces misconfigured: label ${label}is not unique.")
                break
            }
    }


    /**  Finds version-level URNs for all online versions
    * belonging to the TextGroup of the requested CtsUrn.
    * @param u CtsUrn identifying the TextGroup to search for.
    * @returns A (possibly empty) List of CtsUrn strings.
    */
    /// MOVE TO CITATION CONFIG
  /*
    def onlineForGroup(CtsUrn u) {
        def versionList = []
        onlineDataForGroup(u).each { vers ->
            versionList.add(vers[0])
        }
        return versionList
    }

  */
    /**  Finds version-level URNs for all online versions
    * belonging to the TextGroup of the requested CtsUrn.
    * @param u CtsUrn identifying the TextGroup to search for.
    * @returns A (possibly empty) List of CtsUrn strings.
    * @throws Exception if s is not a valid CtsUrn String
    */
      /// MOVE TO CITATION CONFIG
  /*
    def onlineForGroup(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return onlineForGroup(new CtsUrn(s))
        } catch (Exception e) {
            throw e
        }
    }
  */

    /** Finds version-level URNs for all versions
     * available online for a given CTS URN.
    * @param u A CTS URN to check for.
    * This URN may include either a work- or version-level reference to the work:
    * it is trimmed to the work level before searching.  If u is given at the
    * text group level, it is not an error, but the returned list will be empty.
    * @returns A possibly empty List of Strings giving CtsUrns at the version
    * level.
    * @throws Exception if s is not a valid CtsUrn String
    */
      /// MOVE TO CITATION CONFIG
  /*
    def onlineForWork(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return onlineForWork(u)
        } catch (Exception e) {
            throw e
        }
    }
  */


    /** Finds version-level URNs for all versions
     * available online for a given CTS URN.
    * @param u A CTS URN to check for.
    * This URN may include either a work- or version-level reference to the work:     * it is trimmed to the work level before searching.  If u is given at the
    * text group level, it is not an error, but the returned list will be empty.
    * @returns A possibly empty List of Strings giving CtsUrns at the version
    * level.
    */
      /// MOVE TO CITATION CONFIG
  /*
    def onlineForWork(CtsUrn u) {
        def urns = []
        onlineDataForWork(u).each {
            urns.add(it[0])
        }
        return urns
    }
  */


    /** Finds version-level URNs known to this inventory for a
    * requested CTS URN.
    * @param u The CTS URN to search for.  This URN may include either a work-
    * or version-level reference to the work: it is trimmed to the work level
    * before searching.  If u is given at the text group level, it is not an
    * error, but the returned list will be empty.
    * @returns A possibly empty List of Strings giving CtsUrns at the version
    * level.
    **/
    def versionsForWork(CtsUrn u) {
        def wk = "urn:cts:${u.getCtsNamespace()}:${u.getTextGroup(false)}.${u.getWork(false)}:"
		CtsUrn testIt = new CtsUrn(wk)
        def edlist = editions.findAll {
            it[3] == wk.toString()
        }

        def edUrls = []
        edlist.each { ed ->
            edUrls.add(ed[0])
        }


        def translist = translations.findAll {
            it[3] == wk.toString()
        }

        def transUrls = []
        translist.each { tr ->
            transUrls.add(tr[0])
        }

        return edUrls + transUrls
    }

    /** Finds exemplar-level URNs known to this inventory for a
    * requested CTS URN.
    * @param u The CTS URN to search for.  This URN may include either a version-
    * or exemplar-level reference to the work: it is trimmed to the work level
    * before searching.  If u is given at the text group or work level, it is not an
    * error, but the returned list will be empty.
    * @returns A possibly empty List of Strings giving CtsUrns at the version
    * level.
    **/
    def exemplarsForVersion(CtsUrn u) {
        def v = "urn:cts:${u.getCtsNamespace()}:${u.getTextGroup(false)}.${u.getWork(false)}.${u.getVersion(false)}:"
		CtsUrn testIt = new CtsUrn(v)
        def exlist = exemplars.findAll {
            it[3] == v.toString()
        }

        def exUrls = []
        exlist.each { ex ->
            exUrls.add(ex[0])
        }

        return exUrls
    }



    /** Finds exemplar-level URNs known to this inventory for a
    * requested CTS URN.
    * @param s A String representing the CTS URN to search for.
    * This URN may include either a version-level or exemplar-level reference to the work:     * it is trimmed to the version level before searching.  If u is given at the
    * text group or work level, it is not an error, but the returned list will be empty.
    * @returns A possibly empty List of Strings giving CtsUrns at the version
    * level.
    * @throws Exception if s is not a valid CtsUrn String
    */
    def exemplarsForVersion(String s) {
        def urn
        try {
            urn = new CtsUrn(s)
            exemplarsForVersion(new CtsUrn(s))
        } catch (Exception e) {
            throw e
        }
    }


    /** Finds version-level URNs known to this inventory for a
    * requested CTS URN.
    * @param s A String representing the CTS URN to search for.
    * This URN may include either a work- or version-level reference to the work:     * it is trimmed to the work level before searching.  If u is given at the
    * text group level, it is not an error, but the returned list will be empty.
    * @returns A possibly empty List of Strings giving CtsUrns at the version
    * level.
    * @throws Exception if s is not a valid CtsUrn String
    */
    def versionsForWork(String s) {
        def urn
        try {
            urn = new CtsUrn(s)
            versionsForWork(new CtsUrn(s))
        } catch (Exception e) {
            throw e
        }
    }


    /** Creates reabable name for text group identified by URN.
    * @param u URN identifying text group to label.
    * @returns A String with a human-readable name for the text group,
    * or null if no text group is found for the requested URN.
    * @throws Exception if urnStr is not a valid CTS URN string.
    */
    String getGroupName(String urnStr)
    throws Exception {
        CtsUrn urn
        try {
            urn = new CtsUrn(urnStr)
        } catch (Exception e) {
            throw e
        }
        return getGroupName(urn)
    }


    /** Creates reabable name for text group identified by URN.
    * @param u URN identifying text group to label.
    * @returns A String with a human-readable name for the text group,
    * or null if no text group is found for the requested URN.
    */
    String getGroupName (CtsUrn u) {
        def tgStr = "urn:cts:${u.getCtsNamespace()}:${u.getTextGroup(false)}:"
	//System.err.println "Hunt for " + tgStr + " in "	 + this.textgroups
        def gp = this.textgroups.find {
            it[0] == tgStr
        }
        if (gp) {
            return gp[1]
        } else {
            return null
        }
    }


    /** Creates reabable name for text group identified by URN.
    * @param u URN identifying text group to label.
    * @returns A String with a human-readlabe name for the text group,
    * or null if no text group is found for the requested URN.
    * @throws Exception if s is not a valid CtsUrn String
    */

    String groupName(String s) {
        def u
        try {
            u =  new CtsUrn(s)
            return groupName(u)
        } catch (Exception e) {
            throw e
        }
    }



    /** Gets a reabable name for a work identified by URN.
    * @param u CTS URN identifying the work to label.
    * @returns A String with a human-readlabe name for the work,
    * or null if no work is found for the requested URN.
    */
    String workTitle (CtsUrn u) {
        def wk = this.works.find {
            (it[0] == u.toString())
        }
        if (wk) {
            wk[1]
        } else {
            null
        }
    }


    /** Creates reabable name for a work identified by URN.
    * @param u URN identifying the work to label.
    * @returns A String with a human-readlabe name for the work,
    * or null if no work is found for the requested URN.
    * @throws Exception if s is not a valid CtsUrn String
    */
  String workTitle(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return workTitle(u)
        } catch (Exception e) {
            throw e
        }
    }


    /** Creates reabable name for a translation identified by URN.
    * @param u URN identifying the translation to label.
    * @returns A String with a human-readable name for the translation,
    * or null if no translation is found for the requested URN.
    */
    String translationLabel (CtsUrn u) {
        def xlat = translations.find {
            it[0] == u.getUrnWithoutPassage()
        }
        if (xlat) {
            return xlat[1]
        } else { return null }
    }

    /** Creates reabable name for a translation identified by URN.
    * @param u URN identifying the translation to label.
    * @returns A String with a human-readable name for the translation,
    * or null if no translation is found for the requested URN.
    * @throws Exception if s is not a valid CtsUrn String
    */
    def translationLabel (String s) {
        def u
        try {
            u = new CtsUrn(s)
            return translationLabel(u)
        } catch (Exception e) {
            throw e
        }
    }


    /** Creates reabable name for an exemplar identified by URN.
    * @param u URN identifying the edition to label.
    * @returns A String with a human-readable name for the edition,
    * or null if no edition is found for the requested URN.
    */
    String exemplarLabel (CtsUrn u) {
      String exLabel = null
      String urnBase = u.getUrnWithoutPassage()

      exemplars.each { e ->

	String firstCol = e[0]

	if (firstCol == urnBase) {
	  exLabel = e[1]
	  if (debug > 1) {
	    println "Equal! ${firstCol == urnBase} #${urnBase}# with #${firstCol}#"
	    println "exLabel now ${exLabel}"
	  }

	}
      }
      return exLabel
    }

    /** Creates reabable name for an exemplar identified by URN.
    * @param u URN identifying the translation to label.
    * @returns A String with a human-readable name for the translation,
    * or null if no translation is found for the requested URN.
    * @throws Exception if s is not a valid CtsUrn String
    */
    def exemplarLabel (String s) {
        def u
        try {
            u = new CtsUrn(s)
            return exemplarLabel(u)
        } catch (Exception e) {
            throw e
        }
    }

    /** Creates reabable name for an edition identified by URN.
    * @param u URN identifying the edition to label.
    * @returns A String with a human-readable name for the edition,
    * or null if no edition is found for the requested URN.
    */
    String editionLabel (CtsUrn u) {
      String edLabel = null
      String urnBase = u.getUrnWithoutPassage()

      editions.each { e ->

	String firstCol = e[0]

	if (firstCol == urnBase) {
	  edLabel = e[1]
	  if (debug > 1) {
	    println "Equal! ${firstCol == urnBase} #${urnBase}# with #${firstCol}#"
	    println "edLabel now ${edLabel}"
	  }

	}
      }
      return edLabel
    }

    /** Creates reabable name for an edition identified by URN.
    * @param u URN identifying the edition to label.
    * @returns A String with a human-readable name for the edition,
    * or null if no edition is found for the requested URN.
    * @throws Exception if s is not a valid CtsUrn String
    */
  String editionLabel(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return editionLabel(u)
        } catch (Exception e) {
            throw e
        }
    }


   /** Creates reabable name for an edition identified by URN.
    * @param u URN identifying the edition to label.
    * @returns A String with a human-readable name for the version,
    * (either translation or edition) or null if no version is found for the requested URN.
    */
    String versionLabel(CtsUrn u) {
        def edLabel = editionLabel(u)
        def xlatLabel = translationLabel(u)
		def exempLabel = exemplarLabel(u)

	if (debug > 1) {
	  println "versionLabel for ${u} gives edLabel ${edLabel}"
	}
        if (edLabel) {
            return edLabel
        } else if (xlatLabel) {
            return xlatLabel
        } else if (exempLabel) {
            return exempLabel
        } else {
	  System.err.println "No label for ${u}"
            return null
        }
    }


   /** Creates reabable name for an edition identified by URN.
    * @param u URN identifying the edition to label.
    * @returns A String with a human-readable name for the version,
    * (either translation or edition) or null if no version is found for the requested URN.
    * @throws Exception if s is not a valid CtsUrn String
    */
    String versionLabel(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return versionLabel(u)
        } catch (Exception e) {
            throw e
        }
    }


    /* ********************************************************************/
    /* ** Begin "private" or internal methods *****************************/


    /** "Private" method populates TI model from a string serialization of a
    * CTS TextInventory.
    * @param str String giving content of a valid XML inventory.
    */
    void initFromTextInvString (String str) {
        def root = new XmlParser().parseText(str)
        initFromParsedTextInv(root, true)
    }

    void initFromParsedTextInv (groovy.util.Node root) {
        initFromParsedTextInv(root, true)
    }


    /** "Private" method populates TI model from an XML representation
    * of a CTS TextInventory that has been parsed as a
    * groovy Node object.
    * @root The root Node of the parse.
    * @throws Exception if data values violate definition of TextInventory.
    */
    void initFromParsedTextInv (groovy.util.Node root, boolean checkData)
    throws Exception {
        if (checkData) {
            try {
                errorList = TextInventoryXmlVerifier.checkDataValues(root)
            } catch(Exception e) {
                throw e
            }
        }
        //this.tiId = root.'@tiid'
        this.tiVersion = root.'@tiversion'
        ctsnamespaces = TextInventoryFileReader.collectCtsNamespaceData(root)

        root[ti.textgroup].each { g ->
          textgroups.add(TextInventoryFileReader.tgFromNode(g))

            g[ti.work].each { w ->
              works.add(TextInventoryFileReader.wkFromNode(w, g.'@urn'))
              worksLanguages << TextInventoryFileReader.languageMapping(w)

                w[ti.edition].each { ed ->
                  editions.add(TextInventoryFileReader.childObjectFromNode(ed, w.'@urn'))

		  ed[ti.exemplar].each { ex ->
		    exemplars.add(TextInventoryFileReader.childObjectFromNode(ex,ed.'@urn'))
		  }
                }

                w[ti.translation].each { tr ->
                  translations.add(TextInventoryFileReader.childObjectFromNode(tr, w.'@urn'))
		  translationLanguages << TextInventoryFileReader.languageMapping(tr)

		  tr[ti.exemplar].each { ex ->
		    exemplars.add(TextInventoryFileReader.childObjectFromNode(ex,tr.'@urn'))
		  }
		}
	    }
	}
    }


    /** "Private" method finds data structures for all online versions of
    * texts known to the inventory.
    * @returns A List of version-level data structures.
    */

      /// MOVE TO CITATION CONFIG

  /*
    def allDataOnline() {
        def onlineEdd = editions.findAll {
            it[2] == true
        }
        def onlineTranss = translations.findAll {
            it[2] == true
        }
        return onlineEdd + onlineTranss
    }

  */



    /** "Private" or internal method finds work-level data structures for all works
     * available online for the textgroup of a given CTS URN.
    * @param u A CTS URN to check for.
    * @returns A (possibly empty) List of work-level data structures.
    * @throws Exception if s is not a valid CtsUrn String
    */
    def worksDataForGroup(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return worksDataForGroup(u)
        } catch (Exception e) {
            throw e
        }
    }

    /** "Private" or internal method finds work-level data structures for all works
     * available online for the textgroup of a given CTS URN.
    * @param u CTS URN of a textgroup to check for.
    * @returns A (possibly empty) List of work-level data structures.
    */
    def worksDataForGroup(CtsUrn u) {
        def wkList = works.findAll {
            (it[2] == u.toString())
        }
        return wkList
    }

    /**   "Private" or internal method finds version-level data structures for all online versions
    * belonging to the TextGroup of the requested CtsUrn.
    * @param u CtsUrn identifying the TextGroup to search for.
    * @returns A (possibly empty) List of version data structures.
    * @throws Exception if s is not a valid CtsUrn String
    */

      /// MOVE TO CITATION CONFIG
  /*
    def onlineDataForGroup(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return onlineDataForGroup(new CtsUrn(s))
        } catch (Exception e) {
            throw e
        }
    }
  */


    /**   "Private" or internal method finds version-level data structures
    * for all online versions belonging to the TextGroup of the requested
    * CtsUrn.
    * @param u CtsUrn identifying the TextGroup to search for.
    * @returns A (possibly empty) List of version data structures.
    */
      /// MOVE TO CITATION CONFIG
  /*
    def onlineDataForGroup(CtsUrn u) {
        def online = []
        def wks = worksDataForGroup(u)
        wks.each { w ->
            def workList = onlineDataForWork(w[0])
            online = online + workList
        }
        return online
	}*/




    /**  "Private" or internal method finds version-level data structures for all versions
     * available online for a given CTS URN.
    * @param u A CTS URN to check for.
    * @returns A (possibly empty) List of version-level data structures.
    */
      /// MOVE TO CITATION CONFIG
  /*
    def onlineDataForWork(CtsUrn u) {
        def edlist = editions.findAll {
            ((it[3] == u.toString()) & (it[2] == true))
        }
        def edUrls = []
        edlist.each { ed ->
            edUrls.add(ed)
        }

        def trlist = translations.findAll {
            ((it[3] == u.toString()) & (it[2] == true))
        }

        def transUrls = []
        trlist.each { tr ->
            transUrls.add(tr)
        }
        return edUrls + transUrls
    }



  */



    /**  "Private" or internal method finds version-level data structures for all versions
     * available online for a given CTS URN.
    * @param u A CTS URN to check for
    * This URN may include either a work- or version-level reference to the work:
    * it is trimmed to the work level before searching.  If u is given at the
    * text group level, it is not an error, but the returned list will be empty.
    * @returns A (possibly empty) List of version-level data structures.
    * @throws Exception if s is not a valid CtsUrn String
    */
      /// MOVE TO CITATION CONFIG
  /*
    def onlineDataForWork(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return onlineDataForWork(new CtsUrn(s))
        } catch (Exception e) {
            throw e
        }
    }
  */



    /**  "Private" or internal method finds version data structures for the work identified by
    * requested CTS URN.
    * @param s A String representing the CTS URN to search for.
    * This URN may include either a work- or version-level reference to the work:
    * it is trimmed to the work level before searching.  If u is given at the
    * text group level, it is not an error, but the returned list will be empty.
    * @returns A possibly empty List of Strings giving CtsUrns at the version
    * level.
    * @throws Exception if s is not a valid CtsUrn String
    */
    def versionsDataForWork(String s) {
        def u
        try {
            u = new CtsUrn(s)
            return versionsDataForWork(u)
        } catch (Exception e) {
            throw e
        }
    }

    /**  "Private" or internal method finds version data structures for the work identified by
    * requested CTS URN.
    * @param s A String representing the CTS URN to search for.
    * This URN may include either a work- or version-level reference to the work:
    * it is trimmed to the work level before searching.  If u is given at the
    * text group level, it is not an error, but the returned list will be empty.
    * @returns A possibly empty List of Strings giving CtsUrns at the version
    * level.
    */
    def versionsDataForWork(CtsUrn u) {
        def wk = "${u.getCtsNamespace()}:${u.getTextGroup(false)}.${u.getWork(false)}"
        def edList = editions.findAll {
            it[0] == wk.toString()
        }
        def transList = translations.findAll {
            it[0] == wk.toString()
        }
        return edList + transList
    }

    /* ** End "private" or internal methods *******************************/
    /* ********************************************************************/

}
