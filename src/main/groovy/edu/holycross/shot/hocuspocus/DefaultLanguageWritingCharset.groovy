package edu.holycross.shot.hocuspocus


/** Class implementing the LanguageCharacters interface when
* the language is unknown or unsupported.  Other classes can
* extend this class based on knowledge of how specific languages
* are represented using some subset of Unicode.
*/
class DefaultLanguageWritingCharset implements LanguageWritingCharset {


    static String charSet = "UTF-8"
    /** Empty constructor.
    */
    DefaultLanguageWritingCharset() {}

    /**  Always returns null since this class
    * is specifically for handling character set questions
    * when the mapping of character representations onto
    * Unicode code points is unknown.
    */
    String getLanguageCode() {
        return null
    }

    String getWritingSystemDescription() {
        return "Unlimited use of UTF-8 character set with no specified semantics."
    }

    String getDigitalCharset() {
        return this.charSet
    }

    /** This class accepts only the character representation of
    * defined Unicode code points.
    */
    boolean isValidString(String stringToTest) {
        stringToTest.getChars().each { c ->
            def i = c as int
            try {
                Character.isDefined(i)
            } catch (Exception e) {
                return false
            }
        }
        return true
    }



}
