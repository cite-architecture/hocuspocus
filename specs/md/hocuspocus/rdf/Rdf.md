# Generating RDF #

You can represent archival text files in a [corpus](../corpus/Corpus.html) as RDF statements equivalent under the OHCO2 model of citable texts. 



@openex@
### Example ###



The files in <a href="../../../resources/test/data/archive1/xml/" concordion:set="#archive = setHref(#HREF)">this root directory</a> (documented in 
 <a href="../../../resources/test/data/archive1/testinventory.xml" concordion:set="#ti = setHref(#HREF)">this TextInventory file</a>) contain two selections from the *Iliad*, each 32 lines long.   If we express the entire repository as RDF in  <a href="../../../tabulated" concordion:set="#tabdir = setHref(#HREF)">this directory</a>, the file `corpus.ttl` will describe <strong concordion:assertEquals="shouldCountTtlContentLines(#ti,#archive,#tabdir)">64</strong> citable units of text.


@closeex@


## RDF verbs ##



The files in <a href="../../../resources/test/data/archive1/xml/" concordion:set="#archive2 = setHref(#HREF)">this root directory</a> (documented in 
 <a href="../../../resources/test/data/archive1/testinventory.xml" concordion:set="#ti2 = setHref(#HREF)">this TextInventory file</a>) contain two selections from the *Iliad*, each 32 lines long.   If we express the entire repository as RDF in  <a href="../../../verbtabs" concordion:set="#tabdir2 = setHref(#HREF)">this directory</a>, the file `corpus.ttl`  will give us these verbs:

 <table concordion:verifyRows="#rdfverb : shouldGetVerbs(#ti2,#archive2,#tabdir2)">
        <tr><th concordion:assertEquals="#rdfverb">Verb (with abbreviated prefix)</th></tr>
        <tr><td>cts:abbreviatedBy</td></tr>
        <tr><td>cts:belongsTo</td></tr>
                <tr><td>cts:citationDepth</td></tr>
                <tr><td>cts:containedBy</td></tr>
                                <tr><td>cts:contains</td></tr>
                                                <tr><td>cts:hasSequence</td></tr>
                                                                <tr><td>cts:hasTextContent</td></tr>
                                                                                <tr><td>cts:lang</td></tr>
                                                                                                <tr><td>cts:next</td></tr>
                                                                                                                <tr><td>cts:possesses</td></tr>
                                                                                                                                <tr><td>cts:prev</td></tr>
                                                                                                                                                <tr><td>cts:xmlns</td></tr>
                                                                                                                                                                <tr><td>dcterms:title</td></tr>
                                                                                                                                                                                <tr><td>hmt:xmlOpen</td></tr>
                                                                                                                                                                                <tr><td>hmt:xpTemplate</td></tr>
                                                                                                                                                                                
                                                                                                                                                                                <tr><td>rdf:label</td></tr>
                                                                                                                                                                                
 <tr><td>rdf:type</td></tr>
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  

    </table>