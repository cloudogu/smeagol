//@flow
import React from 'react';
import PageContent from './PageContent';
import PageHeader from './PageHeader';
import PageFooter from './PageFooter';

type Props = {
    page: any,
    deletePage: () => void,
    clickHome: () => void
};

class PageViewer extends React.Component<Props> {

    render() {
        const { page, deletePage, clickHome } = this.props;

        return (
            <div>
                <PageHeader page={page} deletePage={deletePage} clickHome={clickHome} />
                <PageContent page={page} />
                <PageFooter page={page} />
            </div>
        );
    }

}

export default PageViewer;
