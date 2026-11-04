/**
 * This class is used to generate unique ids based on text input.
 * The occurrences of a text will be counted and the id will be
 * a combination of the text input and the count.
 */
export class IdUtil {
  public readonly blankSpaceReplaceText = "-";
  /**
   * Used to count the occurrences of exactly the same headline
   */
  readonly countById = new Map<string, number>();

  /**
   * All ids returned by nextId so far
   */
  readonly usedIds = new Set<string>();

  /**
   * Returns the current count of headlines with the given id which were counted with the count method.
   * @see count
   * @param id The id of the headline
   */
  public getCount(id: string): number {
    if (!this.countById.has(id)) {
      return 0;
    } else return this.countById.get(id);
  }

  /**
   * Increases the counter for a given id by one.
   * @see getCount
   * @param id The id to increase the counter for
   */
  public count(id: string) {
    const newCount = this.getCount(id) + 1;
    this.countById.set(id, newCount);
  }

  /**
   * Creates a unique id based on the raw text.
   * Increases the counter of that text.
   *
   * The counter suffix can produce an id which another text already got.
   * In this case the counter is increased until the id is unique,
   * so only the later of the two headlines gets a different id.
   *
   * @param rawText The text of a headline.
   */
  public nextId(rawText: string) {
    const text = rawText.replace(/\s+/g, this.blankSpaceReplaceText);

    let id: string;
    do {
      const count = this.getCount(text);
      this.count(text);
      id = count == 0 ? text : text + this.blankSpaceReplaceText + count;
    } while (this.usedIds.has(id));

    this.usedIds.add(id);
    return id;
  }
}
