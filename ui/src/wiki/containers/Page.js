//@flow
import React from 'react';
import {fetchPage} from '../modules/page';
import {connect} from 'react-redux';
import PageViewer from '../components/PageViewer';
import * as queryString from 'query-string';
import PageEditor from '../components/PageEditor';

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

    isEditMode() {
        const queryParams = queryString.parse(this.props.location.search);
        return queryParams.edit;
    }

    render() {
        const { page } = this.props;
        if (!page) {
            return <div></div>
        }

        if (this.isEditMode()) {
            return <PageEditor page={page} />;
        }

        return <PageViewer page={page} />;
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
