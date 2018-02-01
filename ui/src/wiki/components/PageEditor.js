//@flow
import React from 'react';
import MarkdownEditor from './MarkdownEditor';
import I18nAlert from '../../I18nAlert';

type Props = {
    page: any,
    onSave: Function
};

class PageEditor extends React.Component<Props> {

    render() {
        const { page, onSave } = this.props;
        if (!page._links.edit){
            return (
                <div>
                    <h1>{page.path}</h1>
                    <I18nAlert i18nKey="page-editor_no_write_permissions" />
                </div>
            );
        }

        return (
            <div>
                <h1>{ page.path }</h1>
                <MarkdownEditor path={ page.path } content={ page.content } onSave={onSave} />
            </div>
        );
    }

}

export default PageEditor;
