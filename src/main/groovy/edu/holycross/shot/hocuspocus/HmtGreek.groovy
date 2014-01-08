package edu.holycross.shot.hocuspocus


/**  Implements the LanguageWritingCharset interface for Greek texts
* following the conventions of the Homer Multitext project.
*/
class HmtGreek extends DefaultLanguageWritingCharset implements LanguageWritingCharset {

    static String langCode = "grc"
    static String digitalCharset = "UTF-8"
    HmtGreek() {
    }


    HmtGreek(File f) {
        this.document = f
    }

    String getLanguageCode() {
        return langCode
    }


    String getDigitalCharset() {
        return digitalCharset
    }

    boolean isValidString(String stringToTest) {
        //...
        return true
    }
    
}
