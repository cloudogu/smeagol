//@flow
import React from 'react';
import {fetchPage} from '../modules/page';
import {connect} from 'react-redux';
import PageViewer from '../components/PageViewer';
import * as queryString from 'query-string';
import PageEditor from '../components/PageEditor';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';

type Props = {
    loading: boolean,
    error: any,
    page: any
};

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
        const { error, loading, page } = this.props;

        if (error) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <I18nAlert i18nKey="page_failed_to_fetch" />
                </div>
            );
        } else if (loading) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <Loading />
                </div>
            );
        } else if (!page) {
            return (
                <div>
                    <h1>Smeagol</h1>
                </div>
            );
        }

        if (this.isEditMode()) {
            return <PageEditor page={page} />;
        }

        return <PageViewer page={page} />;
    }

}

const mapStateToProps = (state) => {
    return state.page;
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchPage: (repository, branch, path) => {
            dispatch(fetchPage(repository, branch, path))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Page);
