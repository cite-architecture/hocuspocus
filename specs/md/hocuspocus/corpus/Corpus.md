# Constructing and validating the corpus #

An archival corpus is made of text files and an inventory documenting the citable structure them.  

### Example ###


We can use <a href="../../../specs/data/archive1/testinventory.xml" concordion:set="#ti = setHref(#HREF)">this TextInventory file</a> with
files in <a href="../../../specs/data/archive1/xml" concordion:set="#archive = setHref(#HREF)">this root directory</a> to <strong concordion:assertTrue="shouldMakeCorpus(#ti,#archive)">construct a Corpus</strong>.


## The inventory ##

For a given corpus, we can determine:

- what files are cataloged in the inventory
- what URNs are cataloged in the inventory

The inventory contains entries for <strong concordion:assertEquals="shouldGetNumberFilesInInventory(#ti,#archive)">2</strong> files.  

The file names are:

<table concordion:execute="#result = shouldGetFilenameFromInventory(#ti, #archive, #idx)">
<tr><th concordion:set="#idx">Index</th><th concordion:assertEquals="#result">File path</th></tr>
<tr><td>0</td><td>A_Iliad_p5uc.xml</td></tr>
<tr><td>1</td><td>tier2/B_Iliad_p5.xml</td></tr>
</table>


Their URNs are :


<table concordion:execute="#result = shouldGetUrnsFromInventory(#ti, #archive, #idx)">
<tr><th concordion:set="#idx">Index</th><th concordion:assertEquals="#result">File path</th></tr>
<tr><td>0</td><td>urn:cts:greekLit:tlg0012.tlg001.msA</td></tr>
<tr><td>1</td><td>urn:cts:greekLit:tlg0012.tlg001.msB</td></tr>
</table>


## The archive of files ##

- what xml files appear in the file system

These files are found in the file system:


<table concordion:execute="#result = shouldGetFilesOnDisk(#ti, #archive, #idx)">
<tr><th concordion:set="#idx">Index</th><th concordion:assertEquals="#result">File path</th></tr>

<tr><td>0</td><td>A_Iliad_p5uc.xml</td></tr>
<tr><td>1</td><td>tier2/B_Iliad_p5.xml</td></tr>
</table>


## Inspecting contents of the corpus ##

- `filesInArchive()`: lists all XML files contained within `c.baseDirectory` and its subdirectories (recursively)
- `filesInInventory()`: lists all file names appearing in `online@docname` values in the text inventory.
- `urnsInInventory()`:  lists CTS URNs for all texts identified as "online" in the text inventory.

## Verifying the contents of the corpus ##

- `validateInventory()`: validates the text inventory against the published RNG schema.
- `filesAndInventoryMatch()`:  true if there is a 1-1 match of files in the text inventory and in the archival XML files.
- `inventoriedMissingFromArchive()`: lists documents marked in the corpus text inventory as online but not appearing in the archive.
- `filesMissingFromInventory()`:  lists `.xml` files in the archive lacking a corresponding "online" entry in the corpus TextInventory.