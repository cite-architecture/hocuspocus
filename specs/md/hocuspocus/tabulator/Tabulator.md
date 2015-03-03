# Tabulator #

You can convert archival text files in a [corpus](../corpus/Corpus.html) to a tabular delimited-text format that is equivalent under the OHCO2 model of citable texts.  It is possible to specify by URN a single document to convert, or to convert the entire corpus documented in the Text Inventory.


### Example ###


Using the files in <a href="../../../specs/data/archive1/xml/" concordion:set="#archive = setHref(#HREF)">this root directory</a> and 
 <a href="../../../specs/data/archive1/testinventory.xml" concordion:set="#ti = setHref(#HREF)">this TextInventory file</a>, if we request a tabular representation of <strong concordion:set="#urn">urn:cts:greekLit:tlg0012.tlg001.msA:</strong>, the results will be in a file named <strong concordion:assertEquals="shouldGetFileNameForUrn(#ti,#archive,#urn)">Iliad-A.txt</strong>, and will have
<strong concordion:assertEquals="shouldCountTabulatedLines(#ti,#archive,#urn)">33</strong> lines.

If we tabulate all files in the same corpus in  <a href="../../../tabulated" concordion:set="#tabdir = setHref(#HREF)">this directory</a>, we will generate <strong concordion:assertEquals="shouldCountTabs(#ti, #archive, #tabdir)">3</strong> files.


