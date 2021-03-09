import React, { FC } from "react";
import { useEditPage, useCreatePage, usePage, useRenamePage, useDeletePage, useRestorePage } from "../hooks/page";
import { useWiki } from "../hooks/wiki";
import PageViewer from "../components/PageViewer";
import * as queryString from "query-string";
import PageEditor from "../components/PageEditor";
import Loading from "../../Loading";
import I18nAlert from "../../I18nAlert";
import { PAGE_NOT_FOUND_ERROR } from "../../apiclient";
import { match } from "react-router";
import WikiHeader from "../components/WikiHeader";

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

  const pushLandingPageState = () => {
    pushPageState(wikiQuery.data.landingPage);
  };

  const path = findPagePath(props);
  const pageName = getPageNameFromPath(path);
  const directory = getDirectoryFromPath(path);

  const pageQuery = usePage(repository, branch, path, getCommitParameter(props));
  const wikiQuery = useWiki(repository, branch);

  const editPageMutation = useEditPage(repository, branch, path);
  const deletePageMutation = useDeletePage(repository, branch, path, pushLandingPageState);
  const createPageMutation = useCreatePage(repository, branch, path);
  const renamePageMutation = useRenamePage(repository, branch, path, pushPageState);
  const restorePageMutation = useRestorePage(repository, branch, path, pushPageState);

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
    return (
      <div>
        <h1>Smeagol</h1>
        <Loading />
      </div>
    );
  } else if (pageQuery.error === PAGE_NOT_FOUND_ERROR || wikiQuery.error === PAGE_NOT_FOUND_ERROR) {
    return (
      <div>
        {wikiHeader}
        <PageEditor
          path={path}
          content=""
          onSave={(message, content) => {
            createPageMutation.mutate({ message: message, content: content });
          }}
          onAbort={onAbortCreate}
        />
      </div>
    );
  } else if (pageQuery.error || wikiQuery.error) {
    return (
      <div>
        <h1>Smeagol</h1>
        <I18nAlert i18nKey="page_failed_to_fetch" />
      </div>
    );
  } else if (!pageQuery.data || !wikiQuery.data) {
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
    return (
      <div>
        <h1>Smeagol</h1>
        <I18nAlert i18nKey="page_failed_to_modify" />
      </div>
    );
  }

  if (isEditMode(props)) {
    return (
      <div>
        {wikiHeader}
        <PageEditor
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

  return (
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
      />
    </div>
  );
};
export default Page;

function isEditMode(props): boolean {
  const queryParams = queryString.parse(props.location.search);
  return queryParams.edit === "true";
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
