import React from "react";
import injectSheet from "react-jss";

import Editor from "@toast-ui/editor";

import colorSyntax from "@toast-ui/editor-plugin-color-syntax";
import chart from "@toast-ui/editor-plugin-chart";
import uml from "@toast-ui/editor-plugin-uml";

import "./TableClassEditorExtension";
import "./HistoryEditorExtension";
import "./LegacyPlantumlEditorExtension";
import "./ShortLinkEditorExtension";

import "@toast-ui/editor/dist/toastui-editor.css";
import "tui-color-picker/dist/tui-color-picker.css";
import "@toast-ui/editor-plugin-color-syntax/dist/toastui-editor-plugin-color-syntax.css";

import "highlight.js/lib";
import "highlight.js/styles/default.css";
import { IdUtil } from "../../idUtil";

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
    const idUtil = new IdUtil();
    const elements = Array.prototype.slice.call(parentNode.querySelectorAll("h1, h2, h3, h4, h5, h6"));

    elements.forEach((element) => {
      element.id = idUtil.nextId(element.innerText);
    });
  }

  componentDidMount() {
    this.editor = new Editor({
      el: this.viewerNode,
      viewer: true,
      initialValue: this.props.content,
      usageStatistics: false,
      plugins: [
        colorSyntax,
        chart,
        [uml, { rendererURL: "/plantuml/png/" }],
        // Import your local extensions and add them here
        tableClass,
        shortlinks,
        history,
        legacyplantuml
        // Note: "mark" and "taskCounter" may require specific v3
        // plugin versions if they aren't standard.
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
