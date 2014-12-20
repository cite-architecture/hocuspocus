# Constructing and validating the corpus #

An archival corpus is made of text files and an inventory documenting the citable structure them.  For a given corpus, we can determine:

- what files are cataloged in the inventory
- what URNs are cataloged in the inventory
- what xml files appear in the file system


### Examples ###


We can use <a href="../../../specs/data/archive1/testinventory.xml" concordion:set="#ti = setHref(#HREF)">this TextInventory file</a> with
files in <a href="../../../specs/data/archive1/xml" concordion:set="#archive = setHref(#HREF)">this root directory</a> to <strong concordion:assertTrue="shouldMakeCorpus(#ti,#archive)">construct a Corpus</strong>.

## Inspecting contents of the corpus ##

- `filesInArchive()`: lists all XML files contained within `c.baseDirectory` and its subdirectories (recursively)
- `filesInInventory()`: lists all file names appearing in `online@docname` values in the text inventory.
- `urnsInInventory()`:  lists CTS URNs for all texts identified as "online" in the text inventory.

## Verifying the contents of the corpus ##

- `validateInventory()`: validates the text inventory against the published RNG schema.
- `filesAndInventoryMatch()`:  true if there is a 1-1 match of files in the text inventory and in the archival XML files.
- `inventoriedMissingFromArchive()`: lists documents marked in the corpus text inventory as online but not appearing in the archive.
- `filesMissingFromInventory()`:  lists `.xml` files in the archive lacking a corresponding "online" entry in the corpus TextInventory.