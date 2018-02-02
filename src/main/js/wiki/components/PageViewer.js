//@flow
import React from 'react';
import PageContent from './PageContent';
import PageHeader from './PageHeader';
import PageFooter from './PageFooter';

type Props = {
    page: any
};

class PageViewer extends React.Component<Props> {

    render() {
        const { page } = this.props;

        return (
            <div>
                <PageHeader page={page} />
                <PageContent page={page} />
                <PageFooter page={page} />
            </div>
        );
    }

}

export default PageViewer;
