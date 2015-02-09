---
title:  Programmer's guide to the `hocuspocus` library
layout: page
---

Most of the functionality of individual classes in the `hocuspocus` library is available through the `Corpus` class.  This document offers an overview of the library's functionality;  the accompanying file `corpus.md` provides a guide to using the `Corpus` class. Further details for all classes can be found in the API documentation (which can be generated with `gradle groovydoc`).  The unit tests also provide illustrative examples.

## Overview ##

The `hocuspocus` library operates on an archive of XML texts documented in a CTS TextInventory.  The functionality it provides includes:

- converting canonically citable XML texts to a tabular format equivalent under OHCO2.
- tokenizing editions using a specified tokenization system
- generating RDF statements representing:
    - an OHCO2-complete representation of the entire edited corpus using the Homer Multitext project's RDF vocabulary
    - a tokenization of the corpus mapping tokens to occcurrences cited with canonical CTS URNs
    - automatically generated editions citable by tokenized units




