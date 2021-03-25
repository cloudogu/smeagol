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
  path: string;
  onAbortClick: () => void;
};

type State = {
  showCommitForm: boolean;
};

class MarkdownEditor extends Component<Props, State> {
  constructor(props) {
    super(props);
    this.state = {
      showCommitForm: false
    };
  }

  componentDidMount() {
    this.editor = new Editor.factory({
      el: this.editorNode,
      height: "640px",
      previewStyle: "vertical",
      initialEditType: "markdown",
      initialValue: this.props.content,
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
    const { classes, path, onAbortClick } = this.props;
    const defaultMessage = "Updated " + path + " (smeagol)";

    return (
      <div>
        <div className={this.props.classes.markdownEditor} ref={(ref) => (this.editorNode = ref)} />
        <div className={classes.action}>
          <ActionButton i18nKey="markdown-editor_save" type="primary" onClick={this.commit} />
          <ActionButton i18nKey="markdown-editor_abort" onClick={onAbortClick} />
        </div>
        <CommitForm
          defaultMessage={defaultMessage}
          show={this.state.showCommitForm}
          onSave={this.save}
          onAbort={this.abortCommit}
        />
      </div>
    );
  }
}

export default withRouter(injectSheet(styles)(MarkdownEditor));
