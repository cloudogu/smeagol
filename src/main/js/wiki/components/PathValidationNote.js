//@flow
import React from 'react';
import classNames from "classnames";
import injectSheet from "react-jss";
import {OverlayTrigger} from "react-bootstrap";
import PathValidationPopover from "./PathValidationPopover";

const styles = {
    help: {
        fontSize: '150%'
    }
};

class PathValidationNote extends React.Component {

    render() {
        const {classes} = this.props;

        const popover = <PathValidationPopover/>;

        return (
            <OverlayTrigger placement="bottom" overlay={popover} shouldUpdatePosition={true}>
                <i className={classNames("glyphicon", "glyphicon-info-sign", "text-info", "control-label", classes.help)}></i>
            </OverlayTrigger>
        );
    }

}

export default injectSheet(styles)(PathValidationNote);
