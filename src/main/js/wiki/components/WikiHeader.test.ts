import { getName, getNamespace } from "./WikiHeader";

it("should return namespace", () => {
  expect(getNamespace("namespace/repoA") === "namespace").toBeTruthy();
  expect(getNamespace("foo/bar") === "foo").toBeTruthy();
  expect(getNamespace("") === "").toBeTruthy();
  expect(getNamespace("wrongformat") === "").toBeTruthy();
  expect(getNamespace("wrong/for/mat") === "").toBeTruthy();
});

it("should return name", () => {
  expect(getName("namespace/repoA") === "repoA").toBeTruthy();
  expect(getName("foo/bar") === "bar").toBeTruthy();
  expect(getName("") === "").toBeTruthy();
  expect(getName("wrongformat") === "").toBeTruthy();
  expect(getName("wrong/for/mat") === "").toBeTruthy();
});
