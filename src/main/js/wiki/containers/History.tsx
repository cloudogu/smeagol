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

type Params = {
  repository: string;
  branch: string;
};

type Props = {
  t: (string) => string;
  match: match<Params>;
  location: Location;
};

const History: FC<Props> = (props) => {
  const { repository, branch } = props.match.params;
  const page = findDirectoryPath(props);
  const pageName = getPageNameFromPath(page);
  const directory = getDirectoryFromPath(page);

  const pageHistoryQuery = usePageHistory(repository, branch, page);
  const wikiQuery = useWiki(repository, branch);
  const pagePath = `/${repository}/${branch}/${page}`;
  if (pageHistoryQuery.error || wikiQuery.error) {
    return <WikiAlertPage i18nKey={"directory_failed_to_fetch"} />;
  }
  if (pageHistoryQuery.isLoading || wikiQuery.isLoading) {
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
      <div className="page-header">
        <h1>{props.t("history_heading") + page}</h1>
      </div>
      <CommitsTable commits={pageHistoryQuery.data.commits} pagePath={pagePath} />
    </div>
  );
};

function findDirectoryPath(props) {
  const { pathname } = props.location;
  const parts = pathname.split("/");
  return parts.slice(4).join("/");
}

export default translate()(History);
