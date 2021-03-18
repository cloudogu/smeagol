import React from "react";
import injectSheet from "react-jss";

import Editor from "tui-editor/dist/tui-editor-Editor";
import "tui-editor/dist/tui-editor-extTable";
import "tui-editor/dist/tui-editor-extScrollSync";
import "tui-editor/dist/tui-editor-extUML";

import "./TableClassEditorExtension";
import "./HistoryEditorExtension";
import "./LegacyPlantumlEditorExtension";
import "./ShortLinkEditorExtension";

import "codemirror/lib/codemirror.css";

import "highlight.js/lib";
import "highlight.js/styles/default.css";

const styles = {
  markdown: {
    // makes img elements responsive
    "& img": {
      "max-width": "100%",
      height: "auto",
      display: "block"
    }
  },
  toAddId: {}
};

type Props = {
  content: string;
  classes: any;
};

class Markdown extends React.Component<Props> {
  readonly blankSpaceReplaceText = "-";
  /**
   * Used to count the occurrences of exactly the same headline
   */
  readonly countById = new Map<string, number>();

  /**
   * Returns the current count of headlines with the given id which were counted with the count method.
   * @see count
   * @param id The id of the headline
   */
  getCount(id: string): number {
    if (!this.countById.has(id)) {
      return 0;
    } else return this.countById.get(id);
  }

  /**
   * Increases the counter for a given id by one.
   * @see getCount
   * @param id The id to increase the counter for
   */
  count(id: string) {
    const newCount = this.getCount(id) + 1;
    this.countById.set(id, newCount);
  }

  /**
   * Adds ids to any tag of "h1, h2, h3, h4, h5, h6" based on their content.
   * Spaces in id are replaced with a '-'.
   * If there is exactly the same content twice or more, a counter is applied to the id.
   *
   * This is necessary because in the current version of tui editor (1.4.10), there is no way
   * implemented to add ids to the headlines.
   * But ids are necessary to scroll to the headline by using the table of contents.
   *
   * @param parentNode The html element in which the tags should be searched.
   */
  setIdsOnHeadlines(parentNode: any) {
    const elements = Array.prototype.slice.call(parentNode.querySelectorAll("h1, h2, h3, h4, h5, h6"));

    elements.forEach((element) => {
      const text = element.innerText.replace(/\s+/g, this.blankSpaceReplaceText);
      const count = this.getCount(text);

      if (count == 0) {
        element.id = text;
      } else {
        element.id = text + this.blankSpaceReplaceText + count;
      }

      this.count(text);
    });
  }

  componentDidMount() {
    this.editor = new Editor.factory({
      el: this.viewerNode,
      viewer: true,
      initialEditType: "markdown",
      initialValue: this.props.content,
      exts: [
        "colorSyntax",
        { name: "uml", rendererURL: "/plantuml/png/" },
        "chart",
        "mark",
        "table",
        "tableClass",
        "taskCounter",
        "shortlinks",
        "history",
        "legacyplantuml"
      ]
    });

    // After the markdown has been rendered, the ids for the headlines need to be applied.
    this.setIdsOnHeadlines(this.viewerNode);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.content !== this.props.content) {
      this.editor.setMarkdown(this.props.content);
    }
  }

  render() {
    return <div className={this.props.classes.markdown} ref={(ref) => (this.viewerNode = ref)}></div>;
  }
}

export default injectSheet(styles)(Markdown);
