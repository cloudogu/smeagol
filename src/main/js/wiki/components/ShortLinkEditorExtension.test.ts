import { transformShortLinks } from "./ShortLinkEditorExtension";

describe("transformShortLinks", () => {
  it("should transform short links into links", () => {
    expect(transformShortLinks("see [[Docs/Setup]]")).toBe('see <a href="Docs/Setup">Docs/Setup</a>');
  });

  it("should leave text without short links untouched", () => {
    expect(transformShortLinks("plain [text]")).toBe("plain [text]");
  });

  it("should transform the table of contents macros into a placeholder", () => {
    const placeholder = '<span class="smeagol-inline-toc"></span>';
    expect(transformShortLinks("[[TOC]]")).toBe(placeholder);
    expect(transformShortLinks("[[_TOC_]]")).toBe(placeholder);
    expect(transformShortLinks("[[ TOC ]]")).toBe(placeholder);
  });

  it("should still transform links next to a table of contents macro", () => {
    expect(transformShortLinks("[[TOC]] [[Home]]")).toBe(
      '<span class="smeagol-inline-toc"></span> <a href="Home">Home</a>'
    );
  });

  it("should treat other spellings as page links", () => {
    expect(transformShortLinks("[[TOCs]]")).toBe('<a href="TOCs">TOCs</a>');
  });
});
