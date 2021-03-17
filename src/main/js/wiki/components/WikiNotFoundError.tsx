import React from "react";
import { translate } from "react-i18next";
import Alert from "../../Alert";

type Props = {
  t: (string) => string;
};

class WikiNotFoundError extends React.Component<Props> {
  render() {
    const { t } = this.props;
    return (
      <Alert type="warning">
        <p>{t("wikiroot_not_found_prefix")}</p>
        <ul>
          <li>{t("wikiroot_not_found_reason_no_repository")}</li>
          <li>{t("wikiroot_not_found_reason_wrong_type")}</li>
          <li>{t("wikiroot_not_found_reason_permissions")}</li>
          <li>{t("wikiroot_not_found_reason_missing_conf")}</li>
        </ul>
      </Alert>
    );
  }
}

export default translate()(WikiNotFoundError);
