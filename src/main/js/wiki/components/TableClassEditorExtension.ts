import { HTMLConvertorMap } from "@toast-ui/editor";

export default function tableClassPlugin() {
  return {
    toHTMLRenderers: {
      table(node: any, context: any) {
        const { origin } = context;
        const html = origin();

        if (!html.attributes) {
          html.attributes = {};
        }

        html.attributes = {
          ...html.attributes,
          class: html.attributes.class ? `${html.attributes.class} table` : "table"
        };

        return html;
      }
    } as HTMLConvertorMap
  };
}
