// @flow
import React from "react";
import GeneralInformation from "../components/GeneralInformation";
import RepositoryList from "../components/RepositoryList";

import Loading from "../../Loading";
import I18nAlert from "../../I18nAlert";
import { useRepositories } from "../modules/repositories";

export default function Repositories() {
  const { isLoading, isError, data } = useRepositories();

  let child = <div />;
  if (isError) {
    child = <I18nAlert i18nKey="repositories_failed_to_fetch" />;
  } else if (isLoading) {
    child = <Loading />;
  } else if (data) {
    child = <RepositoryList repositories={data} />;
  }

  return (
    <div>
      <h1>Smeagol</h1>
      <GeneralInformation />
      <h2>Wikis</h2>
      {child}
    </div>
  );
}
