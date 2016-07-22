package edu.holycross.shot.hocuspocus

class OnlineSettings {


  String fileName
  DocumentFormat docFormat
  NodeFormat nodeFormat

  def acceptsNode = [DocumentFormat.O2XF, DocumentFormat.TWO_COLS]

  // ERROR CHECKING:  NodeFormat only allowed with
  // docformat 82xf and 2cols.  Exception otherwise.
  OnlineSettings(String fName, DocumentFormat df, NodeFormat nf) {
    if (! acceptsNode.contains(df)) {
      throw new Exception("OnlineSettings:  cannot override node format for document format " + df)
    }

    this.fileName = fName
    this.docFormat = df
    this.nodeFormat = nf
  }


  // default value for node format is plain text
  /// if document format is not XML or markdown.
  OnlineSettings(String fName, DocumentFormat df) {
    this.fileName = fName
    this.docFormat = df
    switch(this.docFormat) {
      case DocumentFormat.XML:
      this.nodeFormat = NodeFormat.XML
      break
      case DocumentFormat.MARKDOWN:
      this.nodeFormat = NodeFormat.MARKDOWN
      break
      case DocumentFormat.O2XF:
      case DocumentFormat.TWO_COLS:
      this.nodeFormat = NodeFormat.PLAIN_TEXT
      break
    }
  }

}
