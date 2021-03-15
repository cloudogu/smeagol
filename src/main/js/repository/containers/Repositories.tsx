import React, { FC, useState } from "react";
import GeneralInformation from "../components/GeneralInformation";
import RepositoryList from "../components/RepositoryList";

import Loading from "../../Loading";
import I18nAlert from "../../I18nAlert";
import { useRepositories } from "../hooks/useRepositories";
import { translate } from "react-i18next";

const Repositories: FC = (props) => {
  const { t } = props;
  const [displayRepositoriesWithouWiki, setDisplayRepositoriesWithouWiki] = useState(false);

  const checkboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDisplayRepositoriesWithouWiki(event.target.checked);
  };

  const { isLoading, error, data } = useRepositories(!displayRepositoriesWithouWiki);

  let child = <div />;
  if (error) {
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
      <label>
        {t("display_repositories_without_wiki")}
        <input
          name="displayWithoutWiki"
          type="checkbox"
          checked={displayRepositoriesWithouWiki}
          onChange={checkboxChange}
        />
      </label>
      {child}
    </div>
  );
};

export default translate()(Repositories);
