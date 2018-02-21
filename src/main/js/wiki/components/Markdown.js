//@flow
import React from 'react';
import injectSheet from 'react-jss';

import Viewer from 'tui-editor/dist/tui-editor-Viewer';
import 'tui-editor/dist/tui-editor-extTable';
import 'tui-editor/dist/tui-editor-extScrollSync';
import 'tui-editor/dist/tui-editor-extUML';
import './HistoryEditorExtension';

import 'codemirror/lib/codemirror.css';

import 'highlight.js/lib';
import 'highlight.js/styles/default.css';

const styles = {};

type Props = {
    content: string,
    classes: any
}

class Markdown extends React.Component<Props> {

    componentDidMount() {
        this.editor = new Viewer({
            el: this.viewerNode,
            initialEditType: 'markdown',
            initialValue: this.props.content,
            exts: ['colorSyntax', 'uml', 'chart', 'mark', 'table', 'taskCounter', 'history'],
        });
    }

    componentDidUpdate(prevProps) {
        if (prevProps.content !== this.props.content) {
            this.editor.setMarkdown(this.props.content);
        }
    }

    render() {
        return (
            <div ref={ref => this.viewerNode = ref}>
            </div>
        );
    }

}

export default injectSheet(styles)(Markdown);
