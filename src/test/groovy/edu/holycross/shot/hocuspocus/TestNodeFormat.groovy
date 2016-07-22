package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail





/**
*/
class TestNodeFormat  {



    def expectedLabels = ["xml", "markdown", "text"]

    @Test
    void testNodeFormatEnum() {
      ArrayList testList = NodeFormat.values()  as ArrayList
      testList.eachWithIndex { n, i ->
        assert n.getLabel() == expectedLabels[i]
      }
    }


    @Test
    void testIndex() {
      assert NodeFormat.getByLabel("xml") == NodeFormat.XML

      assert NodeFormat.getByLabel("markdown") == NodeFormat.MARKDOWN

      assert NodeFormat.getByLabel("text") == NodeFormat.PLAIN_TEXT
    }

}
