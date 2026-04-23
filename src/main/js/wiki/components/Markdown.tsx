import React from "react";
import injectSheet from "react-jss";

import Editor from "@toast-ui/editor";

import colorSyntax from "@toast-ui/editor-plugin-color-syntax";
import chart from "@toast-ui/editor-plugin-chart";
import uml from "@toast-ui/editor-plugin-uml";

import history, { handleHistoryClick } from "./HistoryEditorExtension";
import tableClass from "./TableClassEditorExtension";
import shortLinks from "./ShortLinkEditorExtension";
import legacyPlantuml from "./LegacyPlantumlEditorExtension";

import { IdUtil } from "../../idUtil";

import "@toast-ui/editor/dist/toastui-editor.css";
import "tui-color-picker/dist/tui-color-picker.css";
import "@toast-ui/editor-plugin-color-syntax/dist/toastui-editor-plugin-color-syntax.css";
import "highlight.js/styles/default.css";

const styles = {
  markdown: {
    // makes img elements responsive
    "& img": {
      "max-width": "100%",
      height: "auto",
      display: "block"
    }
  }
};

type Props = {
  content: string;
  classes: any;
};

class Markdown extends React.Component<Props> {
  private editor!: Editor;
  private viewerNode!: HTMLDivElement;

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
  private setIdsOnHeadlines(parentNode: HTMLElement) {
    if (!parentNode) return;
    const idUtil = new IdUtil();
    const elements = parentNode.querySelectorAll("h1, h2, h3, h4, h5, h6");

    elements.forEach((element) => {
      const htmlElement = element as HTMLElement;
      htmlElement.id = idUtil.nextId(htmlElement.innerText);
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
        history,
        tableClass,
        shortLinks,
        legacyPlantuml
      ]
    });

    this.viewerNode.addEventListener("click", handleHistoryClick);
    this.setIdsOnHeadlines(this.viewerNode);
  }

  componentDidUpdate(prevProps: Props) {
    // Only update if content actually changed to avoid unnecessary re-renders
    if (prevProps.content !== this.props.content) {
      this.editor.setMarkdown(this.props.content);
      this.setIdsOnHeadlines(this.viewerNode);
    }
  }

  componentWillUnmount() {
    if (this.viewerNode) {
      this.viewerNode.removeEventListener("click", handleHistoryClick);
    }
    // Clean up editor instance
    if (this.editor) {
      this.editor.destroy();
    }
  }

  render() {
    return (
      <div
        className={this.props.classes.markdown}
        ref={(ref) => {
          if (ref) this.viewerNode = ref;
        }}
      />
    );
  }
}

export default injectSheet(styles)(Markdown);
