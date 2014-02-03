package edu.holycross.shot.hocuspocus


/**
*/
public interface TokenizationSystem {


  /** Returns a human-readable description of the tokenization system */
  String getDescription()


  /** Tokenizes a tabulated representation of a text.
   * @param inputFile File with the tabular representation of the text.
   * @param separatorStr String used to separate columns in the tabular file.
   * @returns An ordered list of URN pairs.  The first URN in each pair is a CTS
   * URN including subreference identifying the token.  The second URN classifies
   * the token with an appropriate value determined by the implementing system.
   */
  ArrayList tokenize(File inputFile, String separatorStr)


}
