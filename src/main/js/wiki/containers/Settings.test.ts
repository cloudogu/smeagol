import { findDirectoryPath } from "./Settings";

it("should return 4th part of the URL", () => {
  expect(findDirectoryPath("https://server.com/smeagol/docs/home")).toBe("docs/home");
});
