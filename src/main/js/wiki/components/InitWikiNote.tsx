import React from "react";
import classNames from "classnames";
import injectSheet from "react-jss";
import { OverlayTrigger } from "react-bootstrap";
import InitWikiPopover from "./InitWikiPopover";

const styles = {
  help: {
    fontSize: "150%",
    "margin-right": "5px"
  }
};

class InitWikiNote extends React.Component {
  render() {
    const { classes } = this.props;

    const popover = <InitWikiPopover />;

    return (
      <OverlayTrigger placement="bottom" overlay={popover} shouldUpdatePosition={true}>
        <i className={classNames("glyphicon", "glyphicon-info-sign", "text-info", "control-label", classes.help)}></i>
      </OverlayTrigger>
    );
  }
}

export default injectSheet(styles)(InitWikiNote);
