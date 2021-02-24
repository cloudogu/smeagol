//@flow
import React from "react";
import I18nAlert from "../../I18nAlert";
import Loading from "../../Loading";
import { translate } from "react-i18next";
import { usePageHistory } from "../modules/pagehistory";
import CommitsTable from "../components/CommitsTable";
import ActionLink from "../components/ActionLink";

type Props = {
  t: any,
  match: any,
  location: any
};

function History(props: Props) {
  const { repository, branch } = props.match.params;
  const page = findDirectoryPath(props);

  const pageHistoryQuery = usePageHistory(repository, branch, page);
  const pagePath = `/${repository}/${branch}/${page}`;
  if (pageHistoryQuery.error) {
    return (
      <div>
        <h1>Smeagol</h1>
        <I18nAlert i18nKey="directory_failed_to_fetch" />
      </div>
    );
  } else if (pageHistoryQuery.isLoading) {
    return (
      <div>
        <h1>Smeagol</h1>
        <Loading />
      </div>
    );
  } else if (!pageHistoryQuery.data) {
    return (
      <div>
        <h1>Smeagol</h1>
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <h1>{props.t("history_heading") + page}</h1>
        <ActionLink to={pagePath} i18nKey="history-header_show_page" type="primary" />
      </div>
      <CommitsTable commits={pageHistoryQuery.data.commits} pagePath={pagePath} />
    </div>
  );
}

function findDirectoryPath(props) {
  const { pathname } = props.location;
  const parts = pathname.split("/");
  return parts.slice(4).join("/");
}

export default translate()(History);
