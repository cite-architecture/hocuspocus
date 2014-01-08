package edu.holycross.shot.hocuspocus


public interface DocumentConfiguration {

    
    void setDocument(File doc)
    void setLanguageWritingCharset(LanguageWritingCharset lwc)

    String getDescription()
    String getId()
    boolean isValid()
    void writeTokenUrns(File outputFile)
}
