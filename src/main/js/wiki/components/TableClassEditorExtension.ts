import Editor from "tui-editor/dist/tui-editor-Editor";

Editor.defineExtension("tableClass", function (editor) {
  const tableHtmlRx = /<table/g;

  editor.eventManager.listen("convertorAfterMarkdownToHtmlConverted", (html) => {
    return html.replace(tableHtmlRx, "<table class='table'");
  });
});
