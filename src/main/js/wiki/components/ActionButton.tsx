import React from "react";
import { translate } from "react-i18next";
import injectSheet from "react-jss";
import classNames from "classnames";

const styles = {
  button: {
    marginRight: "5px"
  },
  menuElement: {
    border: "none"
  }
};

type Props = {
  onClick: (...args: Array<any>) => any;
  i18nKey: string;
  type?: string;
  disabled: boolean;
  glyphicon: string;
};

class ActionButton extends React.Component<Props> {
  static defaultProps = {
    type: "default"
  };

  handleClick = (e: Event) => {
    // use prevent default to avoid a complete page reload, if the button is used within a form
    e.preventDefault();
    this.props.onClick();
  };

  render() {
    const { i18nKey, type, classes, t, disabled, glyphicon } = this.props;

    let btnType = type;
    let additionalClasses;
    if (type === "menu") {
      btnType = "default";
      additionalClasses = classes.menuElement;
    }

    const typeClass = "btn-" + btnType;
    let icon: JSX.Element;
    if (glyphicon) {
      icon = <span className={classNames("glyphicon", glyphicon)}></span>;
    }
    return (
      <button
        disabled={disabled}
        className={classNames("btn", typeClass, classes.button, additionalClasses)}
        onClick={this.handleClick}
      >
        {icon}
        {t(i18nKey)}
      </button>
    );
  }
}

export default injectSheet(styles)(translate()(ActionButton));
