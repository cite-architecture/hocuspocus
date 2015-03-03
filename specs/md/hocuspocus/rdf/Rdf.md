# Generating RDF #

You can represent archival text files in a [corpus](../corpus/Corpus.html) as RDF statements equivalent under the OHCO2 model of citable texts. 



@openex@
### Example ###



The files in <a href="../../../resources/test/data/archive1/xml/" concordion:set="#archive = setHref(#HREF)">this root directory</a> (documented in 
 <a href="../../../resources/test/data/archive1/testinventory.xml" concordion:set="#ti = setHref(#HREF)">this TextInventory file</a>) contain two selections from the *Iliad*, each 32 lines long.   If we express the entire repository as RDF in  <a href="../../../tabulated" concordion:set="#tabdir = setHref(#HREF)">this directory</a>, the file `corpus.ttl` will describe <strong concordion:assertEquals="shouldCountTtlContentLines(#ti,#archive,#tabdir)">73</strong> citable units of text.


@closeex@


## RDF verbs ##


`hocuspocus` uses RDF verbs from four vocabularies:   

- the RDF Concepts vocabulary (http://www.homermultitext.org/hmt/rdf/, abbreivated `rdf`)
-  the Dublin Core Metdata vocabulary  (http://purl.org/dc/terms/, abbreviated `dc`)
-  the Canonical Text Services vocabulary (http://www.homermultitext.org/cts/rdf/, abbreviated `cts`) 
-  the Homer Multitext project vocabulary (http://www.homermultitext.org/hmt/rdf/, abbreviated `hmt`).

@openex@

### Example ###


@closeex@

### RDF Concepts ###

CTS textgroups, works, versions and exemplars are classified with the `rdf:type` verb;  the predicate is one of `cts:TextGroup`, `cts:Work`, `cts:Edition`, `cts:Translation` or `cts:Exemplar`.

CTS textgroups, works, versions, exemplars and citable nodes of text are labelled using  the `rdf:label` verb and a String predicate.

### Dublin Core ###

Titles of CTS textgroups, works, versions and exemplars are identified with `dc:title` verbs.

### Canonical Text Services ###

Twelve verbs in the `cts` namespace are used with the following semantics (TBA):

- cts:abbreviatedBy
- cts:belongsTo
- cts:citationDepth
- cts:containedBy
- cts:contains
- cts:hasSequence
- cts:hasTextContent
- cts:lang
- cts:next
- cts:possesses
- cts:prev
- cts:translationLang
- cts:xmlns


### Homer Multitext project ###



Two verbs from the `hmt` namespace describe information used internally in this implementation in the conversion of canonically citable texts to and from XML serializations (TBA):

- hmt:xmlOpen
- hmt:xpTemplate


@openex@
### Examples ###


The files in <a href="../../../resources/test/data/archive1/xml/" concordion:set="#archive2 = setHref(#HREF)">this root directory</a> (documented in 
 <a href="../../../resources/test/data/archive1/testinventory.xml" concordion:set="#ti2 = setHref(#HREF)">this TextInventory file</a>) contain two selections from the *Iliad*, each 32 lines long.   If we express the entire repository as RDF in  <a href="../../../verbtabs" concordion:set="#tabdir2 = setHref(#HREF)">this directory</a>, the file `corpus.ttl`  will give us these verbs:

I also want ot know what we get for just the inventory, so how about

<strong concordion:assertEquals="getInvTTL(#ti2)">INV VERBS</strong>


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
<tr><td>cts:translationLang</td></tr>
<tr><td>cts:xmlns</td></tr>
<tr><td>dcterms:title</td></tr>
<tr><td>hmt:xmlOpen</td></tr>
<tr><td>hmt:xpTemplate</td></tr>                                                                                                                                                                     
<tr><td>rdf:label</td></tr>
<tr><td>rdf:type</td></tr>
</table>

@closeex@
