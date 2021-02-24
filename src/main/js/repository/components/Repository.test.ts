import { nameWithoutNamespace } from "./Repository";

test("nameWithoutNamespace", () => {
  expect(nameWithoutNamespace("tricia")).toBe("tricia");
  expect(nameWithoutNamespace("hitchhiker/tricia")).toBe("tricia");
  expect(nameWithoutNamespace("hitchhiker/heartOfGold/tricia")).toBe("tricia");
});
