import React, { FC } from "react";
import FileBrowser from "../components/FileBrowser";
import Breadcrumb from "../components/Breadcrumb";
import { useDirectory } from "../hooks/directory";
import I18nAlert from "../../I18nAlert";
import Loading from "../../Loading";
import { translate } from "react-i18next";
import { match } from "react-router";
import WikiHeader from "../components/WikiHeader";
import { useWiki } from "../hooks/wiki";

type Params = {
  repository: string;
  branch: string;
};

type Props = {
  t: (string) => string;
  match: match<Params>;
  location: Location;
};

const Directory: FC<Props> = (props) => {
  const { repository, branch } = props.match.params;

  const path = findDirectoryPath(props);
  const directoryQuery = useDirectory(repository, branch, path);
  const wikiQuery = useWiki(repository, branch);

  const isLoading = directoryQuery.isLoading || wikiQuery.isLoading;
  const error = directoryQuery.error || wikiQuery.error;

  const createDirectoryLink = (path: string) => {
    const { repository, branch } = props.match.params;
    return `/${repository}/${branch}/pages/${path}`;
  };

  const createPageLink = (path: string) => {
    const { repository, branch } = props.match.params;
    return `/${repository}/${branch}/${path}`;
  };

  const createLink = (directory: any, file: any) => {
    const path = endingSlash(directory.path) + file.name;

    if (file.type === "directory") {
      return createDirectoryLink(endingSlash(path));
    } else if (file.type === "page") {
      return createPageLink(path);
    } else {
      return "#";
    }
  };

  const endingSlash = (value: string) => {
    // TODO check polyfil
    if (!value.endsWith("/")) {
      return value + "/";
    }
    return value;
  };

  if (error) {
    return (
      <div>
        <h1>Smeagol</h1>
        <I18nAlert i18nKey="directory_failed_to_fetch" />
      </div>
    );
  } else if (isLoading) {
    return (
      <div>
        <h1>Smeagol</h1>
        <Loading />
      </div>
    );
  } else if (!directoryQuery.data) {
    return (
      <div>
        <WikiHeader branch={branch} repository={repository} wiki={wikiQuery.data} directory={path} />
        <hr />
        <h1>Smeagol</h1>
      </div>
    );
  }

  return (
    <div>
      <WikiHeader branch={branch} repository={repository} wiki={wikiQuery.data} directory={path} />
      <hr />
      <h1>{props.t("directory_heading")}</h1>
      <FileBrowser directory={directoryQuery.data} createLink={createLink} />
    </div>
  );
};

function findDirectoryPath(props) {
  const { pathname } = props.location;
  const parts = pathname.split("/");
  return parts.slice(4).join("/");
}

export default translate()(Directory);
