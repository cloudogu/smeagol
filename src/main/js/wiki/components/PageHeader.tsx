import React from "react";
import injectSheet from "react-jss";
import ActionLink from "./ActionLink";
import ActionButton from "./ActionButton";
import PageNameForm from "./PageNameForm";
import { withRouter } from "react-router-dom";
import classNames from "classnames";
import ConfirmModal from "./ConfirmModal";

const styles = {
  header: {
    borderBottom: "1px solid #ddd"
  },
  actions: {
    marginBottom: "1em"
  }
};

type Props = {
  page: any;
  wiki: any;
  historyLink: string;
  onDelete: () => void;
  onHomeClick: () => void;
  onOkMoveClick: () => void;
  onRestoreClick: () => void;
  search: (arg0: string) => void;
  history: any;
  classes: any;
};

type State = {
  showCreateForm: boolean;
  showMoveForm: boolean;
  showDeleteConfirm: boolean;
};

class PageHeader extends React.Component<Props, State> {
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

  onOkMoveClick = (name) => {
    const path = this.getPathFromPagename(name);
    this.props.onOkMoveClick(path);
  };

  onDeleteClick = () => {
    this.setState({
      showDeleteConfirm: true
    });
  };

  onAbortDeleteClick = () => {
    this.setState({
      showDeleteConfirm: false
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

  onMoveClick = () => {
    this.setState({
      showMoveForm: true
    });
  };

  onAbortMoveClick = () => {
    this.setState({
      showMoveForm: false
    });
  };

  onRestoreClick = () => {
    const pagePath = this.props.page.path;
    const commit = this.props.page.commit.commitId;
    this.props.onRestoreClick(pagePath, commit);
  };

  getPagePathWithoutRootDirectory(page, wiki) {
    if (page.path.indexOf(wiki.directory) === 0) {
      return page.path.substring(wiki.directory.length + 1);
    }

    return page.path;
  }

  render() {
    const { page, wiki, classes, onDelete, historyLink } = this.props;

    const pathWithoutRoot = this.getPagePathWithoutRootDirectory(page, wiki);

    const editButton = page._links.edit ? (
      <ActionLink glyphicon="glyphicon-edit" type="menu" to="?edit=true" i18nKey="page-header_edit" />
    ) : (
      ""
    );
    const createButton = (
      <ActionButton glyphicon="glyphicon-plus" type="menu" onClick={this.onCreateClick} i18nKey="page-header_create" />
    );
    const historyButton = (
      <ActionLink glyphicon="glyphicon-step-backward" type="menu" to={historyLink} i18nKey="page-header_history" />
    );
    const moveButton = page._links.move ? (
      <ActionButton onClick={this.onMoveClick} glyphicon="glyphicon-pencil" type="menu" i18nKey="page-header_move" />
    ) : (
      ""
    );
    const deleteButton = page._links.delete ? (
      <ActionButton glyphicon="glyphicon-trash" type="menu" onClick={this.onDeleteClick} i18nKey="page-header_delete" />
    ) : (
      ""
    );
    const restoreButton = page._links.restore ? (
      <ActionButton
        glyphicon="glyphicon-retweet"
        type="menu"
        onClick={this.onRestoreClick}
        i18nKey="page-header_restore"
      />
    ) : (
      ""
    );
    const createForm = (
      <PageNameForm
        show={this.state.showCreateForm}
        onOk={this.onOkCreate}
        onAbortClick={this.onAbortCreateClick}
        labelPrefix="create"
        directory={wiki.directory}
      />
    );
    const moveForm = (
      <PageNameForm
        show={this.state.showMoveForm}
        onOk={this.onOkMoveClick}
        onAbortClick={this.onAbortMoveClick}
        labelPrefix="move"
        directory={wiki.directory}
        initialValue={pathWithoutRoot}
      />
    );
    const deleteConfirmModal = (
      <ConfirmModal
        show={this.state.showDeleteConfirm}
        onOk={onDelete}
        onAbortClick={this.onAbortDeleteClick}
        labelPrefix="delete"
      />
    );

    return (
      <div className={classes.header}>
        <div className={classNames(classes.actions, "row")}>
          <div className="col-xs-9">
            {createButton}
            {editButton}
            {moveButton}
            {historyButton}
            {deleteButton}
            {restoreButton}
          </div>
        </div>
        {createForm}
        {moveForm}
        {deleteConfirmModal}
      </div>
    );
  }
}

export default withRouter(injectSheet(styles)(PageHeader));
