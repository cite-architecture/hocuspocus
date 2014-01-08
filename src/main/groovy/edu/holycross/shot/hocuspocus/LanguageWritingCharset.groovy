package edu.holycross.shot.hocuspocus


/** Defines behaviors that depend on the interaction of a document's
* language, original writing system, and mapping of that writing system
* to some digital character set.
*/
public interface LanguageWritingCharset {

    String getLanguageCode()
    String getDigitalCharset()
    String getWritingSystemDescription()
    

    boolean isValidString(String stringToTest)

    

}
