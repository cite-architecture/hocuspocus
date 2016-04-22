---
layout: page
title: hocuspocus
---


## What is it?

`hocuspocus` is a system for managing a corpus of citable texts.  `hocuspocus` follows the [OHCO2](http://cite-architecture.github.io/ohco2/) abstract model of texts, and uses [CTS URNs](http://cite-architecture.github.io/ctsurn/) to identify passages of text.  `hocuspocus` uses the Canonical Text Service's TextInventory to catalog texts in a corpus, and can generate OHCO2-equivalent representations of texts in different forms, as summarized here:


| From                                      | To                     |
|:------------------------------------------|:-----------------------|
| arbitrary XML  files in local file system | tabular representation |
| tabular representation                    | RDF graph in TTL       |


See a brief [overview for programmers of the `hocuspocus` library](narrative).

## Current status

- version: **1.1.0**.  (See [release notes](releases).)
- requires: java version >= 7
- maven identifiers: group `edu.holycross.shot`, package `hocuspocus`, available from the repository at <http://beta.hpcc.uh.edu/nexus/content/groups/public>



## Further documentation

- [current API documentation](api).


Specifications for the library are being written using [concordion](http://concordion.org) to specify tests.  These specifications are **not** current with the version 1.+ release series.  For now, users of `hocuspocus` should instead consult the brief [overview of the hocuspocus library for programmers](narrative).
