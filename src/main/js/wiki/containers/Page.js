//@flow
import React from 'react';
import {
    editPage,
    createPage,
    createPageUrl,
    usePage, renamePage
} from '../modules/page';
import {useWiki} from '../modules/wiki';
import PageViewer from '../components/PageViewer';
import * as queryString from 'query-string';
import PageEditor from '../components/PageEditor';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';
import {PAGE_NOT_FOUND_ERROR} from "../../apiclient";

type Props = {
    path: string,
    loading: boolean,
    notFound: boolean,
    editMode: boolean,
    error: any,
    page: any,
    wiki: any,
    repository: string,
    branch: string,
    history: any,
    match: any,
    location: any
};

export default function Page(props: Props) {

    let pushLandingPageState = () => {
        pushPageStateClosure(wikiQuery.data.landingPage)();
    };

    let deleteP = () => {
        const {path, url, deletePage} = this.props;
        // TODO i18n
        const message = 'Delete page ' + path + ' (smeagol)';
        deletePage(url, message, this.pushLandingPageState);
    };

    let onRestore = (pagePath: string, commit: string) => {
        const {restorePage, repository, branch} = this.props;
        // TODO i18n
        const message = 'Restore commit ' + commit + ' from page ' + pagePath + ' (smeagol)';
        const apiPath = createPageUrl(repository, branch, pagePath);
        restorePage(apiPath, message, commit, this.pushPageStateClosure(pagePath));
    };

    let pushPageStateClosure = (pagePath: string) => {
        const {repository, branch} = props.match.params;
        return function () {
            props.history.push(`/${repository}/${branch}/${pagePath}`);
        }
    };

    let onAbortEdit = () => {
        const {history} = props;
        history.push('?');
    };

    let onAbortCreate = () => {
        pushLandingPageState();
    };

    let search = (query: string) => {
        const {history, repository, branch} = props.match.params;
        history.push(`/${repository}/${branch}/search?query=${query}`);
    };

    const {repository, branch} = props.match.params;
    const path = findPagePath(props);

    let url = createPageUrl(repository, branch, path);
    if (isCommitPage(props)) {
        url += '?commit=' + getCommitParameter(props);
    }

    const pageQuery = usePage(url)
    const wikiQuery = useWiki(repository, branch)
    const editPageMutation = editPage(url)
    const createPageMutation = createPage(url)
    const renamePageMutation = renamePage(url, props.path)

    if (pageQuery.error === PAGE_NOT_FOUND_ERROR || wikiQuery.error === PAGE_NOT_FOUND_ERROR) {
        return (
            <PageEditor path={props.path} content="" onSave={(message, content) => {
                createPageMutation.mutate({"message": message, "content": content})
            }} onAbort={onAbortCreate}/>
        );
    } else if (pageQuery.isError || wikiQuery.isError) {
        return (
            <div>
                <h1>Smeagol</h1>
                <I18nAlert i18nKey="page_failed_to_fetch"/>
            </div>
        );
    } else if (pageQuery.isLoading || wikiQuery.isLoading) {
        return (
            <div>
                <h1>Smeagol</h1>
                <Loading/>
            </div>
        );
    } else if (!pageQuery.data || !wikiQuery.data) {
        return (
            <div>
                <h1>Smeagol</h1>
            </div>
        );
    }

    if (isEditMode(props)) {
        return <PageEditor path={pageQuery.data.path} content={pageQuery.data.content} onSave={(message, content) => {
            editPageMutation.mutate({"message": message, "content": content})
        }} onAbort={onAbortEdit}/>;
    }
    let pagesLink = '#';
    let historyLink = '#';
    if (wikiQuery.data.directory) {
        pagesLink = `/${repository}/${branch}/pages/${wikiQuery.data.directory}`;
        historyLink = `/${repository}/${branch}/history/${path}`;
        // TODO check for polyfill
        if (!pagesLink.endsWith('/')) {
            pagesLink += '/';
        }
    }

    return <PageViewer page={pageQuery.data} wiki={wikiQuery.data} onDelete={deleteP} onHome={pushLandingPageState}
                       onMove={(target) => {
                           renamePageMutation.mutate({"target": target})
                       }} pagesLink={pagesLink} historyLink={historyLink}
                       onRestore={onRestore} search={search}/>;
}

function isEditMode(props): boolean {
    const queryParams = queryString.parse(props.location.search);
    return queryParams.edit === 'true';
}

function isCommitPage(props): boolean {
    const queryParams = queryString.parse(props.location.search);
    return queryParams.commit !== undefined;
}

function getCommitParameter(props): string {
    const queryParams = queryString.parse(props.location.search);
    return queryParams.commit;
}

function findPagePath(props) {
    const {pathname} = props.location;
    const parts = pathname.split('/');
    return parts.slice(3).join('/');
}