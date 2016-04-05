---
layout: page
title: Brief overview
---


A corpus of citable texts must include:

1. editions of the texts
2. cataloging information, including how texts are canonically identified and cited

`hocuspocus` represents a corpus with the `Corpus` class.

A typical situation is that editors work with files in a local file system.  Editions of texts and the cataloging information are both typically structured in XML.  Editions may use any XML vocabulary in any namespace (or in no namespace).  XML files of cataloging information validate against the `TextInventory.rng` schema defined in [the `cite` library](http://cite-architecture.github.io/cite/).

`hocuspocus` maintains the configuration data needed to work with local files in a `CitationConfigurationFileReader`.  This includes information about where to find files in the file system, and how the XML markup of individual files is related to canonical citation schemes.  The constructor for a `Corpus` object therefore includes a local XML file with citation configuration data, as well as an XML TextInventory and a base directory for finding XML editions.

In the current implementation of the `hocuspocus` library, the `Corpus` object  already includes a high-level method `tabulateRepository` that loops through all editions in a corpus and creates tabular files from local XML files.  In addition, we are developing a high-level methods for converting an entire corpus from local tabular files to an  OHCO2-equivalent graph representation in RDF.

In its current state, the `hocuspocus` library includes classes that perform these tasks, although they have not yet been abstracted in higher-level methods of the  `Corpus` object.

To convert a corpus of local XML files to tabular files, the `Corpus` object uses a `Tabulator` object.  Its `tabulateFile` method converts a single XML file to tabular format.

To convert tabular files to RDF, you (and, ultimately, the `Corpus` object) can use a `CtsTtl` object.  Its `turtleizeTabs` method converts a single file of tabular data to RDF.

In the next release of `hocuspocus`, the `Corpus` object we offer a `turtleizeRepository` method in parallel to the current `tabulateRepository` method.
