import React, { FC } from "react";
import { useCreatePage, useDeletePage, useEditPage, usePage, useRenamePage, useRestorePage } from "../hooks/page";
import { useWiki } from "../hooks/wiki";
import PageViewer from "../components/PageViewer";
import * as queryString from "query-string";
import PageEditor from "../components/PageEditor";
import { PAGE_NOT_FOUND_ERROR } from "../../apiclient";
import { match, Redirect } from "react-router";
import WikiHeader from "../components/WikiHeader";
import WikiNotFoundError from "../components/WikiNotFoundError";
import WikiLoadingPage from "../components/WikiLoadingPage";
import WikiAlertPage from "../components/WikiAlertPage";
import { useRepository } from "../../repository/hooks/useRepository";
import { LOCAL_STORAGE_UNSAVED_CHANGES_KEY } from "../components/MarkdownEditor";

type Params = {
  repository: string;
  branch: string;
};

type Props = {
  history: any;
  match: match<Params>;
  location: Location;
};

const Page: FC<Props> = (props) => {
  const { repository, branch } = props.match.params;

  const pushPageState = (pagePath: string) => {
    props.history.push(`/${repository}/${branch}/${pagePath}`);
  };

  const pushBranchState = (branchName: string, pagePath: string) => {
    props.history.push(`/${repository}/${branchName}/${pagePath}`);
  };

  const pushLandingPageState = () => {
    pushPageState(wikiQuery.data.landingPage);
  };

  const path = findPagePath(props);
  const pageName = getPageNameFromPath(path);
  const directory = getDirectoryFromPath(path);

  const refetch = !isEditMode(props) && !isCreateMode(props);
  const pageQuery = usePage(repository, branch, path, getCommitParameter(props), refetch);
  const wikiQuery = useWiki(repository, branch, refetch);
  const repositoryQuery = useRepository(repository, refetch);

  const editPageMutation = useEditPage(repository, branch, path);
  const deletePageMutation = useDeletePage(repository, branch, path, pushLandingPageState);
  const createPageMutation = useCreatePage(repository, branch, path);
  const renamePageMutation = useRenamePage(repository, branch, path, pushPageState);
  const restorePageMutation = useRestorePage(repository, branch, path, pushPageState);

  if (editPageMutation.isSuccess) {
    localStorage.removeItem(LOCAL_STORAGE_UNSAVED_CHANGES_KEY);
    editPageMutation.reset();
  }

  if (createPageMutation.isSuccess) {
    localStorage.removeItem(LOCAL_STORAGE_UNSAVED_CHANGES_KEY);
    createPageMutation.reset();
  }

  const deletePage = () => {
    // TODO i18n
    const message = "Delete page " + path + " (smeagol)";
    deletePageMutation.mutate(message);
  };

  const onRestore = (pagePath: string, commit: string) => {
    // TODO i18n
    const message = "Restore commit " + commit + " from page " + pagePath + " (smeagol)";
    restorePageMutation.mutate({ message: message, commit: commit });
  };

  const onMove = (target: string) => {
    renamePageMutation.mutate(target);
  };

  const onAbortEdit = () => {
    const { history } = props;
    history.push("?");
  };

  const onAbortCreate = () => {
    pushLandingPageState();
  };

  const isLoading =
    pageQuery.isLoading ||
    wikiQuery.isLoading ||
    editPageMutation.isLoading ||
    createPageMutation.isLoading ||
    renamePageMutation.isLoading ||
    deletePageMutation.isLoading ||
    repositoryQuery.isLoading ||
    restorePageMutation.isLoading;

  let wikiHeader: JSX.Element;
  if (wikiQuery.data) {
    wikiHeader = (
      <>
        <WikiHeader
          branch={branch}
          repository={repository}
          wiki={wikiQuery.data}
          pageName={pageName}
          directory={directory}
        />
        <hr />
      </>
    );
  }

  if (isLoading) {
    return <WikiLoadingPage />;
  }
  if (pageQuery.error || wikiQuery.error || repositoryQuery.error) {
    if (wikiQuery.error === PAGE_NOT_FOUND_ERROR) {
      return (
        <>
          <h1>Smeagol</h1>
          <WikiNotFoundError />
        </>
      );
    }

    if (isCreateMode(props)) {
      return (
        <div>
          {wikiHeader}
          <PageEditor
            repository={repository}
            branch={branch}
            path={path}
            content=""
            onSave={(message, content) => {
              createPageMutation.mutate({ message: message, content: content });
            }}
            onAbort={onAbortCreate}
          />
        </div>
      );
    }

    if (createPageMutation.error) {
      return <WikiAlertPage i18nKey={"page_failed_to_create"} />;
    }

    if (pageQuery.error === PAGE_NOT_FOUND_ERROR) {
      return <Redirect to={"?create=true"} />;
    }

    if (repositoryQuery.error) {
      return <WikiAlertPage i18nKey={"error_in_repository_query_for_branches_data"} />;
    }
    // any other errors are caught here
    return <WikiAlertPage i18nKey={"page_failed_to_fetch"} />;
  }
  // no error but no data
  if (!pageQuery.data || !wikiQuery.data || !repositoryQuery.data) {
    return (
      <div>
        <h1>Smeagol</h1>
      </div>
    );
  }

  const wiki = {
    ...wikiQuery.data,
    repository,
    branch
  };

  if (
    editPageMutation.error ||
    createPageMutation.error ||
    renamePageMutation.error ||
    deletePageMutation.error ||
    restorePageMutation.error
  ) {
    return <WikiAlertPage i18nKey={"page_failed_to_modify"} />;
  }

  if (isEditMode(props)) {
    return (
      <div>
        {wikiHeader}
        <PageEditor
          repository={repository}
          branch={branch}
          path={pageQuery.data.path}
          content={pageQuery.data.content}
          onSave={(message, content) => {
            editPageMutation.mutate({ message: message, content: content });
          }}
          onAbort={onAbortEdit}
        />
      </div>
    );
  }
  let pagesLink = "#";
  let historyLink = "#";
  if (wiki.directory) {
    pagesLink = `/${repository}/${branch}/pages/${wiki.directory}`;
    historyLink = `/${repository}/${branch}/history/${path}`;
    // TODO check for polyfill
    if (!pagesLink.endsWith("/")) {
      pagesLink += "/";
    }
  }

  if (repositoryQuery.isError) {
    return <WikiAlertPage i18nKey={"could_not_fetch_branch_info"} />;
  }

  return (
    <>
      <div>
        {wikiHeader}
        <PageViewer
          page={pageQuery.data}
          wiki={wiki}
          onDelete={deletePage}
          onHome={pushLandingPageState}
          onMove={onMove}
          pagesLink={pagesLink}
          historyLink={historyLink}
          onRestore={onRestore}
          pushBranchStateFunction={pushBranchState}
          branch={branch}
          branches={repositoryQuery.data._embedded.branches}
        />
      </div>
    </>
  );
};
export default Page;

function isEditMode(props): boolean {
  const queryParams = queryString.parse(props.location.search);
  return queryParams.edit === "true";
}

function isCreateMode(props): boolean {
  const queryParams = queryString.parse(props.location.search);
  return queryParams.create === "true";
}

function isCommitPage(props): boolean {
  const queryParams = queryString.parse(props.location.search);
  return queryParams.commit !== undefined;
}

function getCommitParameter(props): string {
  if (isCommitPage(props)) {
    const queryParams = queryString.parse(props.location.search);
    return queryParams.commit;
  } else {
    return "";
  }
}

function findPagePath(props) {
  const { pathname } = props.location;
  const parts = pathname.split("/");
  return parts.slice(3).join("/");
}

export function getPageNameFromPath(path: string) {
  const parts = path.split("/");
  return parts[parts.length - 1];
}

export function getDirectoryFromPath(path: string) {
  const parts = path.split("/");
  return parts.slice(0, parts.length - 1).join("/");
}
