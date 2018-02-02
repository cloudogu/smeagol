//@flow
import React from 'react';
import {translate} from 'react-i18next';
import injectSheet from 'react-jss';
import classNames from 'classnames';

const styles = {
    button: {
        marginRight: '5px'
    }
};

type Props = {
    onClick: Function,
    i18nKey: string,
    type?: string
}

class ActionButton extends React.Component<Props> {

    static defaultProps = {
        type: 'default'
    };

    render() {
        const { onClick, i18nKey, type, classes, t } = this.props;
        const typeClass = 'btn-' + type;
        return (
            <button className={classNames('btn', typeClass, classes.button)} onClick={onClick}>
                {t(i18nKey)}
            </button>
        );
    }

}

export default injectSheet(styles)(translate()(ActionButton));
