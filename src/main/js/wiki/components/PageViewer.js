//@flow
import React from 'react';
import PageContent from './PageContent';
import PageHeader from './PageHeader';
import PageFooter from './PageFooter';

type Props = {
    page: any,
    wiki: any,
    onDelete: () => void,
    onHome: () => void,
    onMove: () => void
};

class PageViewer extends React.Component<Props> {

    render() {
        const { page, wiki, onDelete, onHome, onMove } = this.props;

        return (
            <div>
                <PageHeader page={page} wiki={wiki} onDeleteClick={onDelete} onHomeClick={onHome} onOkMoveClick={onMove} />
                <PageContent page={page} />
                <PageFooter page={page} />
            </div>
        );
    }

}

export default PageViewer;
