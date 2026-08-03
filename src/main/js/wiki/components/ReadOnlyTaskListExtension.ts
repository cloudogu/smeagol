import { HTMLConvertorMap } from "@toast-ui/editor";

// The viewer lets users click a task list checkbox, but that only toggles a CSS
// class in the DOM - there is no way to persist the change on a read-only page,
// so clicking it just misleads users into thinking they saved something. Marking
// task items with data-task-disabled makes the viewer's own click handler skip them.
export default function readOnlyTaskListPlugin() {
  return {
    toHTMLRenderers: {
      item(node: any, context: any) {
        const { entering, origin } = context;
        const html = origin();

        if (entering && node.listData && node.listData.task) {
          html.attributes = { ...html.attributes, "data-task-disabled": "" };
        }

        return html;
      }
    } as HTMLConvertorMap
  };
}
