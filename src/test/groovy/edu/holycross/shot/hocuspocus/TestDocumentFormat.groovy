package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail





/**
*/
class TestDocumentFormat  {



    def expectedLabels = ["xml", "2cols", "82xf", "markdown"]

    @Test
    void testDocFormatEnum() {
      ArrayList testList = DocumentFormat.values()  as ArrayList
      testList.eachWithIndex { n, i ->
        assert n.getLabel() == expectedLabels[i]
      }
    }

/*
    @Test
    void testIndex() {
      assert Mood.getByToken("<indic>") == Mood.INDICATIVE
      assert Mood.getByToken("<subj>") == Mood.SUBJUNCTIVE
      assert Mood.getByToken("<opt>") == Mood.OPTATIVE
      assert Mood.getByToken("<imptv>") == Mood.IMPERATIVE
    }
    */
}
