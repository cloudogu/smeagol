//@flow
import React from 'react';
import injectSheet from 'react-jss';
import classNames from 'classnames';
import { translate } from 'react-i18next';

const styles = {
    infoBox: {
        backgroundColor: '#e7eff3'
    }
};

type Props = {
    classes: any;
}

class GeneralInformation extends React.Component<Props> {

    render() {
        const { classes, t } = this.props;
        return (
            <div className={classNames('alert', classes.infoBox)}>
                <p>{t('general-information_welcome')}</p>
                <p dangerouslySetInnerHTML={{__html: t('general-information_description')}} />
            </div>
        );
    }

}

export default translate()(injectSheet(styles)(GeneralInformation));
