//@flow
import React from 'react';
import PageContent from './PageContent';
import PageHeader from './PageHeader';
import PageFooter from './PageFooter';

type Props = {
    page: any,
    onDeleteClick: () => void,
    onHomeClick: () => void
};

class PageViewer extends React.Component<Props> {

    render() {
        const { page, onDeleteClick, onHomeClick } = this.props;

        return (
            <div>
                <PageHeader page={page} onDeleteClick={onDeleteClick} onHomeClick={onHomeClick} />
                <PageContent page={page} />
                <PageFooter page={page} />
            </div>
        );
    }

}

export default PageViewer;
