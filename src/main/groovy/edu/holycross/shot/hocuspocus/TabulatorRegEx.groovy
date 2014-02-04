package edu.holycross.shot.hocuspocus

class TabulatorRegEx {


  def citationPattern = ~/.+['"]?['"].+/

  TabulatorRegEx() {}

  def splitAncestors(String s) {
    return s.split(/\\//)
  }

}
