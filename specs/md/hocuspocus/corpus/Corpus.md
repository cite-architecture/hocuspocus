# Basic contents of a corpus of texts #


## Constructing a corpus ##


An archival corpus is made up of a set of text files, and an inventory documenting the citable structure of each document.  


@openex@
### Example ###


We can use <a href="../../../resources/test/data/archive1/testinventory.xml" concordion:set="#ti = setHref(#HREF)">this TextInventory file</a> with
files in <a href="../../../resources/test/data/archive1/xml" concordion:set="#archive = setHref(#HREF)">this root directory</a> to <strong concordion:assertTrue="shouldMakeCorpus(#ti,#archive)">construct a Corpus</strong>.

@closeex@


## The inventory ##

When serialized as XML, the inventory validates against a Relax NG schema.

For a given corpus, we can determine:

- the number of files cataloged in the inventory
- names of files cataloged in the inventory
- URNs of texts are cataloged in the inventory


@openex@
### Examples ###


In the example corpus defined above, the inventory contains entries for <strong concordion:assertEquals="shouldGetNumberFilesInInventory(#ti,#archive)">3</strong> files.  

The file names are:

<table concordion:execute="#result = shouldGetFilenameFromInventory(#ti, #archive, #idx)">
<tr><th concordion:set="#idx">Index</th><th concordion:assertEquals="#result">File path</th></tr>
<tr><td>0</td><td>Iliad-A.xml</td></tr>
<tr><td>1</td><td>Iliad-Butler.xml</td></tr>
<tr><td>2</td><td>tier2/Iliad-B.xml</td></tr>
</table>


Their URNs are :

<table concordion:execute="#result = shouldGetUrnsFromInventory(#ti, #archive, #idx)">
<tr><th concordion:set="#idx">Index</th><th concordion:assertEquals="#result">File path</th></tr>
<tr><td>0</td><td>urn:cts:greekLit:tlg0012.tlg001.butler:</td></tr>
<tr><td>1</td><td>urn:cts:greekLit:tlg0012.tlg001.msA:</td></tr>
<tr><td>2</td><td>urn:cts:greekLit:tlg0012.tlg001.msB:</td></tr>
</table>
@closeex@

## The archive of files ##

For a given corpus, we can determine:

- the number of XML files in the directory tree
- names of XML files cataloged in the directory tree


@openex@
### Examples ###

In the example corpus defined above, the inventory contains entries for <strong concordion:assertEquals="shouldGetNumberFilesOnDisk(#ti,#archive)">3</strong> files.  


These files are found in the file system:


<table concordion:execute="#result = shouldGetFilesOnDisk(#ti, #archive, #idx)">
<tr><th concordion:set="#idx">Index</th><th concordion:assertEquals="#result">File path</th></tr>

<tr><td>0</td><td>Iliad-A.xml</td></tr>
<tr><td>1</td><td>Iliad-Butler.xml</td></tr>
<tr><td>2</td><td>tier2/Iliad-B.xml</td></tr>
</table>

@closeex@


## Validating a corpus ##


We can determine if the list of files in the inventory have a one-to-one relation to the XML files in the directory hierarchy.  We can get names of documents identified in the inventory but not found on disk, and names of files found on disk but not identified in the inventory.

@openex@

### Examples ###

**One-to-one match**.  In the example corpus defined above, the files and inventory <strong concordion:assertTrue="filesAndInventoryShouldMatch(#ti,#archive)">do match</strong> (have a one-to-one correspondence).

**Files on disk missing from inventory**.
If we use <a href="../../../specs/data/archive1/incompleteinv.xml" concordion:set="#ti2 = setHref(#HREF)">this TextInventory file</a> with the same set of archival files, we can <strong concordion:assertTrue="shouldMakeCorpus(#ti2,#archive)">construct a valid Corpus</strong>, even though it contains only  <strong concordion:assertEquals="shouldGetNumberFilesInInventory(#ti2,#archive)">1</strong> entry for an online file.  We can verify that files listed in the inventory and files on disk<strong concordion:assertFalse="filesAndInventoryShouldMatch(#ti2,#archive)">do not match</strong>, and can determine that <strong concordion:assertEquals="shouldGetNumberFilesOnDiskNotInventoried(#ti2, #archive)">2</strong> files in the file system does not appear in the inventory, and that the first item (item <strong concordion:set="#missingIdx">0</strong>) in the list of missing files is <strong concordion:assertEquals="shouldGetFileOnDiskNotInventoried(#ti2,#archive, #missingIdx)">Iliad-Butler.xml</strong>.




**Files in inventory not found in file system**. If, with the same set of archival files, we use <a href="../../../specs/data/archive1/overbooked.xml" concordion:set="#ti3 = setHref(#HREF)">a TextInventory listing additional files as online</a> , we can still <strong concordion:assertTrue="shouldMakeCorpus(#ti3,#archive)">construct a valid Corpus</strong>, even though it contains  <strong concordion:assertEquals="shouldGetNumberFilesInInventory(#ti3,#archive)">3</strong> entries.  We can verify that files listed in the inventory and files on disk<strong concordion:assertFalse="filesAndInventoryShouldMatch(#ti3,#archive)">do not match</strong>, and can determine that <strong concordion:assertEquals="shouldGetNumberInventoriedFilesNotOnDisk(#ti3, #archive)">1</strong> file in the file system does not appear in the inventory, and that the first item in the list of missing files is <strong concordion:assertEquals="shouldGetInventoriedFileNotFound(#ti3,#archive, #missingIdx)">Iliad-C.xml</strong>.



@closeex@
