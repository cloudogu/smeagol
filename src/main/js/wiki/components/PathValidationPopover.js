//@flow
import React from 'react';
import {translate} from "react-i18next";
import {Popover} from "react-bootstrap";

type Props = {
    t: any;
};

class PathValidationPopover extends React.Component<Props> {

    render() {
        const {t} = this.props;
        return (
            <Popover {...this.props} title={t('path-validation-note_title')}>
                <ul>
                    <li>{t('path-validation-note_valid')}</li>
                    <li>{t('path-validation-note_invalid')}</li>
                </ul>
            </Popover>
        );
    }

}

export default translate()(PathValidationPopover);
