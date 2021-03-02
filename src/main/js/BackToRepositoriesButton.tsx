import React from "react";
import { Link } from "react-router-dom";
import { translate } from "react-i18next";

type Props = any;

class BackToRepositoriesButton extends React.Component<Props> {
  render() {
    const { t } = this.props;

    return (
      <Link className="btn btn-default" to="/">
        {t("back-to-repositories-button_text")}
      </Link>
    );
  }
}

export default translate()(BackToRepositoriesButton);
