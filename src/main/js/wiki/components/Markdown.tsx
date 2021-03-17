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
  readonly idsCountByName = new Map<string, number>();

  getCount(name: string): number {
    if (!this.idsCountByName.has(name)) {
      return 0;
    } else return this.idsCountByName.get(name);
  }

  count(name: string) {
    const newCount = this.getCount(name) + 1;
    this.idsCountByName.set(name, newCount);
  }

  setClasses(base: any, depth = 1) {
    const elements = Array.prototype.slice.call(base.getElementsByTagName("h" + depth));
    elements.forEach((element) => {
      this.setClasses(element.parentElement, depth + 1);
      element.className = this.props.classes.toAddId;
    });
  }

  setIds() {
    const elements = Array.prototype.slice.call(this.viewerNode.getElementsByClassName(this.props.classes.toAddId));
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

    this.setClasses(this.viewerNode);
    this.setIds();
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
