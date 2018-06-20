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
    type?: string,
    disabled: boolean
}

class ActionButton extends React.Component<Props> {

    static defaultProps = {
        type: 'default'
    };

    handleClick = (e: Event) => {
        // use prevent default to avoid a complete page reload, if the button is used within a form
        e.preventDefault();
        this.props.onClick();
    };

    render() {
        const { i18nKey, type, classes, t, disabled } = this.props;
        const typeClass = 'btn-' + type;
        return (
            <button disabled={disabled} className={classNames('btn', typeClass, classes.button)} onClick={this.handleClick}>
                {t(i18nKey)}
            </button>
        );
    }

}

export default injectSheet(styles)(translate()(ActionButton));
