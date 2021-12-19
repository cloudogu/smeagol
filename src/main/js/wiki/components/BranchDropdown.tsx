import React from "react";
import injectSheet from "react-jss";
import classNames from "classnames";
import { orderBranches } from "../../repository/components/BranchOverview";
import { Branch } from "../../repository/types/repositoryDto";
import "./BranchDropdownMediaQuery.css";

const styles = {
  dropdownWrapper: {
    "background-color": "white",
    border: "#777777",
    color: "#777777",
    padding: "0px 12px",
    fontSize: "14px",
    marginLeft: "auto",
    fontFamily: "sans-serif",
    justifySelf: "end"
  },
  dropdown: {
    maxWidth: "200px",
    textOverflow: "ellipsis",
    backgroundColor: "transparent",
    fontSize: "14px"
  },
  option: {
    wordBreak: "break-all",
    overflow: "hidden",
    maxWidth: "inherit",
    fontSize: "16px"
  },
  label: {
    fontWeight: "400 !important",
    paddingLeft: "6px",
    paddingRight: "3px",
    verticalAlign: "top"
  }
};

type Props = {
  repository: string;
  path: any;
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
    const { path, pushBranchStateFunction } = this.props;
    pushBranchStateFunction(event.target.value, path);
  };

  render() {
    const { branch, branches, classes } = this.props;

    const branchEntries = [];
    orderBranches(branches);

    for (const branch of branches) {
      branchEntries.push({ name: branch.name });
    }

    return (
      <div id="branchDropdownWrapper" className={classNames(classes.dropdownWrapper)}>
        <label className={classNames(classes.label)} htmlFor={"branchSelect"}>
          <span style={{ marginRight: "10px" }} className={"glyphicon glyphicon-random"} />
          Branch
        </label>
        <select
          id={"branchSelect"}
          key="branchDropdown"
          value={branch}
          onChange={this.handleBranchChange}
          className={classNames(classes.dropdown)}
        >
          {branchEntries.map((branch) => {
            return (
              <option className={classNames(classes.option)} key={branch.name} value={encodeURIComponent(branch.name)}>
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
