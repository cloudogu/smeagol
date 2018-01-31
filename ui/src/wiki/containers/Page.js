//@flow
import React from 'react';
import {fetchPage} from '../modules/page';
import {connect} from 'react-redux';
import PageHeader from '../components/PageHeader';
import PageContent from '../components/PageContent';
import PageFooter from '../components/PageFooter';

type Props = {}

class Page extends React.Component<Props> {

    componentDidMount() {
        const { repository, branch } = this.props.match.params;
        const path = this.findPagePath();
        this.props.fetchPage(repository, branch, path);
    }

    findPagePath() {
        const { pathname } = this.props.location;
        const parts = pathname.split('/');
        return parts.slice(3).join('/');
    }

    render() {
        const { page } = this.props;
        if (!page) {
            return <div></div>
        }

        return (
            <div>
                <PageHeader page={page} />
                <PageContent page={page} />
                <PageFooter page={page} />
            </div>
        );
    }

}

const mapStateToProps = (state) => ({
    page: state.page ? state.page.page : null
});

const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        fetchPage: (repository, branch, path) => {
            dispatch(fetchPage(repository, branch, path))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Page);
