import React from "react";
import RepositoryGroup from "./RepositoryGroup";

type Props = {
  repositories: any;
};

// @VisibleForTesting
export function createRepositoryGroups(repositories: any) {
  const groups = {};
  for (const repository of repositories) {
    const name = repository.name;

    let groupName = "main";
    const index = name.lastIndexOf("/");
    if (index > 0) {
      groupName = name.substring(0, index);
    }

    let group = groups[groupName];
    if (!group) {
      group = {
        name: groupName,
        repositories: []
      };
      groups[groupName] = group;
    }
    group.repositories.push(repository);
  }

  const groupArray = [];
  for (const groupName in groups) {
    groupArray.push(groups[groupName]);
  }
  groupArray.sort(function (a, b) {
    if (a.name === "main" && b.name !== "main") {
      return 10;
    } else if (a.name !== "main" && b.name === "main") {
      return -10;
    } else if (a.name < b.name) {
      return -1;
    } else if (a.name > b.name) {
      return 1;
    }
    return 0;
  });

  return groupArray;
}

class RepositoryList extends React.Component<Props> {
  render() {
    const { repositories } = this.props;

    const groups = createRepositoryGroups(repositories);

    return (
      <div className="list-group">
        {groups.map((group) => {
          return <RepositoryGroup key={group.name} group={group} />;
        })}
      </div>
    );
  }
}

export default RepositoryList;
