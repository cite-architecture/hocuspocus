package edu.holycross.shot.hocuspocus

class OnlineSettings {

  
  String fileName
  DocumentFormat docFormat
  NodeFormat nodeFormat

  
  // ERROR CHECKING:  NodeFormat only allowed with
  // docformat 82xf and 2cols.  Exception otherwise.
  OnlineSettings(String fName, DocumentFormat df, NodeFormat nf) {
    this.fileName = fName
    this.docFormat = df
    this.nodeFormat = nf
  }


  
  OnlineSettings(String fName, DocumentFormat df) {
    this.fileName = fName
    this.docFormat = df
    this.nodeFormat = null
  }
  
}
