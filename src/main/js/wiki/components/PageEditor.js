//@flow
import React from 'react';
import MarkdownEditor from './MarkdownEditor';

type Props = {
    path: string,
    content: string,
    onSave: Function
};

class PageEditor extends React.Component<Props> {

    render() {
        const { path, content, onSave } = this.props;
        return (
            <div>
                <h1>{ path }</h1>
                <MarkdownEditor path={ path } content={ content } onSave={onSave} />
            </div>
        );
    }

}

export default PageEditor;
