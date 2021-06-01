import React from "react";
import classNames from "classnames";
import injectSheet from "react-jss";
import { OverlayTrigger } from "react-bootstrap";
import ToolTipPopover from "./ToolTipPopover";

const styles = {
  help: {
    fontSize: "100%",
    "margin-right": "5px"
  }
};

type Props = {
  prefix: string;
  classes: any;
};

class ToolTip extends React.Component<Props> {
  render() {
    const { classes, prefix } = this.props;

    const popover = <ToolTipPopover prefix={prefix} />;

    return (
      <OverlayTrigger placement="bottom" overlay={popover} shouldUpdatePosition={true}>
        <i className={classNames("glyphicon", "glyphicon-info-sign", "text-info", "control-label", classes.help)}></i>
      </OverlayTrigger>
    );
  }
}

export default injectSheet(styles)(ToolTip);
