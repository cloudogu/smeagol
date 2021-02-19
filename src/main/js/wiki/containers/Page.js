//@flow
import React from 'react';
import {
    useEditPage,
    useCreatePage,
    createPageUrl,
    usePage, useRenamePage, useDeletePage, useRestorePage
} from '../modules/page';
import {useWiki} from '../modules/wiki';
import PageViewer from '../components/PageViewer';
import * as queryString from 'query-string';
import PageEditor from '../components/PageEditor';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';
import {PAGE_NOT_FOUND_ERROR} from "../../apiclient";

type Props = {
    history: any,
    match: any,
    location: any
};

export default function Page(props: Props) {
    const pushLandingPageState = () => {
        pushPageStateClosure()(wikiQuery.data.landingPage);
    };

    const deletePage = () => {
        // TODO i18n
        const message = 'Delete page ' + path + ' (smeagol)';
        deletePageMutation.mutate(message)
    };

    const onRestore = (pagePath: string, commit: string) => {
        // TODO i18n
        const message = 'Restore commit ' + commit + ' from page ' + pagePath + ' (smeagol)';
        restorePageMutation.mutate({message: message, commit: commit})
    };

    const pushPageStateClosure = () => {
        const {repository, branch} = props.match.params;
        return function (pagePath: string) {
            props.history.push(`/${repository}/${branch}/${pagePath}`);
        }
    };

    const onMove = (target: string) => {
        renamePageMutation.mutate({"target": target})
    }

    const onAbortEdit = () => {
        const {history} = props;
        history.push('?');
    };

    const onAbortCreate = () => {
        pushLandingPageState();
    };

    const search = (query: string) => {
        const {history, repository, branch} = props.match.params;
        history.push(`/${repository}/${branch}/search?query=${query}`);
    };

    const {repository, branch} = props.match.params;
    const path = findPagePath(props);

    let pageURL = createPageUrl(repository, branch, path);
    let currentPageURL
    if (isCommitPage(props)) {
        currentPageURL = pageURL + '?commit=' + getCommitParameter(props);
    } else {
        currentPageURL = pageURL
    }

    const pageQuery = usePage(currentPageURL)
    const wikiQuery = useWiki(repository, branch)

    const editPageMutation = useEditPage(pageURL)
    const deletePageMutation = useDeletePage(pageURL, pushLandingPageState)
    const createPageMutation = useCreatePage(pageURL)
    const renamePageMutation = useRenamePage(pageURL, path, pushPageStateClosure())
    const restorePageMutation = useRestorePage(pageURL, () => {
        pushPageStateClosure()((path))
    })

    const isLoading = pageQuery.isLoading || wikiQuery.isLoading || editPageMutation.isLoading ||
        createPageMutation.isLoading || renamePageMutation.isLoading || deletePageMutation.isLoading ||
        restorePageMutation.isLoading

    if (pageQuery.error === PAGE_NOT_FOUND_ERROR || wikiQuery.error === PAGE_NOT_FOUND_ERROR) {
        return (
            <PageEditor path={path} content="" onSave={(message, content) => {
                createPageMutation.mutate({"message": message, "content": content})
            }} onAbort={onAbortCreate}/>
        );
    } else if (pageQuery.error || wikiQuery.error) {
        return (
            <div>
                <h1>Smeagol</h1>
                <I18nAlert i18nKey="page_failed_to_fetch"/>
            </div>
        );
    } else if (isLoading) {
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

    let wiki = wikiQuery.data;
    wiki.repository = repository;
    wiki.branch = branch;

    if (editPageMutation.error || createPageMutation.error || renamePageMutation.error || deletePageMutation.error ||
        restorePageMutation.error) {
        return (
            <div>
                <h1>Smeagol</h1>
                <I18nAlert i18nKey="page_failed_to_modify"/>
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
    if (wiki.directory) {
        pagesLink = `/${repository}/${branch}/pages/${wiki.directory}`;
        historyLink = `/${repository}/${branch}/history/${path}`;
        // TODO check for polyfill
        if (!pagesLink.endsWith('/')) {
            pagesLink += '/';
        }
    }

    return <PageViewer page={pageQuery.data} wiki={wiki} onDelete={deletePage} onHome={pushLandingPageState}
                       onMove={onMove} pagesLink={pagesLink} historyLink={historyLink}
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