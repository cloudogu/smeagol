//@flow
import React from 'react';
import {editPage, fetchPage, createPage} from '../modules/page';
import {connect} from 'react-redux';
import PageViewer from '../components/PageViewer';
import * as queryString from 'query-string';
import PageEditor from '../components/PageEditor';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';

type Props = {
    loading: boolean,
    notFound: boolean,
    error: any,
    page: any,
    fetchPage: Function,
    editPage: Function
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

    edit = (message: string, content: string) => {
        this.props.editPage(this.props.page, message, content);
    };

    create = (message: string, content: string) => {
        const { repository, branch } = this.props.match.params;
        const path = this.findPagePath();
        this.props.createPage(repository, branch, path, message, content);
    };

    render() {
        const { error, loading, page, notFound } = this.props;

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
                    <Loading/>
                </div>
            );
        } else if (notFound) {
            const path = this.findPagePath();

            return (
                <PageEditor path={path} content="" onSave={this.create} />
            );
        } else if (!page) {
            return (
                <div>
                    <h1>Smeagol</h1>
                </div>
            );
        }

        if (this.isEditMode()) {
            return <PageEditor path={page.path} content={page.content} onSave={this.edit} />;
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
        },
        editPage: (page: any, message: string, content: string) => {
            dispatch(editPage(page, message, content))
        },
        createPage: (repository: string, branch: string, path: string, message: string, content: string) => {
            dispatch(createPage(repository, branch, path, message, content))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Page);
