// Rewrites both legacy plantuml formats into the "$$uml ... $$" custom block syntax
// that @toast-ui/editor-plugin-uml (v3) renders as an image: "@startuml ...
// @enduml" tags, and the old tui-editor v1 "```uml ... ```" fenced code block.
// The @startuml/@enduml replacements below normalize away any number of already-wrapped
// "$$uml" layers before re-wrapping exactly once, so repeatedly feeding already-transformed
// content back through this function (e.g. an edit/save round-trip) stays idempotent instead
// of accumulating another wrapper on every pass.
export function transformLegacyPlantuml(markdown: string): string {
  return markdown
    .replace(/(?:\$\$uml\n)*@startuml/g, "$$$$uml\n@startuml")
    .replace(/@enduml(?:\n\$\$)*/g, "@enduml\n$$$$")
    .replace(/```\s*uml\r?\n([\s\S]*?)```/gi, (match, content) => `$$uml\n${content}$$`);
}
