//@flow
import React from 'react';
import PageContent from './PageContent';
import PageHeader from './PageHeader';
import PageFooter from './PageFooter';

type Props = {
    page: any,
    wiki: any,
    pagesLink: string,
    onDelete: () => void,
    onHome: () => void,
    onMove: () => void,
    search: (string) => void
};

class PageViewer extends React.Component<Props> {

    render() {
        const { page, wiki, onDelete, onHome, onMove, pagesLink, search } = this.props;

        return (
            <div>
                <PageHeader page={page} wiki={wiki} pagesLink={pagesLink} onDeleteClick={onDelete} onHomeClick={onHome} onOkMoveClick={onMove} search={search} />
                <PageContent page={page} />
                <PageFooter page={page} />
            </div>
        );
    }

}

export default PageViewer;
