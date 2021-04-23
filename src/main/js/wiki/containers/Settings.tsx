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
import { isValidPath } from "../../pathUtil";

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

type Configuration = {
  rootDir: string;
  landingPage: string;
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

  return (
    <div>
      <WikiHeader branch={branch} repository={repository} wiki={wikiQuery.data} />
      <hr />
      <div className="page-header">
        <h1>{props.t("settings_heading")}</h1>
      </div>
      <SettingsInputField
        prefix={"settings-rootDir"}
        setParentState={setRootDir}
        initValue={wikiQuery.data.directory}
      />
      <SettingsInputField
        prefix={"settings-landingPage"}
        setParentState={setLandingPage}
        initValue={wikiQuery.data.landingPage.substr(wikiQuery.data.directory.length + 1)}
      />
      <hr />
      <div>
        <ActionButton
          i18nKey="settings_save"
          type="primary"
          onClick={() => {
            editWikiMutation.mutate({ landingPage: landingPage, rootDir: rootDir });
          }}
          disabled={
            !settingsValid({
              landingPage: landingPage,
              rootDir: rootDir
            })
          }
        />
        <ActionButton i18nKey="settings_abort" onClick={changeToWikiRoot} />
      </div>
    </div>
  );
};

/**
 * Checks if the entered configuration is valid.
 */
function settingsValid(configuration: Configuration) {
  const rootDirValid = isValid(configuration.rootDir);
  const landingPageValid = isValid(configuration.landingPage);
  return rootDirValid && landingPageValid;
}

/**
 * Validates that the given path is a valid path.
 * Also validates that the path does not start with '.' or '/'
 * @param path The path to check
 */
function isValid(path: string): boolean {
  let result = isValidPath(path);

  result = result && !path.startsWith("/");
  result = result && !path.startsWith(".");

  return result;
}

export default translate()(Settings);
