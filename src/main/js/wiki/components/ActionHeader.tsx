import React from "react";
import injectSheet from "react-jss";
import ActionButton from "./ActionButton";
import PageNameForm from "./PageNameForm";
import { withRouter } from "react-router-dom";
import classNames from "classnames";
import BranchDropdown from "./BranchDropdown";
import { Branch } from "../../repository/types/repositoryDto";

const styles = {
  flexbox: {
    justifyContent: "space-between",
    flexDirection: "column"
  },
  actions: {
    display: "flex",
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
    alignItems: "flex-start",
    marginBottom: "1em"
  }
};

type Props = {
  wiki: any;
  path: any;
  history: any;
  classes: any;
  inSettings: boolean;
  inEdit: boolean;
  pushBranchStateFunction: (branchName: string, pagePath: string) => void;
  branch: string;
  branches: Branch[];
};

type State = {
  showCreateForm: boolean;
  showMoveForm: boolean;
  showDeleteConfirm: boolean;
};

class ActionHeader extends React.Component<Props, State> {
  constructor(props) {
    super(props);
    this.state = {
      showCreateForm: false
    };
  }

  onCreateClick = () => {
    this.setState({
      showCreateForm: true
    });
  };

  onAbortCreateClick = () => {
    this.setState({
      showCreateForm: false
    });
  };

  onOkCreate = (name) => {
    const { repository, branch } = this.props.wiki;
    const wikiPath = `/${repository}/${branch}/`;
    const pagePath = this.getPathFromPagename(name);
    this.props.history.push(wikiPath + pagePath);
  };

  getPathFromPagename = (name) => {
    if (name.startsWith("/")) {
      return name.substr(1);
    } else {
      return `${this.props.wiki.directory}/${name}`;
    }
  };

  render() {
    const { path, wiki, classes, inSettings, inEdit, pushBranchStateFunction, branch, branches } = this.props;

    const createButton = (
      <ActionButton glyphicon="glyphicon-plus" type="menu" onClick={this.onCreateClick} i18nKey="page-header_create" />
    );

    let settingsButton;
    let branchDropdown;
    if (!inSettings) {
      settingsButton = (
        <ActionButton
          glyphicon="glyphicon-cog"
          type="menu"
          onClick={() => {
            const { repository, branch } = wiki;
            const wikiPath = `/${repository}/${branch}/`;
            this.props.history.push(wikiPath + "settings");
          }}
          i18nKey="page-header_settings"
        />
      );
    }

    if (!inSettings && !inEdit) {
      branchDropdown = (
        <BranchDropdown
          path={path}
          repository={this.props.wiki.repository}
          pushBranchStateFunction={pushBranchStateFunction}
          branch={branch}
          branches={branches}
        />
      );
    }

    const createForm = (
      <PageNameForm
        show={this.state.showCreateForm}
        onOk={this.onOkCreate}
        onAbortClick={this.onAbortCreateClick}
        labelPrefix="create"
        directory={wiki.directory}
      />
    );

    return (
      <div>
        <div className={classNames(classes.flexbox)}>
          <div className={classNames(classes.actions)}>
            <div>{createButton}</div>
            <div>{settingsButton}</div>
            <div>{branchDropdown}</div>
          </div>
          {this.props.children}
        </div>
        {createForm}
      </div>
    );
  }
}

export default withRouter(injectSheet(styles)(ActionHeader));
