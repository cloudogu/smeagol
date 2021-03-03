import { orderBranches } from "./BranchOverview";

test("orderBranches", () => {
  const branches = [
    {
      name: "feature/two"
    },
    {
      name: "feature/one"
    },
    {
      name: "master"
    },
    {
      name: "feature/three"
    },
    {
      name: "develop"
    }
  ];

  orderBranches(branches);

  let i = 0;
  expect(branches[i++].name).toBe("master");
  expect(branches[i++].name).toBe("develop");
  expect(branches[i++].name).toBe("feature/one");
  expect(branches[i++].name).toBe("feature/three");
  expect(branches[i++].name).toBe("feature/two");
});
