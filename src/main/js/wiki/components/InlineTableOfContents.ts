import i18next from "i18next";
import { IdUtil } from "../../idUtil";

import "../styles/inline-table-of-contents.css";

/**
 * CSS class of the placeholder element which the short link extension renders for a
 * table of contents macro ("[[TOC]]" or gollum's "[[_TOC_]]").
 */
export const INLINE_TOC_CLASS = "smeagol-inline-toc";

const HEADING_SELECTOR = "h1, h2, h3, h4, h5, h6";

/**
 * Rewrites gollum's "[[_TOC_]]" macro into "[[TOC]]".
 */
export function transformGollumTocMacro(markdown: string): string {
  return markdown.replace(/^([ \t]*)\[\[_TOC_\]\]([ \t]*)$/gm, "$1[[TOC]]$2");
}

function rawHeadingText(heading: HTMLElement): string {
  // jsdom does not implement innerText, fall back to textContent there
  return heading.innerText ?? heading.textContent ?? "";
}

function headingText(heading: HTMLElement): string {
  return rawHeadingText(heading).trim();
}

/**
 * Adds ids to any tag of "h1, h2, h3, h4, h5, h6" based on their content.
 * Spaces in id are replaced with a '-'.
 * If there is exactly the same content twice or more, a counter is applied to the id.
 *
 * @param parentNode The html element in which the tags should be searched.
 */
export function setIdsOnHeadlines(parentNode: HTMLElement) {
  if (!parentNode) return;
  const idUtil = new IdUtil();
  parentNode.querySelectorAll(HEADING_SELECTOR).forEach((element) => {
    const htmlElement = element as HTMLElement;
    // untrimmed on purpose: keeps the ids (and existing links to them) stable
    htmlElement.id = idUtil.nextId(rawHeadingText(htmlElement));
  });
}

/**
 * Builds a nested list of links to the given headings. The topmost heading level found on the page
 * becomes the first list level; skipped heading levels result in a single level of nesting.
 */
export function createTableOfContentsList(document: Document, headings: HTMLElement[]): HTMLUListElement {
  const root = document.createElement("ul");
  if (headings.length === 0) {
    return root;
  }

  const levelOf = (heading: HTMLElement) => Number(heading.tagName.substring(1));
  const baseLevel = Math.min(...headings.map(levelOf));
  const stack: Array<{ level: number; list: HTMLUListElement }> = [{ level: baseLevel, list: root }];

  headings.forEach((heading) => {
    const level = levelOf(heading);

    while (stack.length > 1 && stack[stack.length - 1].level > level) {
      stack.pop();
    }

    let current = stack[stack.length - 1];
    if (level > current.level) {
      const lastItem = current.list.lastElementChild;
      // reuse the nested list of a deeper, skipped level (e.g. h3 below h1) for a following h2
      let nested = lastItem?.querySelector(":scope > ul") as HTMLUListElement | null;
      if (!nested) {
        nested = document.createElement("ul");
        if (lastItem) {
          lastItem.appendChild(nested);
        } else {
          // the first heading is deeper than the base level, e.g. h3 before the first h2
          const wrapper = document.createElement("li");
          wrapper.appendChild(nested);
          current.list.appendChild(wrapper);
        }
      }
      current = { level, list: nested };
      stack.push(current);
    }

    const link = document.createElement("a");
    link.href = `#${heading.id}`;
    link.textContent = headingText(heading);

    const item = document.createElement("li");
    item.appendChild(link);
    current.list.appendChild(item);
  });

  return root;
}

/**
 * Fills every table of contents placeholder inside the container with a nested list of links to the
 * container's headings. The headings need ids (see {@link setIdsOnHeadlines}) for the links to work.
 * Calling this function again rebuilds the lists, so it can be used after every (re-)render.
 *
 * @param container The html element containing the rendered markdown.
 */
export function renderInlineTableOfContents(container: HTMLElement) {
  if (!container) return;
  const placeholders = container.querySelectorAll(`.${INLINE_TOC_CLASS}`);
  if (placeholders.length === 0) return;

  const document = container.ownerDocument;
  const headings = Array.from(container.querySelectorAll(HEADING_SELECTOR)) as HTMLElement[];

  placeholders.forEach((placeholder) => {
    const title = document.createElement("span");
    title.className = `${INLINE_TOC_CLASS}-title`;
    title.textContent = i18next.t("table-of-contents");

    placeholder.replaceChildren(title, createTableOfContentsList(document, headings));
  });
}
