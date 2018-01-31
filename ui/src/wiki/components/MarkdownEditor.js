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


const styles = {};

class MarkdownEditor extends Component {

    componentDidMount() {
        this.editor = new Editor({
            el: this.editorNode,
            height: '640px',
            previewStyle: 'vertical',
            initialEditType: 'markdown',
            initialValue: this.props.content,
            exts: ['scrollSync', 'colorSyntax', 'uml', 'chart', 'mark', 'table', 'taskCounter'],
            events: {
                change: this.onChange
            }
        });
    }

    onChange = () => {
        this.setState({
            value: this.editor.getMarkdown()
        });
    };

    render() {
        return (
            <div ref={ref => this.editorNode = ref}>
            </div>
        );
    }
}

export default injectSheet(styles)(MarkdownEditor);