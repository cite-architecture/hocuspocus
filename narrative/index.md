---
layout: page
title: Brief overview of the hocuspocus library for programmers
---


A corpus of citable texts must include:

1. editions of the texts
2. cataloging information, including how texts are canonically identified and cited

`hocuspocus` represents a corpus with the `Corpus` class.

A typical situation is that editors work with files in a local file system.  Editions of texts and the cataloging information are both typically structured in XML.  Editions may use any XML vocabulary in any namespace (or in no namespace).  XML files of cataloging information validate against the `TextInventory.rng` schema defined in [the `cite` library](http://cite-architecture.github.io/cite/).

`hocuspocus` maintains the configuration data needed to work with local files in a `CitationConfigurationFileReader`.  This includes information about where to find files in the file system, and how the XML markup of individual files is related to canonical citation schemes.  The constructor for a `Corpus` object therefore includes a local XML file with citation configuration data, as well as an XML TextInventory and a base directory for finding XML editions.

The `Corpus` object  includes two specially useful high-level methods.  The `tabulateRepository`  loops through all texts in a corpus and creates tabular files from local XML files.  The `turtleizeRepository` method first tabulates all texts in a corpus, then composes an OHCO-2 equivalent represenation in TTL.

To convert a corpus of local XML files to tabular files, the `Corpus` object uses a `Tabulator` object.  Its `tabulateFile` method can convert a single XML file to tabular format.

To convert tabular files to RDF, the `Corpus` object uses a `CtsTtl` object.  Its `turtleizeFile` method converts a single file of tabular data to RDF.
