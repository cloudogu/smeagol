import React from "react";
import injectSheet from "react-jss";
import classNames from "classnames";
import { orderBranches } from "../../repository/components/BranchOverview";
import { Branch } from "../../repository/types/repositoryDto";

const styles = {
  dropdownWrapper: {
    "background-color": "white",
    border: "#777777",
    color: "#777777",
    padding: "6px 12px",
    fontSize: "14px",
    marginLeft: "auto",
    float: "right !important",
    fontFamily: "sans-serif"
  },
  dropdown: {
    maxWidth: "200px",
    textOverflow: "ellipsis",
    border: "1px solid #777777",
    backgroundColor: "transparent",
    borderRadius: "3px",
    fontSize: "14px"
  },
  option: {
    wordBreak: "break-all",
    maxWidth: "200px",
    textOverflow: "ellipsis",
    fontSize: "16px"
  },
  label: {
    fontWeight: "400 !important",
    paddingLeft: "6px",
    paddingRight: "3px"
  }
};

type Props = {
  repository: string;
  page: any;
  pushBranchStateFunction: (branchName: string, pagePath: string) => void;
  branch: string; //current branch
  branches: Branch[];
  classes: any;
};

class BranchDropdown extends React.Component<Props> {
  constructor(props) {
    super(props);
  }

  handleBranchChange = (event) => {
    const { page, pushBranchStateFunction } = this.props;
    pushBranchStateFunction(event.target.value, page.path);
  };

  render() {
    const { branch, branches, classes } = this.props;

    const branchEntries = [];
    orderBranches(branches);

    for (const branch of branches) {
      branchEntries.push({ name: branch.name });
    }

    return (
      <div className={classNames(classes.dropdownWrapper)}>
        <span className={"glyphicon glyphicon-random"} />
        <label className={classNames(classes.label)} htmlFor={"branchSelect"}>
          Branch
        </label>
        <select
          id={"branchSelect"}
          value={branch}
          onChange={this.handleBranchChange}
          className={classNames(classes.dropdown)}
        >
          {branchEntries.map((branch) => {
            return (
              <option
                className={classNames(classes.option)}
                key={branch.name}
                value={encodeURIComponent(branch.name)}
                // label={branch.name.substr(0, branch.name.length)} possible leverage point for to long branch names, other solutions did not work
              >
                {branch.name}
              </option>
            );
          })}
        </select>
      </div>
    );
  }
}

export default injectSheet(styles)(BranchDropdown);
