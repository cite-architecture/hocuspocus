package edu.holycross.shot.hocuspocus


/**
*/
public interface TokenEditionGenerator {

  /** Returns a human-readable description of the system
   * for generating token editions derived from and aligned 
   * with a source edition. 
   */
  String getDescription()


  /** Generates a tokenized edition from a tabulated representation of a text.
   * It creates two artifacts in outputDirectory:
   * - a file named "tokenedition.txt" with the tabular representation of
   * the tokenized edition
   * - a TTL mapping of the citable nodes of the token edition to the
   * source edition
   * @param inputFile File with the tabular representation of the text.
   * @param separatorStr String used to separate columns in the tabular file.
   * @param outputDirectory A writable directory where output will be created.
   */
  void generate(File inputFile, String separatorStr, File outputDirectory)

}
