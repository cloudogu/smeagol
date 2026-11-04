import {
  createTableOfContentsList,
  renderInlineTableOfContents,
  setIdsOnHeadlines,
  transformGollumTocMacro
} from "./InlineTableOfContents";

function container(html: string): HTMLDivElement {
  const div = document.createElement("div");
  div.innerHTML = html;
  return div;
}

// compact representation of a table of contents list: "text" for an item, [...] for a nested list
function outline(list: Element): any[] {
  return Array.from(list.children).map((item) => {
    const link = item.querySelector(":scope > a");
    const nested = item.querySelector(":scope > ul");
    const entry: any[] = link ? [`${link.textContent}->${link.getAttribute("href")}`] : [];
    return nested ? [...entry, outline(nested)] : entry[0];
  });
}

describe("transformGollumTocMacro", () => {
  it("should rewrite gollum's macro on its own line", () => {
    expect(transformGollumTocMacro("[[_TOC_]]\n\n# A")).toBe("[[TOC]]\n\n# A");
    expect(transformGollumTocMacro("intro\n  [[_TOC_]] \nrest")).toBe("intro\n  [[TOC]] \nrest");
  });

  it("should leave inline occurrences and code spans untouched", () => {
    expect(transformGollumTocMacro("see `[[_TOC_]]` here")).toBe("see `[[_TOC_]]` here");
    expect(transformGollumTocMacro("text [[_TOC_]]")).toBe("text [[_TOC_]]");
  });
});

describe("setIdsOnHeadlines", () => {
  it("should set unique ids based on the heading text", () => {
    const div = container("<h1>Intro</h1><h2>Set up now</h2><h2>Intro</h2>");
    setIdsOnHeadlines(div);
    expect(Array.from(div.querySelectorAll("h1, h2")).map((h) => h.id)).toEqual(["Intro", "Set-up-now", "Intro-1"]);
  });
});

describe("createTableOfContentsList", () => {
  function listFor(html: string) {
    const div = container(html);
    setIdsOnHeadlines(div);
    const headings = Array.from(div.querySelectorAll("h1, h2, h3, h4, h5, h6")) as HTMLElement[];
    return outline(createTableOfContentsList(document, headings));
  }

  it("should return an empty list without headings", () => {
    expect(listFor("<p>no headings</p>")).toEqual([]);
  });

  it("should nest headings by level", () => {
    expect(listFor("<h1>A</h1><h2>A1</h2><h2>A2</h2><h3>A2a</h3><h1>B</h1>")).toEqual([
      ["A->#A", ["A1->#A1", ["A2->#A2", ["A2a->#A2a"]]]],
      "B->#B"
    ]);
  });

  it("should start at the topmost heading level used on the page", () => {
    expect(listFor("<h2>A</h2><h3>A1</h3><h2>B</h2>")).toEqual([["A->#A", ["A1->#A1"]], "B->#B"]);
  });

  it("should nest skipped heading levels only once", () => {
    expect(listFor("<h1>A</h1><h3>A1</h3><h2>A2</h2>")).toEqual([["A->#A", ["A1->#A1", "A2->#A2"]]]);
  });

  it("should handle a first heading deeper than later ones", () => {
    expect(listFor("<h3>Pre</h3><h2>A</h2>")).toEqual([[["Pre->#Pre"]], "A->#A"]);
  });
});

describe("renderInlineTableOfContents", () => {
  it("should fill every placeholder with a title and the list of headings", () => {
    const div = container(
      '<p><span class="smeagol-inline-toc"></span></p><h1>A</h1><h2>B</h2><p><span class="smeagol-inline-toc"></span></p>'
    );
    setIdsOnHeadlines(div);
    renderInlineTableOfContents(div);

    const placeholders = div.querySelectorAll(".smeagol-inline-toc");
    expect(placeholders).toHaveLength(2);
    placeholders.forEach((placeholder) => {
      expect(placeholder.querySelector(".smeagol-inline-toc-title")).not.toBeNull();
      expect(outline(placeholder.querySelector(":scope > ul"))).toEqual([["A->#A", ["B->#B"]]]);
    });
  });

  it("should rebuild the list instead of appending when called again", () => {
    const div = container('<p><span class="smeagol-inline-toc"></span></p><h1>A</h1>');
    setIdsOnHeadlines(div);
    renderInlineTableOfContents(div);
    div.insertAdjacentHTML("beforeend", "<h1>B</h1>");
    setIdsOnHeadlines(div);
    renderInlineTableOfContents(div);

    const placeholder = div.querySelector(".smeagol-inline-toc");
    expect(placeholder.querySelectorAll(":scope > ul")).toHaveLength(1);
    expect(outline(placeholder.querySelector(":scope > ul"))).toEqual(["A->#A", "B->#B"]);
  });

  it("should do nothing without placeholders", () => {
    const div = container("<h1>A</h1>");
    renderInlineTableOfContents(div);
    expect(div.innerHTML).toBe("<h1>A</h1>");
  });
});
