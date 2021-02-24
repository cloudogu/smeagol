import isPageNameValid from "./PageNameValidator";

it("should return true for the paths", () => {
  expect(isPageNameValid("", "abc")).toBeTruthy();
  expect(isPageNameValid("", "some/path")).toBeTruthy();
  expect(isPageNameValid("", "some/deeper/cool/path")).toBeTruthy();
  expect(isPageNameValid("", "Home")).toBeTruthy();
  expect(isPageNameValid("", "Home.page")).toBeTruthy();
  expect(isPageNameValid("", "Home_page")).toBeTruthy();
  expect(isPageNameValid("", "Home page")).toBeTruthy();
  expect(isPageNameValid("", "Home-page")).toBeTruthy();
  expect(isPageNameValid("", "./Home")).toBeTruthy();
  expect(isPageNameValid("", "with0123456789")).toBeTruthy();
});

it("should not contain ..", () => {
  expect(isPageNameValid("", "abc..def")).toBeFalsy();
  expect(isPageNameValid("", "..")).toBeFalsy();
});

it("should not contain //", () => {
  expect(isPageNameValid("", "abc//def")).toBeFalsy();
});

it("should start with /", () => {
  expect(isPageNameValid("", "/def")).toBeFalsy();
});

it('should start with " "', () => {
  expect(isPageNameValid("", " def")).toBeFalsy();
});

it('should end with " "', () => {
  expect(isPageNameValid("", "def ")).toBeFalsy();
});

it("should should not end with /", () => {
  expect(isPageNameValid("", "myPageName/")).toBeFalsy();
});

it("should should not end with .", () => {
  expect(isPageNameValid("", "myPageName.")).toBeFalsy();
});

it("should have at least one char", () => {
  expect(isPageNameValid("", "")).toBeFalsy();
});

it("should not contain illegal chars", () => {
  expect(isPageNameValid("", "abc?def")).toBeFalsy();
  expect(isPageNameValid("", "abc!def")).toBeFalsy();
  expect(isPageNameValid("", "abc&def")).toBeFalsy();
  expect(isPageNameValid("", "abc;def")).toBeFalsy();
  expect(isPageNameValid("", "abc*def")).toBeFalsy();
});

it("should not be eaqual to the initial name", () => {
  expect(isPageNameValid("abc", "abc")).toBeFalsy();
  expect(isPageNameValid("def-def", "def-def")).toBeFalsy();
});
