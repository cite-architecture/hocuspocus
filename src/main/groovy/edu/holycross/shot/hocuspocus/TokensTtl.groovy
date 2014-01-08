package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory




/** Class managing serialization of token editions as RDF TTL.
*/
class TokensTtl {

    File tokenSourceFile
    TokenizationSystem tokenSystem 

    TokensTtl(File srcFile, String tokenizerClassName) {
        this.tokenSourceFile = srcFile

    }
}
