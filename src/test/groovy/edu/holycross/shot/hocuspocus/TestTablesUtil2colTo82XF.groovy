package edu.holycross.shot.hocuspocus

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.TextInventory

import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


/** */
class TestTablesUtil2colTo82XF {

  @Test
  void testSingleLine() {
    String ln = "urn:cts:aflibre:af.ah.hc:19420614.h1#Zondag, 14 Juni 1942"
    TablesUtil tu = new TablesUtil()
    String str82xf = tu.twoTo82XF(ln)
    def lines82xf = str82xf.readLines()
    // header + 1 line of text:
    assert lines82xf.size() == 2
    def cols = lines82xf[1].split(/#/)
    // 5 items in 82XF format:
    assert cols.size() == 5
    assert cols[0] == "urn:cts:aflibre:af.ah.hc:19420614.h1"
    assert cols[4] == "Zondag, 14 Juni 1942"
    // no prev, no next:
    assert cols[1] == ""
    assert cols[3] == ""
    // sequence number is an integer, but have to do a 
    // stupid test to force evaluation as boolean since groovy will take a value of 0
    // to mean false in boolean context!
    assert cols[2].toInteger() + 1 == (2 - 1 + cols[2].toInteger())
  }



  @Test
  void testTwoLines() {
    String lns = """urn:cts:aflibre:af.ah.hc:19420614.h1#Zondag, 14 Juni 1942
urn:cts:aflibre:af.ah.hc:19420614.p1#Vrijdag 12 Juni was ik al om 6 uur wakker en dat is heel begrijpelijk, daar ik jarig was. Maar om 6 uur mocht ik toch nog niet opstaan, dus moest ik mijn nieuwsgierigheid bedwingen tot kwart voor zeven. Toen ging het niet langer, ik ging naar de eetkamer, waar ik door Moortje (de kat) met kopjes verwelkomd werd."""
    TablesUtil tu = new TablesUtil()
    String str82xf = tu.twoTo82XF(lns)
    def lines82xf = str82xf.readLines()
    // header + 1 line of text:
    assert lines82xf.size() == 3

    def item1 = lines82xf[1].split(/#/)
    // 5 items in 82XF format:
    assert item1[0] == "urn:cts:aflibre:af.ah.hc:19420614.h1"
    assert item1[1] == ""
    assert item1[3] == "urn:cts:aflibre:af.ah.hc:19420614.p1"

    def item2 = lines82xf[2].split(/#/)
    assert item2[0] == "urn:cts:aflibre:af.ah.hc:19420614.p1"
    assert item2[1] == "urn:cts:aflibre:af.ah.hc:19420614.h1"
    assert item2[3] == ""
  }




    @Test
  void testThreeLines() {
    String lns = """urn:cts:aflibre:af.ah.hc:19420614.h1#Zondag, 14 Juni 1942
urn:cts:aflibre:af.ah.hc:19420614.p1#Vrijdag 12 Juni was ik al om 6 uur wakker en dat is heel begrijpelijk, daar ik jarig was. Maar om 6 uur mocht ik toch nog niet opstaan, dus moest ik mijn nieuwsgierigheid bedwingen tot kwart voor zeven. Toen ging het niet langer, ik ging naar de eetkamer, waar ik door Moortje (de kat) met kopjes verwelkomd werd.
urn:cts:aflibre:af.ah.hc:19420614.p2#Om even na zevenen ging ik naar papa en mama en dan naar de huiskamer, om mijn cadeautjes uit te pakken. Het was in de eerste plaats jou die ik te zien kreeg, wat misschien wel een van mijn jnste cadeau's is. Dan een bos rozen, een plantje, twee takken pinkster-rozen, dat waren die ochtend de kinderen van Flora, die op mijn tafel stonden, maar er kwam nog veel meer."""
    
    TablesUtil tu = new TablesUtil()
    String str82xf = tu.twoTo82XF(lns)
    def lines82xf = str82xf.readLines()
    // header + 1 line of text:
    assert lines82xf.size() == 4

    def item1 = lines82xf[1].split(/#/)
    // 5 items in 82XF format:
    assert item1[0] == "urn:cts:aflibre:af.ah.hc:19420614.h1"
    assert item1[1] == ""
    assert item1[3] == "urn:cts:aflibre:af.ah.hc:19420614.p1"

    def item2 = lines82xf[2].split(/#/)
    assert item2[0] == "urn:cts:aflibre:af.ah.hc:19420614.p1"
    assert item2[1] == "urn:cts:aflibre:af.ah.hc:19420614.h1"
    assert item2[3] == "urn:cts:aflibre:af.ah.hc:19420614.p2"

    def item3 = lines82xf[3].split(/#/)
    assert item3[0] == "urn:cts:aflibre:af.ah.hc:19420614.p2"
    assert item3[1] == "urn:cts:aflibre:af.ah.hc:19420614.p1"
    assert item3[3] == ""

  }


  
  @Test
  void test2ColFileto82() {
    File twoCol = new File("testdata/2cols/archive/achterhuis_2cols.txt")

    TablesUtil tu = new TablesUtil()
    String str82xf = tu.twoTo82XF(twoCol)
    def  lines = str82xf.readLines()
    // add 1 for header
    assert lines.size() == twoCol.readLines().size() + 1
  }

}
