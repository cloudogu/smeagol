import { orderRepositoriesByName } from "./RepositoryGroup";

test("orderRepositoriesByName", () => {
  const repositories = [
    {
      name: "different/two"
    },
    {
      name: "different/one"
    },
    {
      name: "different/three"
    },
    {
      name: "different/four"
    }
  ];

  orderRepositoriesByName(repositories);

  let i = 0;
  expect(repositories[i++].name).toBe("different/four");
  expect(repositories[i++].name).toBe("different/one");
  expect(repositories[i++].name).toBe("different/three");
  expect(repositories[i++].name).toBe("different/two");
});
