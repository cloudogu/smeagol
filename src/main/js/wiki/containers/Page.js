//@flow
import React from 'react';
import {editPage, createPage, createPageUrl, fetchPageIfNeeded, deletePage} from '../modules/page';
import {createId, fetchWikiIfNeeded} from '../modules/wiki';
import {connect} from 'react-redux';
import PageViewer from '../components/PageViewer';
import * as queryString from 'query-string';
import PageEditor from '../components/PageEditor';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';
import {createDirectoryUrl} from '../modules/directory';

type Props = {
    pagesLink: string,
    url: string,
    path: string,
    loading: boolean,
    notFound: boolean,
    editMode: boolean,
    error: any,
    page: any,
    wiki:any,
    repository: string,
    branch: string,
    history: any,
    fetchPageIfNeeded: (url: string) => void,
    fetchWikiIfNeeded: (repository: string, branch: string) => void,
    editPage: (url: string, message: string, content: string) => void,
    createPage: (url: string, message: string, content: string) => void,
    onDelete: (url: string, message: string, callback: () => void) => void
};

class Page extends React.Component<Props> {

    componentDidMount() {
        const { url, repository, branch } = this.props;
        this.props.fetchPageIfNeeded(url);
        this.props.fetchWikiIfNeeded(repository, branch);
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

    pushLandingPageState = () => {
        const { history, repository, branch, wiki } = this.props;
        history.push(`/${repository}/${branch}/${wiki.landingPage}`);
    };

    delete = () => {
        const { path, url, deletePage } = this.props;
        // TODO i18n
        const message = 'delete page ' + path + ' (smeagol)';
        deletePage(url, message, this.pushLandingPageState);
    };

    onAbortEdit = () => {
        const { history } = this.props;
        history.push('?');
    };

    onAbortCreate = () => {
        this.pushLandingPageState();
    };

    render() {
        const { error, loading, page, path, notFound, editMode, pagesLink } = this.props;

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
                <PageEditor path={path} content="" onSave={this.create} onAbort={this.onAbortCreate} />
            );
        } else if (!page) {
            return (
                <div>
                    <h1>Smeagol</h1>
                </div>
            );
        }

        if (editMode) {
            return <PageEditor path={page.path} content={page.content} onSave={this.edit} onAbort={this.onAbortEdit} />;
        }

        return <PageViewer page={page} pagesLink={pagesLink} onDelete={ this.delete } onHome={ this.pushLandingPageState } />;
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
    const wikiId = createId(repository, branch);
    const wiki = state.wiki[wikiId] ||{};

    let pagesLink = '#';
    if (wiki.wiki && wiki.wiki.directory) {
        pagesLink = `/${repository}/${branch}/pages/${wiki.wiki.directory}`;
        // TODO check for polyfil
        if (!pagesLink.endsWith('/')) {
            pagesLink += '/';
        }
    }

    const props = {
        ...state.page[url],
        pagesLink,
        path,
        url,
        repository,
        branch,
        editMode: isEditMode(ownProps),
        wiki: wiki.wiki || {}
    };

    return props;
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchWikiIfNeeded: (repository: string, branch: string) => {
            dispatch(fetchWikiIfNeeded(repository, branch))
        },
        fetchPageIfNeeded: (url: string) => {
            dispatch(fetchPageIfNeeded(url))
        },
        editPage: (url: string, message: string, content: string) => {
            dispatch(editPage(url, message, content))
        },
        createPage: (url: string, message: string, content: string) => {
            dispatch(createPage(url, message, content))
        },
        deletePage: (url: string, message: string, callback: () => void) => {
            dispatch(deletePage(url, message, callback))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Page);
