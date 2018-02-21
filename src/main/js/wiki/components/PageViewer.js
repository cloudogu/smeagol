//@flow
import React from 'react';
import PageContent from './PageContent';
import PageHeader from './PageHeader';
import PageFooter from './PageFooter';

type Props = {
    page: any,
    pagesLink: string,
    onDelete: () => void,
    onHome: () => void
};

class PageViewer extends React.Component<Props> {

    render() {
        const { page, onDelete, onHome, pagesLink } = this.props;

        return (
            <div>
                <PageHeader page={page} pagesLink={pagesLink} onDeleteClick={onDelete} onHomeClick={onHome} />
                <PageContent page={page} />
                <PageFooter page={page} />
            </div>
        );
    }

}

export default PageViewer;
