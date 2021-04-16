import React from "react";
import { translate } from "react-i18next";
import { Popover } from "react-bootstrap";

type Props = {
  t: any;
};

class InitWikiPopover extends React.Component<Props> {
  render() {
    const { t } = this.props;
    return (
      <Popover {...this.props} title={t("init-wiki-note_title")}>
        <p>{t("init-wiki-note_description")}</p>
      </Popover>
    );
  }
}

export default translate()(InitWikiPopover);
