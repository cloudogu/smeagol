import { IdUtil } from "./idUtil";

function idsFor(...texts: string[]): string[] {
  const idUtil = new IdUtil();
  return texts.map((text) => idUtil.nextId(text));
}

describe("IdUtil", () => {
  it("should replace whitespace with a dash", () => {
    expect(idsFor("Some  Heading\ttext")).toEqual(["Some-Heading-text"]);
  });

  it("should append a counter to duplicate texts", () => {
    expect(idsFor("Intro", "Intro", "Intro")).toEqual(["Intro", "Intro-1", "Intro-2"]);
  });

  it("should not change ids of texts which do not collide", () => {
    expect(idsFor("hau", "hau", "hau 2")).toEqual(["hau", "hau-1", "hau-2"]);
  });

  it("should keep the id unique when a text equals a counted duplicate", () => {
    expect(idsFor("hau", "hau", "hau", "hau 2")).toEqual(["hau", "hau-1", "hau-2", "hau-2-1"]);
  });

  it("should keep the id unique when a counted duplicate equals an earlier text", () => {
    expect(idsFor("hau 2", "hau", "hau", "hau")).toEqual(["hau-2", "hau", "hau-1", "hau-3"]);
    expect(idsFor("hau 1", "hau", "hau")).toEqual(["hau-1", "hau", "hau-2"]);
  });

  it("should count independently per instance", () => {
    expect(idsFor("Intro")).toEqual(["Intro"]);
    expect(idsFor("Intro")).toEqual(["Intro"]);
  });
});
