import React from "react";
import { translate } from "react-i18next";
import { Popover } from "react-bootstrap";

type Props = {
  t: any;
  prefix: string;
};

class ToolTipPopover extends React.Component<Props> {
  render() {
    const { t, prefix } = this.props;
    return (
      <Popover {...this.props} title={t(prefix + "-note_title")}>
        <p>{t(prefix + "-note_description")}</p>
      </Popover>
    );
  }
}

export default translate()(ToolTipPopover);
