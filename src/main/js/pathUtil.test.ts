import { pathWithTrailingSlash } from "./pathUtil";

test("test pathWithTrailingSlash for path with slash", () => {
  const path = "foo/";
  const newPath = pathWithTrailingSlash(path);

  expect(newPath).toBe(path);
});

test("test pathWithTrailingSlash for path without slash", () => {
  const path = "foo/baa";
  const newPath = pathWithTrailingSlash(path);

  expect(newPath).toBe(path + "/");
});
