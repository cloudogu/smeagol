import React, { Component } from "react";
import injectSheet from "react-jss";

import Editor from "tui-editor/dist/tui-editor-Editor";
import "tui-editor/dist/tui-editor-extTable";
import "tui-editor/dist/tui-editor-extScrollSync";
import "tui-editor/dist/tui-editor-extUML";

import "./HistoryEditorExtension";
import "./ShortLinkEditorExtension";
import "./TableClassEditorExtension";

import "codemirror/lib/codemirror.css";
import "tui-editor/dist/tui-editor.css";
// import 'tui-editor/dist/tui-editor-contents.css';
import "./markdown-editor-customization.css";

import "highlight.js/lib";
import "highlight.js/styles/default.css";
import ActionButton from "./ActionButton";
import CommitForm from "./CommitForm";
import { withRouter } from "react-router-dom";
import ConfirmModal from "./ConfirmModal";

export const LOCAL_STORAGE_UNSAVED_CHANGES_KEY = "unsaved-changes";

const styles = {
  action: {
    paddingTop: "1em"
  },
  markdownEditor: {
    // makes img elements responsive
    "& img": {
      "max-width": "100%",
      height: "auto",
      display: "block"
    }
  }
};

type Props = {
  onSave: (...args: Array<any>) => any;
  history: any;
  classes: any;
  content: string;
  repository: string;
  branch: string;
  path: string;
  onAbortClick: () => void;
};

type State = {
  showCommitForm: boolean;
  unsavedChanges: string;
};

class MarkdownEditor extends Component<Props, State> {
  private editor: Editor.factory;
  private ignoreUnsavedChanges = false;

  constructor(props) {
    super(props);
    this.state = {
      showCommitForm: false,
      unsavedChanges: null
    };
  }

  componentDidMount() {
    this.checkForUnsavedChangesInLocalStorage();
    this.editor = new Editor.factory({
      el: this.editorNode,
      height: "640px",
      previewStyle: "vertical",
      initialEditType: "markdown",
      initialValue: this.props.content,
      usageStatistics: false,
      exts: [
        "scrollSync",
        "colorSyntax",
        { name: "uml", rendererURL: "/plantuml/png/" },
        "chart",
        "mark",
        "table",
        "tableClass",
        "taskCounter",
        "shortlinks",
        "history"
      ]
    });
  }

  componentWillUnmount() {
    this.putUnsavedChangesInLocalStorage();
  }

  commit = () => {
    this.setState({
      showCommitForm: true
    });
  };

  abortCommit = () => {
    this.setState({
      showCommitForm: false
    });
  };

  save = (message: string) => {
    const content = this.editor.getMarkdown();
    if (this.props.onSave) {
      this.props.onSave(message, content);
    }

    this.setState({
      showCommitForm: false
    });

    // navigate back to page
    this.props.history.push("?");
  };

  render() {
    const { classes, path } = this.props;
    const defaultMessage = "Updated " + path + " (smeagol)";

    return (
      <div>
        <div className={this.props.classes.markdownEditor} ref={(ref) => (this.editorNode = ref)} />
        <div className={classes.action}>
          <ActionButton i18nKey="markdown-editor_save" type="primary" onClick={this.commit} />
          <ActionButton i18nKey="markdown-editor_abort" onClick={this.onAbortEditor} />
        </div>
        <CommitForm
          defaultMessage={defaultMessage}
          show={this.state.showCommitForm}
          onSave={this.save}
          onAbort={this.abortCommit}
        />
        <ConfirmModal
          labelPrefix="unsaved_changes"
          show={this.state.unsavedChanges != null}
          onOk={this.onRestoreUnsavedChanges}
          onAbortClick={this.onAbortUnsavedChanges}
        />
      </div>
    );
  }
  onAbortEditor = () => {
    this.ignoreUnsavedChanges = true;
    this.props.onAbortClick();
  };

  onRestoreUnsavedChanges = () => {
    this.editor.setValue(this.state.unsavedChanges);
    localStorage.removeItem(LOCAL_STORAGE_UNSAVED_CHANGES_KEY);
    this.setState({ unsavedChanges: null });
  };

  onAbortUnsavedChanges = () => {
    localStorage.removeItem(LOCAL_STORAGE_UNSAVED_CHANGES_KEY);
    this.setState({ unsavedChanges: null });
  };

  putUnsavedChangesInLocalStorage = () => {
    const content = this.editor.getMarkdown();
    if (content != this.props.content && !this.ignoreUnsavedChanges) {
      // The local storage is limited in space (usually around 5MB). In cases where a page exceeds this limit, an error can occur.
      try {
        localStorage.setItem(
          LOCAL_STORAGE_UNSAVED_CHANGES_KEY,
          JSON.stringify({
            item: { repository: this.props.repository, branch: this.props.branch, path: this.props.path },
            content: content
          })
        );
      } catch (e) {
        console.log("Failed to set local storage:" + e);
      }
    }
  };

  checkForUnsavedChangesInLocalStorage = () => {
    const localContent = localStorage.getItem(LOCAL_STORAGE_UNSAVED_CHANGES_KEY);
    const localContentJSON = JSON.parse(localContent);
    if (
      localContentJSON &&
      localContentJSON["item"].repository === this.props.repository &&
      localContentJSON["item"].branch === this.props.branch &&
      localContentJSON["item"].path === this.props.path &&
      this.props.content != localContentJSON.content
    ) {
      this.setState({ unsavedChanges: localContentJSON.content });
    }
  };
}

export default withRouter(injectSheet(styles)(MarkdownEditor));
