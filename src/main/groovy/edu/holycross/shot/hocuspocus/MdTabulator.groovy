package edu.holycross.shot.hocuspocus

/**
 * A utility class for working with texts in Bare Markdown,
 * and converting them to citable tabular forms.
 *
*/
class MdTabulator {

  /** Constructor with no parameters.
   */
  MdTabulator() {
  }



  static String mdFileTo82XF(File f, String urn) {
    return mdFileTo82XF(f,  urn, "#")
  }
  static String mdFileTo82XF(File f, String urn, String delimiter) {
    String twoColumns = mdFileToTwoColumns(f,urn,delimiter)
    String o2xf = TablesUtil.twoTo82XF(twoColumns,delimiter)
    return o2xf
  }

  
  /** Converts a bare markdown file to an ordered
  * list of URN-text content pairings. URN and
  * text content in the output are delimted by "#".
  * @param f File with bare markdown to convert.
  * @param urn Version-level URN for the text.
  * @returns A list of URN-text content pairings.
  */
  static String mdFileToTwoColumns(File f, String urn){
      return mdFileToTwoColumns(f,urn,"#")

  }
  /** Converts a bare markdown file to an ordered
  * list of URN-text content pairings.
  * @param f File with bare markdown to convert.
  * @param urn Version-level URN for the text.
  * @param delimter String to use as delimiter in output between
  * URN and text content.
  * @returns A list of URN-text content pairings.
  */
  static String mdFileToTwoColumns(File f, String urn, String delimiter){
    StringBuffer twoColumns = new StringBuffer()
    
    // depth in citation hierarchy
    def currDepth = 0
    // count of leaf node in current container
    def currNode = 0

    // list of citation values ordered from
    // outermost container to leaf node
    def citationSkeleton = []

    f.eachLine { l ->
      // check for bare md heading ("#")
      def headingMatch = l =~ "[ ]*(#+)(.+)"
      if (headingMatch.getCount()) {
        headingMatch.each { wholematch, poundSigns, txt ->
          def idx = poundSigns.size() - 1
          if (citationSkeleton[idx]) {
            def prevCount = citationSkeleton[idx]
            citationSkeleton[idx] = prevCount + 1

          } else {
            citationSkeleton[idx] = 1
          }
          citationSkeleton = citationSkeleton.take(idx + 1)
	  twoColumns.append(urn + citationSkeleton.join(".") + ".h1" + delimiter + txt + "\n")
          currNode = 0
        }
      } else {
        if (l ==~ /^[ ]*$/) {
	  // skip empty lines
        } else {
          currNode++;
	  twoColumns.append(urn + citationSkeleton.join(".") + ".n${currNode}" + delimiter + l + "\n")
        }
      }
    }
  }

}
