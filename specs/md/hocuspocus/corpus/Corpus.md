# Validating the corpus #


## Inspecting contents of the corpus ##

- `filesInArchive()`: lists all XML files contained within `c.baseDirectory` and its subdirectories (recursively)
- `filesInInventory()`: lists all file names appearing in `online@docname` values in the text inventory.
- `urnsInInventory()`:  lists CTS URNs for all texts identified as "online" in the text inventory.

## Verifying the contents of the corpus ##

- `validateInventory()`: validates the text inventory against the published RNG schema.
- `filesAndInventoryMatch()`:  true if there is a 1-1 match of files in the text inventory and in the archival XML files.
- `inventoriedMissingFromArchive()`: lists documents marked in the corpus text inventory as online but not appearing in the archive.
- `filesMissingFromInventory()`:  lists `.xml` files in the archive lacking a corresponding "online" entry in the corpus TextInventory.