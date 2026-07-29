import { HTMLConvertorMap } from "@toast-ui/editor";

export function transformShortLinks(literal: string): string {
  return literal.replace(/\[\[([^[\]]+)\]\]/g, (match: string, link: string) => {
    return `<a href="${link}">${link}</a>`;
  });
}

export default function shortLinksPlugin() {
  return {
    toHTMLRenderers: {
      text(node: any, context: any) {
        const { literal } = node;
        const transformed = transformShortLinks(literal);

        if (transformed !== literal) {
          return {
            type: "html",
            content: transformed
          };
        }

        return context.origin();
      }
    } as HTMLConvertorMap
  };
}
