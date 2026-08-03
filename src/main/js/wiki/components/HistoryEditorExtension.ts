import { HTMLConvertorMap } from "@toast-ui/editor";

export default function historyPlugin() {
  return {
    toHTMLRenderers: {
      link(node: any, context: any) {
        const { origin } = context;
        const { destination } = node;
        const html = origin();
        const isLocal = !(destination.startsWith("/") || destination.indexOf("://") > 0);

        if (isLocal) {
          html.attributes = {
            ...html.attributes,
            "data-history": "true"
          };
        }

        return html;
      }
    } as HTMLConvertorMap
  };
}

export const handleHistoryClick = (e: MouseEvent) => {
  const target = e.target as HTMLElement;
  const link = target.closest("a[data-history]");
  if (link) {
    e.preventDefault();
    const href = link.getAttribute("href");
    if (href) {
      (window as any).appHistory.push(href, { some: "thing" });
    }
  }
};
