//@flow
import React from 'react';
import MarkdownEditor from './MarkdownEditor';

type Props = {
    page: any
};

class PageEditor extends React.Component<Props> {

    render() {
        const { page } = this.props;
        return (
            <div>
                <h1>{ page.path }</h1>
                <MarkdownEditor content={ page.content }/>
            </div>
        );
    }

}

export default PageEditor;
