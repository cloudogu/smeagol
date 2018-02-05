//@flow
import React from 'react';
import {editPage, createPage, createPageUrl, fetchPageIfNeeded} from '../modules/page';
import {connect} from 'react-redux';
import PageViewer from '../components/PageViewer';
import * as queryString from 'query-string';
import PageEditor from '../components/PageEditor';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';

type Props = {
    url: string,
    path: string,
    loading: boolean,
    notFound: boolean,
    editMode: boolean,
    error: any,
    page: any,
    fetchPageIfNeeded: (url: string) => void,
    editPage: (url: string, message: string, content: string) => void,
    createPage: (url: string, message: string, content: string) => void
};

class Page extends React.Component<Props> {

    componentDidMount() {
        this.props.fetchPageIfNeeded(this.props.url);
    }

    componentDidUpdate() {
        this.props.fetchPageIfNeeded(this.props.url);
    }

    edit = (message: string, content: string) => {
        this.props.editPage(this.props.url, message, content);
    };

    create = (message: string, content: string) => {
        this.props.createPage(this.props.url, message, content);
    };

    render() {
        const { error, loading, page, path, notFound, editMode } = this.props;

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

        if (editMode) {
            return <PageEditor path={page.path} content={page.content} onSave={this.edit} />;
        }

        return <PageViewer page={page} />;
    }
}

function isEditMode(props): boolean {
    const queryParams = queryString.parse(props.location.search);
    return queryParams.edit === 'true';
}

function findPagePath(props) {
    const { pathname } = props.location;
    const parts = pathname.split('/');
    return parts.slice(3).join('/');
}

const mapStateToProps = (state, ownProps) => {
    const { repository, branch } = ownProps.match.params;
    const path = findPagePath(ownProps);
    const url = createPageUrl(repository, branch, path);

    const props = {
        ...state.page[url],
        path,
        url,
        editMode: isEditMode(ownProps)
    };

    return props;
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchPageIfNeeded: (url: string) => {
            dispatch(fetchPageIfNeeded(url))
        },
        editPage: (url: string, message: string, content: string) => {
            dispatch(editPage(url, message, content))
        },
        createPage: (url: string, message: string, content: string) => {
            dispatch(createPage(url, message, content))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Page);
