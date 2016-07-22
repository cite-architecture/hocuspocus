package edu.holycross.shot.hocuspocus

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail





/**
*/
class TestOnlineSettings  {


    @Test
    void testFullConstructor() {
      OnlineSettings online = new OnlineSettings("filename", DocumentFormat.O2XF, NodeFormat.XML)
      assert online.docFormat.getLabel() == "82xf"
      assert online.nodeFormat.getLabel() == "xml"

      // check invalid combos:
      assert shouldFail {
        OnlineSettings nogo = new OnlineSettings("filename",DocumentFormat.XML, NodeFormat.PLAIN_TEXT)
      }

      assert shouldFail {
        OnlineSettings nogo = new OnlineSettings("filename",DocumentFormat.MARKDOWN, NodeFormat.PLAIN_TEXT)
      }
    }

    @Test
    void testContructorDefaults() {
      OnlineSettings online82xf = new OnlineSettings("filename", DocumentFormat.O2XF)
      assert online82xf.nodeFormat == NodeFormat.PLAIN_TEXT

      OnlineSettings online2cols = new OnlineSettings("filename", DocumentFormat.TWO_COLS)
      assert online2cols.nodeFormat == NodeFormat.PLAIN_TEXT


      OnlineSettings onlineXml = new OnlineSettings("filename", DocumentFormat.XML)
      assert onlineXml.nodeFormat == NodeFormat.XML

      OnlineSettings onlineMd = new OnlineSettings("filename", DocumentFormat.MARKDOWN)
      assert onlineMd.nodeFormat == NodeFormat.MARKDOWN


    }


}
