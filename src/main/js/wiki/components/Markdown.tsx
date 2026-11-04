import React from "react";

import Editor from "@toast-ui/editor";
import Viewer from "@toast-ui/editor/dist/toastui-editor-viewer";

import colorSyntax from "@toast-ui/editor-plugin-color-syntax";
import chart from "@toast-ui/editor-plugin-chart";
import uml from "@toast-ui/editor-plugin-uml";

import history, { handleHistoryClick } from "./HistoryEditorExtension";
import tableClass from "./TableClassEditorExtension";
import shortLinks from "./ShortLinkEditorExtension";
import { transformLegacyPlantuml } from "./LegacyPlantumlEditorExtension";
import readOnlyTaskList from "./ReadOnlyTaskListExtension";
import { renderInlineTableOfContents, setIdsOnHeadlines, transformGollumTocMacro } from "./InlineTableOfContents";

type Props = {
  content: string;
  classes: any;
};

class Markdown extends React.Component<Props> {
  private viewer!: Viewer;
  private viewerNode!: HTMLDivElement;

  /**
   * The toast-ui viewer does not add ids to the headlines, but they are necessary to scroll to a
   * headline from the table of contents (sidebar and "[[TOC]]" macro).
   */
  private decorateRenderedContent() {
    setIdsOnHeadlines(this.viewerNode);
    renderInlineTableOfContents(this.viewerNode);
  }

  componentDidMount() {
    this.viewer = Editor.factory({
      el: this.viewerNode,
      viewer: true,
      initialValue: transformGollumTocMacro(transformLegacyPlantuml(this.props.content)),
      usageStatistics: false,
      plugins: [
        colorSyntax,
        chart,
        [uml, { rendererURL: process.env.PLANTUML_RENDERER_URL || "/plantuml/png/" }],
        history,
        tableClass,
        shortLinks,
        readOnlyTaskList
      ]
    });

    this.viewerNode.addEventListener("click", handleHistoryClick);
    this.decorateRenderedContent();
  }

  componentDidUpdate(prevProps: Props) {
    // Only update if content actually changed to avoid unnecessary re-renders
    if (prevProps.content !== this.props.content) {
      this.viewer.setMarkdown(transformGollumTocMacro(transformLegacyPlantuml(this.props.content)));
      this.decorateRenderedContent();
    }
  }

  componentWillUnmount() {
    if (this.viewerNode) {
      this.viewerNode.removeEventListener("click", handleHistoryClick);
    }
    // Clean up editor instance
    if (this.viewer) {
      this.viewer.destroy();
    }
  }

  render() {
    return (
      <div
        ref={(ref) => {
          if (ref) this.viewerNode = ref;
        }}
      />
    );
  }
}

export default Markdown;
