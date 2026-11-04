import { HTMLConvertorMap } from "@toast-ui/editor";
import { INLINE_TOC_CLASS } from "./InlineTableOfContents";

// "[[TOC]]" is a table of contents macro, not a link to a page. Gollum's "[[_TOC_]]" is rewritten
// to "[[TOC]]" beforehand (see transformGollumTocMacro), "_TOC_" only arrives here when escaped.
const TOC_MACROS = ["TOC", "_TOC_"];

export function transformShortLinks(literal: string): string {
  return literal.replace(/\[\[([^[\]]+)\]\]/g, (match: string, link: string) => {
    if (TOC_MACROS.includes(link.trim())) {
      return `<span class="${INLINE_TOC_CLASS}"></span>`;
    }
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
