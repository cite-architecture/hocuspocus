#hocuspocus, a system for text corpus management#

##Overview ##

Most generally, a text corpus management system stores texts in their archival format, and operates on those archival texts to prepare standard kinds of output.

### Archival requirements ###

- Versioned texts are archived in XML validating against a specified schema.
- A catalog (or text inventory) documents each text's base language, schema, and the mapping of its citation scheme to markup in the specified schema.
- Individual texts may belong to text collections.
- All reference to texts or passages of texts is implemented using CTS URN notation.

## Publishing archival resources ##

In traditional collections of print or other physical resources, archives can store unpublished material. Digital resources, on the other hand, must be published in various formats, including their underlying archival format.  The two most fundamental forms of access to archived texts are:

- publication of the archival artifact,
- export of the text for use in a Canonical Text Service.

Publishing and managing dependencies on texts in archival formats is no different than publishing and managing code or documentation libraries, and can be implemented with a repository manager.  The simplest form of export for use in a CTS is the tabular data format supported by the CHS implementation of Canonical Text Services.   We can implement the two requirements above as:

- create a repository artifact of raw XML and upload to a Nexus repository
- convert xml text to CHS tabular representation, and upload to a Nexus repository


## Other operations ##

How the texts in a corpus should be further processed depends both on the content (including its language and markup schema), and the intended uses of the processed data.  Many batch processing operations therefore are best left up to specific applications, but some are so generic that they are worth building into a corpus management system.  Those generic operations include:

* tokenizing
* classifying tokens
* stemming or morphological analysis of tokens


### Tokenizing ##

Requirements are:

- Based on the language identifier for the text, tokenize each citation unit by words.  
- If the text is marked up in a supported schema, its XML markup can additionally be considered in identifying word units.
- The resulting artifact is an ordered list of CTS URN references that can be uploaded to a Nexus repository.

### Token classification ##

Based on the language identifier for the text, tokens should be classified.  For Greek and Latin texts, classification categories should include:

* named entity
* numeric string
* labeling string (such as labels referring to figures in a mathematical diagram)
* "word as word":  a literally quoted string within the text
* parsed string


### Stemming ###
All parsed string tokens should be mapped to one or more possible lexical entity identifiers.  These lexical entities should be referred to by a CITE Object URN identifying the lexical entity within a collection of entities for a given language.  The end result of stemming therefore will be a mapping of CTS URNs to CITE Object URNs.

### Named Entity Recognition ###

While recognition of named entities can take be incorporated into language-specific token classification, disambiguation of named entities may be best left for later processing that might approach disambiguation differently dependent on specific contexts within a large corpus.

## Text formatting ##

For supported schemas, the system should also support export of individual texts or schemas as:

- epub
- pdf
- web site
- MultiMarkdown


