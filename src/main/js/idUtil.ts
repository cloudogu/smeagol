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
   * Creates an id based on the raw text.
   * Increases the counter of that text.
   *
   * @param rawText The text of a headline.
   */
  public nextId(rawText: string) {
    const text = rawText.replace(/\s+/g, this.blankSpaceReplaceText);
    this.count(text);
    const count = this.getCount(text);

    if (count == 0) {
      return text;
    }

    return text + this.blankSpaceReplaceText + count;
  }
}
