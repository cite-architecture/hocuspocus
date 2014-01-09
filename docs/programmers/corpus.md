# The `Corpus` object #

At the center of the `hocuspocus` library is the `Corpus` object, representing a corpus of citable texts.  Construct it with two files:  a CTS TextInventory, and the root directory of the XML texts.  E.g., 

    Corpus c = new Corpus(new File("textInventory.xml"), new File("xmlDirectory"))

These will now be available as `c.inventory` and `c.baseDirectory`.  Several methods allow you to inspect and verify the contents of your digital corpus.

## Inspecting contents of the corpus ##

- `filesInArchive()`: lists all XML files contained within `c.baseDirectory` and its subdirectories (recursively)
- `filesInInventory()`: lists all file names appearing in `online@docname` values in the text inventory.
- `urnsInInventory()`:  lists CTS URNs for all texts identified as "online" in the text inventory.

## Verifying the contents of the corpus ##

- `validateInventory()`: validates the text inventory against the published RNG schema.
- `filesAndInventoryMatch()`:  true if there is a 1-1 match of files in the text inventory and in the archival XML files.
- `inventoriedMissingFromArchive()`: lists documents marked in the corpus text inventory as online but not appearing in the archive.
- `filesMissingFromInventory()`:  lists `.xml` files in the archive lacking a corresponding "online" entry in the corpus TextInventory.

## Generating secondary data sets from a corpus ##

- ` tabulateRepository(java.io.File outputDir)` :  converts all XML source files in the inventory to OHCO2-equivalent tabular format, and writes output to `outputDir`.
- `tokenizeRepository(TokenizationSystem tokenSystem, java.io.File outputDir)`:  creates a two-column text file where each line is comprised of a token (identified by CTS URN, including subreference), and a type. Both the token and the value of the type depend on the tokenization system selected.
-  `turtleizeRepository(java.io.File outputDir)`:  generates a representation of the entire edited corpus in a single TTL file in `outputDir`.
-  **tokenized editions**:  currently available for individual documents using the `EditionGenerator` class.  A repository-wide method will be added to the `Corpus` class.
