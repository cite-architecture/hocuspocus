package edu.holycross.shot.hocuspocus


/**
*/
public interface TokenizationSystem {

    /** Returns a description of the tokenization system */
    String getDescription()


    /** Tokenizes a tabulated representation of a text.
    * @param inputFile File with the tabular representation of the text.
    * @param separatorStr String used to separate columns in the tabular file.
    * @returns An ordered list of two-item lists consisting of a string and a class.
    */
    ArrayList tokenize(File inputFile, String separatorStr)

}
