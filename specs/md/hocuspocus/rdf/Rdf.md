# Generating RDF #

You can represent archival text files in a [corpus](../corpus/Corpus.html) as RDF statements equivalent under the OHCO2 model of citable texts. 


### Example ###


The files in <a href="../../../specs/data/archive1/xml/" concordion:set="#archive = setHref(#HREF)">this root directory</a> (documented in 
 <a href="../../../specs/data/archive1/testinventory.xml" concordion:set="#ti = setHref(#HREF)">this TextInventory file</a>) contain two selections from the *Iliad*, each 32 lines long.   If we express the entire repository as RDF in  <a href="../../../tabulated" concordion:set="#tabdir = setHref(#HREF)">this directory</a>, the file `corpus.ttl` will describe <strong concordion:assertEquals="shouldCountTtlContentLines(#ti,#archive,#tabdir)">64</strong> citable units of text.



