package edu.holycross.shot.hocuspocus

abstract class TabulatorRegEx {


  static def citationPattern = ~/.+['"]?['"].+/

  static ArrayList splitAncestors(String s) {
    return s.split(/\\//)
  }



}
