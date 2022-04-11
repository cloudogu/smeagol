import React from "react";
import injectSheet from "react-jss";
import Branch from "./Branch";
import BackToRepositoriesButton from "../../BackToRepositoriesButton";

const styles = {};

type Props = {
  repository: any;
  classes: any;
};

//@VisibleForTesting
// master should always be the first one,
// followed by develop the rest should be ordered by its name
export function orderBranches(branches) {
  branches.sort((a, b) => {
    if (a.name === "main" && b.name !== "main") {
      return -20;
    } else if (a.name !== "main" && b.name === "main") {
      return 20;
    } else if (a.name === "master" && b.name !== "master") {
      return -10;
    } else if (a.name !== "master" && b.name === "master") {
      return 10;
    } else if (a.name === "develop" && b.name !== "develop") {
      return -5;
    } else if (a.name !== "develop" && b.name === "develop") {
      return 5;
    } else if (a.name < b.name) {
      return -1;
    } else if (a.name > b.name) {
      return 1;
    }
    return 0;
  });
}

class BranchOverview extends React.Component<Props> {
  render() {
    const { repository } = this.props;
    if (!repository) {
      return <div />;
    }

    let branches = repository._embedded.branches._embedded.branchResourceList;
    if (!branches) {
      branches = [];
    }

    orderBranches(branches);

    return (
      <div>
        <div className="list-group">
          {branches.map((branch) => {
            return <Branch key={branch.name} branch={branch} />;
          })}
        </div>
        <BackToRepositoriesButton />
      </div>
    );
  }
}

export default injectSheet(styles)(BranchOverview);
