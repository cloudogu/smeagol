import Editor from "@toast-ui/editor";

Editor.defineExtension("tableClass", function (editor) {
  const tableHtmlRx = /<table/g;

  editor.eventManager.listen("convertorAfterMarkdownToHtmlConverted", (html) => {
    return html.replace(tableHtmlRx, "<table class='table'");
  });
});
