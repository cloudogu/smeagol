import { HTMLConvertorMap } from "@toast-ui/editor";

export default function shortLinksPlugin() {
  return {
    toHTMLRenderers: {
      text(node: any, context: any) {
        const { literal } = node;
        const linkHtmlRx = /\[\[([^[\]]+)\]\]/g;

        if (linkHtmlRx.test(literal)) {
          return {
            type: "html",
            content: literal.replace(linkHtmlRx, (match: string, link: string) => {
              return `<a href="${link}">${link}</a>`;
            })
          };
        }

        return context.origin();
      }
    } as HTMLConvertorMap
  };
}
