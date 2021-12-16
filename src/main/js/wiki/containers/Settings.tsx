import React, { FC, useState } from "react";
import { translate } from "react-i18next";
import { match } from "react-router";
import WikiHeader from "../components/WikiHeader";
import { useEditWiki, useWiki } from "../hooks/wiki";
import WikiLoadingPage from "../components/WikiLoadingPage";
import ActionButton from "../components/ActionButton";
import WikiNotFoundError from "../components/WikiNotFoundError";
import WikiAlertPage from "../components/WikiAlertPage";
import SettingsInputField from "../components/SettingsInputField";
import { isValidRelativePath } from "../../pathUtil";
import PageHeader from "../components/ActionHeader";

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

  const changeToWikiRoot = () => {
    props.history.push(`/${repository}/${branch}`);
  };

  if (editWikiMutation.isSuccess) {
    changeToWikiRoot();
  }

  if (wikiQuery.isLoading || editWikiMutation.isLoading) {
    return <WikiLoadingPage />;
  }

  if (wikiQuery.error) {
    return <WikiNotFoundError />;
  }

  if (editWikiMutation.error) {
    return <WikiAlertPage i18nKey={"wiki_failed_to_edit"} />;
  }

  const rootDirValid = isValidRelativePath(rootDir);
  const landingPageValid = isValidRelativePath(landingPage);
  const allValid = rootDirValid && landingPageValid;
  let historyLink = "#";

  function findDirectoryPath(props) {
    const { pathname } = props.location;
    const parts = pathname.split("/");
    return parts.slice(4).join("/");
  }

  const path = findDirectoryPath(props);

  if (wikiQuery.data.directory) {
    historyLink = `/${repository}/${branch}/history/${path}`;
  }
  const wiki = { ...wikiQuery.data, branch, repository };

  return (
    <div>
      <WikiHeader branch={branch} repository={repository} wiki={wikiQuery.data} />
      <hr />
      <PageHeader wiki={wiki} historyLink={historyLink} inSettings={true} />
      <hr />
      <div className="page-header">
        <h1>{props.t("settings_heading")}</h1>
      </div>
      <SettingsInputField
        prefix={"settings-rootDir"}
        setParentState={setRootDir}
        initValue={wikiQuery.data.directory}
        isValid={rootDirValid}
      />
      <SettingsInputField
        prefix={"settings-landingPage"}
        setParentState={setLandingPage}
        initValue={wikiQuery.data.landingPage.substr(wikiQuery.data.directory.length + 1)}
        isValid={landingPageValid}
      />
      <hr />
      <div>
        <ActionButton
          i18nKey="settings_save"
          type="primary"
          onClick={() => {
            editWikiMutation.mutate({ landingPage: landingPage, rootDir: rootDir });
          }}
          disabled={!allValid}
        />
        <ActionButton i18nKey="settings_abort" onClick={changeToWikiRoot} />
      </div>
    </div>
  );
};

export default translate()(Settings);
