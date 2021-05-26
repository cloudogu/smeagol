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
    textOverflow: "ellipsis",
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
    event.target.size = 1;
    const { page, pushBranchStateFunction } = this.props;
    pushBranchStateFunction(event.target.value, page.path);
    this.forceUpdate();
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
        <label className={classNames(classes.label)} htmlFor={"branchSelect"}>
          <span style={{ marginRight: "10px" }} className={"glyphicon glyphicon-random"} />
          Branch
        </label>
        <select
          id={"branchSelect"}
          key="branchDropdown"
          value={branch}
          onChange={this.handleBranchChange}
          onBlur={(e) => (e.target.size = 0)}
          onFocus={(e) => (e.target.size = 5)}
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
