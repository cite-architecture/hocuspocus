package edu.holycross.shot.hocuspocus


public enum NodeFormat {




  XML("xml"),
  MARKDOWN("markdown"),
  PLAIN_TEXT("text")

    private String label
    private NodeFormat(String label) {
      this.label = label
    }

    /** Gets a human-readable label for this value. */
    public String getLabel() {
      return label
    }

    /** Looks up value by labelling String. */
    static getByLabel(String labelStr) {
      values().find { it.label == labelStr }
    }
}
