//@flow
import React from 'react';
import injectSheet from 'react-jss';

import Editor from 'tui-editor/dist/tui-editor-Editor';
import 'tui-editor/dist/tui-editor-extTable';
import 'tui-editor/dist/tui-editor-extScrollSync';
import 'tui-editor/dist/tui-editor-extUML';

import './TableClassEditorExtension';
import './HistoryEditorExtension';
import './LegacyPlantumlEditorExtension';
import './ShortLinkEditorExtension';

import 'codemirror/lib/codemirror.css';

import 'highlight.js/lib';
import 'highlight.js/styles/default.css';

const styles = {
    markdown: {
        // makes img elements responsive
        '& img': {
            'max-width': '100%',
            'height': 'auto',
            'display': 'block'
        }
    }
};

type Props = {
    content: string,
    classes: any
}

class Markdown extends React.Component<Props> {

    componentDidMount() {
        this.editor = new Editor.factory({
            el: this.viewerNode,
            viewer: true,
            initialEditType: 'markdown',
            initialValue: this.props.content,
            exts: ['colorSyntax', {name: 'uml', rendererURL: '/plantuml/png/'}, 'chart', 'mark', 'table', 'tableClass',
                'taskCounter', 'shortlinks', 'history', 'legacyplantuml'],
        });
    }

    componentDidUpdate(prevProps) {
        if (prevProps.content !== this.props.content) {
            this.editor.setMarkdown(this.props.content);
        }
    }

    render() {
        return (
            <div className={this.props.classes.markdown} ref={ref => this.viewerNode = ref}>
            </div>
        );
    }

}

export default injectSheet(styles)(Markdown);
