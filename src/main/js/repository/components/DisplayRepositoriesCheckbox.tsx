import React, { FC } from "react";
import { translate } from "react-i18next";
import injectSheet from "react-jss";
import classNames from "classnames";

const styles = {
  position: {
    float: "right"
  }
};

type Props = {
  checkboxChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  displayRepositoriesWithoutWiki: boolean;
};

const DisplayRepositoriesCheckbox: FC<Props> = (props: Props) => {
  const { t, displayRepositoriesWithoutWiki, checkboxChange, classes } = props;
  return (
    <label className={classNames(classes.position)}>
      {t("display_repositories_without_wiki") + " "}
      <input
        name="displayWithoutWiki"
        type="checkbox"
        checked={displayRepositoriesWithoutWiki}
        onChange={checkboxChange}
      />
    </label>
  );
};

export default translate()(injectSheet(styles)(DisplayRepositoriesCheckbox));
