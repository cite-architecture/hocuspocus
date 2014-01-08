package edu.holycross.shot.hocuspocus


class Latin26 extends DefaultLanguageWritingCharset implements LanguageWritingCharset {

    static String langCode = "lat"
    static String digitalCharset = "UTF-8"

    Latin26() {
    }


    Latin26(File f) {
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
