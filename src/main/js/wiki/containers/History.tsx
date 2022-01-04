import React, { FC } from "react";
import { translate } from "react-i18next";
import { usePageHistory } from "../hooks/pagehistory";
import CommitsTable from "../components/CommitsTable";
import { match } from "react-router";
import WikiHeader from "../components/WikiHeader";
import { useWiki } from "../hooks/wiki";
import { getDirectoryFromPath, getPageNameFromPath } from "./Page";
import WikiLoadingPage from "../components/WikiLoadingPage";
import WikiAlertPage from "../components/WikiAlertPage";
import { usePage } from "../hooks/page";
import { useRepository } from "../../repository/hooks/useRepository";
import PageHeader from "../components/PageHeader";
import { findDirectoryPath } from "./Settings";

type Params = {
  repository: string;
  branch: string;
};

type Props = {
  t: (string) => string;
  match: match<Params>;
  location: Location;
  history: any;
};

const History: FC<Props> = (props) => {
  const { repository, branch } = props.match.params;
  const page = findDirectoryPath(props.location.pathname);
  const pageName = getPageNameFromPath(page);
  const directory = getDirectoryFromPath(page);
  const pageHistoryQuery = usePageHistory(repository, branch, page);
  const wikiQuery = useWiki(repository, branch);
  const pagePath = `/${repository}/${branch}/${page}`;
  const pageQuery = usePage(repository, branch, page, "", true);
  const repositoryQuery = useRepository(repository, true);
  if (pageHistoryQuery.error || wikiQuery.error || pageQuery.error || repositoryQuery.error) {
    return <WikiAlertPage i18nKey={"directory_failed_to_fetch"} />;
  }
  if (pageHistoryQuery.isLoading || wikiQuery.isLoading || pageQuery.isLoading || repositoryQuery.isLoading) {
    return <WikiLoadingPage />;
  }
  if (!pageHistoryQuery.data) {
    return (
      <div>
        <WikiHeader
          branch={branch}
          repository={repository}
          wiki={wikiQuery.data}
          pageName={pageName}
          directory={directory}
        />
        <hr />
        <h1>Smeagol</h1>
      </div>
    );
  }

  const pushBranchState = (branchName: string, pagePath: string) => {
    props.history.push(`/${repository}/${branchName}/history/${pagePath}`);
  };
  const wiki = { ...wikiQuery.data, branch, repository };
  const pageObject = { path: directory + "/" + pageName };

  return (
    <div>
      <WikiHeader
        branch={branch}
        repository={repository}
        wiki={wikiQuery.data}
        pageName={pageName}
        directory={directory}
      />
      <hr />
      <PageHeader
        wiki={wiki}
        page={pageObject}
        branch={branch}
        branches={repositoryQuery.data._embedded.branches}
        pushBranchStateFunction={pushBranchState}
      />
      <h1>{props.t("history_heading") + page}</h1>
      <CommitsTable commits={pageHistoryQuery.data.commits} pagePath={pagePath} />
    </div>
  );
};

export default translate()(History);
