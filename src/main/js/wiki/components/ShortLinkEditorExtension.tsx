import Editor from "tui-editor/dist/tui-editor-Editor";

Editor.defineExtension("shortlinks", function (editor) {
  const linkHtmlRx = /\[\[([^[\]]+)\]\]/g;

  editor.eventManager.listen("convertorAfterMarkdownToHtmlConverted", (html) => {
    return html.replace(linkHtmlRx, function (match, link) {
      return `<a href="${link}">${link}</a>`;
    });
  });
});
