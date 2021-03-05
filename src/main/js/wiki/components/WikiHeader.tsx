import React, { FC } from "react";
import { WikiDto } from "../types/wikiDto";
import Breadcrumb, { Entry } from "./Breadcrumb";

type Props = {
  wiki: WikiDto;
  repository: string;
  branch: string;
  pageName?: string;
  directory?: string;
};

const WikiHeader: FC<Props> = (props) => {
  const { wiki, repository, branch, directory, pageName } = props;
  const entries: Entry[] = [];
  const createDirectoryLink = (path: string) => {
    const { repository, branch } = props;
    return `/${repository}/${branch}/pages/${path}`;
  };

  const landingPage = `/${repository}/${branch}/${wiki.landingPage}`;
  entries.push({ name: wiki.repositoryName, link: landingPage });

  if (directory) {
    const parts = directory.split("/");

    let currentPath = "";
    for (const part of parts) {
      currentPath += part + "/";
      entries.push({
        name: part,
        link: createDirectoryLink(currentPath)
      });
    }
  }

  if (pageName) {
    const pageLocation = `/${repository}/${branch}/${directory}/${pageName}`;
    entries.push({ name: pageName, link: pageLocation });
  }

  return <Breadcrumb entries={entries} />;
};

export default WikiHeader;
