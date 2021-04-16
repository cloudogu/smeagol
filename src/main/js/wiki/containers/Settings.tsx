import React, { FC, useState } from "react";
import { translate } from "react-i18next";
import { match } from "react-router";
import WikiHeader from "../components/WikiHeader";
import { useEditWiki, useWiki } from "../hooks/wiki";
import WikiLoadingPage from "../components/WikiLoadingPage";
import ActionButton from "../components/ActionButton";
import WikiNotFoundError from "../components/WikiNotFoundError";
import WikiAlertPage from "../components/WikiAlertPage";

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

const Settings: FC<Props> = (props) => {
  const { repository, branch } = props.match.params;
  const wikiQuery = useWiki(repository, branch);
  const editWikiMutation = useEditWiki(repository, branch);

  const [rootDir, setRootDir] = useState("");
  const [landingPage, setLandingPage] = useState("");

  if (wikiQuery.isLoading || editWikiMutation.isLoading) {
    return <WikiLoadingPage />;
  }

  if (wikiQuery.error) {
    return <WikiNotFoundError />;
  }

  if (editWikiMutation.error) {
    return <WikiAlertPage i18nKey={"wiki_failed_to_edit"} />;
  }

  if (wikiQuery.data && !rootDir) {
    setRootDir(wikiQuery.data.directory);
  }

  if (wikiQuery.data && !landingPage) {
    const currentLandingPage = wikiQuery.data.landingPage.substr(wikiQuery.data.directory.length + 1);
    setLandingPage(currentLandingPage);
  }

  return (
    <div>
      <WikiHeader branch={branch} repository={repository} wiki={wikiQuery.data} />
      <hr />
      <div className="page-header">
        <h1>{props.t("settings_heading")}</h1>
      </div>
      <label>{props.t("settings_rootDir_label")}</label>
      <input
        type="text"
        className="form-control"
        value={rootDir}
        onChange={(event) => setRootDir(event.target.value)}
      />
      <label>{props.t("settings_landingPage_label")}</label>
      <input
        type="text"
        className="form-control"
        value={landingPage}
        onChange={(event) => setLandingPage(event.target.value)}
      />
      <hr />
      <div>
        <ActionButton
          i18nKey="settings_save"
          type="primary"
          onClick={() => {
            editWikiMutation.mutate({ landingPage: landingPage, rootDir: rootDir });
          }}
        />
        <ActionButton
          i18nKey="settings_abort"
          onClick={() => {
            props.history.push(`/${repository}/${branch}`);
          }}
        />
      </div>
    </div>
  );
};

export default translate()(Settings);
