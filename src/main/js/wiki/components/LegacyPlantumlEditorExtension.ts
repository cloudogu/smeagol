import { HTMLConvertorMap } from "@toast-ui/editor";

export default function legacyPlantumlPlugin() {
  return {
    toHTMLRenderers: {
      text(node: any, context: any) {
        const { literal } = node;
        const startUmlRe = /@startuml/g;
        const endUmlRe = /@enduml/g;

        if (startUmlRe.test(literal) || endUmlRe.test(literal)) {
          const transformed = literal.replace(startUmlRe, "```uml").replace(endUmlRe, "```");

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
