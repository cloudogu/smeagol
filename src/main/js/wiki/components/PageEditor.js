//@flow
import React from 'react';
import MarkdownEditor from './MarkdownEditor';

type Props = {
    path: string,
    content: string,
    onSave: Function,
    onAbortClick: () => void
};

class PageEditor extends React.Component<Props> {

    render() {
        const { path, content, onSave, onAbortClick } = this.props;
        return (
            <div>
                <h1>{ path }</h1>
                <MarkdownEditor path={ path } content={ content } onSave={onSave} onAbortClick={onAbortClick} />
            </div>
        );
    }

}

export default PageEditor;
