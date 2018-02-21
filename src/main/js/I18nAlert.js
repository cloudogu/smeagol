//@flow
import React from 'react';
import Alert from './Alert';
import {translate} from 'react-i18next';

type Props = {
    t: any,
    i18nKey: string
};

class I18nAlert extends React.Component<Props> {

    render() {
        const { t, i18nKey } = this.props;

        return (
            <Alert>{t(i18nKey)}</Alert>
        );
    }

}

export default translate()(I18nAlert);
