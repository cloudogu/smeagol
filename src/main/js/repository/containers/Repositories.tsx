import React, { FC, useState } from "react";
import GeneralInformation from "../components/GeneralInformation";
import RepositoryList from "../components/RepositoryList";

import Loading from "../../Loading";
import I18nAlert from "../../I18nAlert";
import { useRepositories } from "../hooks/useRepositories";
import DisplayRepositoriesCheckbox from "../components/DisplayRepositoriesCheckbox";
import { MISSING_SMEAGOL_PLUGIN } from "../../apiclient";

const Repositories: FC = () => {
  const [displayRepositoriesWithoutWiki, setDisplayRepositoriesWithouWiki] = useState(false);

  const checkboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDisplayRepositoriesWithouWiki(event.target.checked);
  };

  const { isLoading, error, data } = useRepositories(!displayRepositoriesWithoutWiki);

  let child = <div />;
  if (error === MISSING_SMEAGOL_PLUGIN) {
    child = <I18nAlert i18nKey="missing_smeagol_plugin" />;
  } else if (error) {
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
      <DisplayRepositoriesCheckbox
        checkboxChange={checkboxChange}
        displayRepositoriesWithoutWiki={displayRepositoriesWithoutWiki}
      />
      <h2>Wikis</h2>
      {child}
    </div>
  );
};

export default Repositories;
