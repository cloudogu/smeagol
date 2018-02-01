// @flow
import React, { Component } from 'react';
import injectSheet from 'react-jss';

import Editor from 'tui-editor/dist/tui-editor-Editor';
import 'tui-editor/dist/tui-editor-extTable';
import 'tui-editor/dist/tui-editor-extScrollSync';
import 'tui-editor/dist/tui-editor-extUML';

import 'tui-editor/dist/tui-editor.css';
// import 'tui-editor/dist/tui-editor-contents.css';

import 'codemirror/lib/codemirror.css';

import 'highlight.js/lib';
import 'highlight.js/styles/default.css';
import ActionButton from './ActionButton';
import ActionLink from './ActionLink';
import CommitForm from './CommitForm';
import { withRouter } from 'react-router-dom';

const styles = {
    action: {
        paddingTop: '1em'
    }
};

type Props = {
    onSave: Function,
    history: any,
    classes: any
};

type State = {
    showCommitForm: boolean
};

class MarkdownEditor extends Component<Props,State> {

    constructor(props) {
        super(props);
        this.state =  {
            showCommitForm: false
        };
    }

    componentDidMount() {
        this.editor = new Editor({
            el: this.editorNode,
            height: '640px',
            previewStyle: 'vertical',
            initialEditType: 'markdown',
            initialValue: this.props.content,
            exts: ['scrollSync', 'colorSyntax', 'uml', 'chart', 'mark', 'table', 'taskCounter']
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
        this.props.history.push('?');
    };

    render() {
        const { classes } = this.props;
        return (
            <div>
                <div ref={ref => this.editorNode = ref} />
                <div className={classes.action}>
                    <ActionButton i18nKey="markdown-editor_save" type="primary" onClick={this.commit} />
                    <ActionLink i18nKey="markdown-editor_abort" to="?" />
                </div>
                <CommitForm show={ this.state.showCommitForm } onSave={ this.save } onAbort={ this.abortCommit } />
            </div>
        );
    }
}

export default withRouter(injectSheet(styles)(MarkdownEditor));