import React from "react";
import { Link } from "react-router-dom";
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
  to: string;
  i18nKey: string;
  type?: string;
  glyphicon: string;
};

class ActionLink extends React.Component<Props> {
  static defaultProps = {
    type: "default"
  };

  render() {
    const { to, i18nKey, type, classes, t, glyphicon } = this.props;
    let btnType = type;
    let additionalClasses;
    if (type === "menu") {
      btnType = "default";
      additionalClasses = classes.menuElement;
    }

    const typeClass = "btn-" + btnType;
    let icon: JSX.Element;
    if (glyphicon) {
      icon = (
        <>
          <span className={classNames("glyphicon", glyphicon)} />{" "}
        </>
      );
    }
    return (
      <Link className={classNames("btn", typeClass, classes.button, additionalClasses)} to={to}>
        {icon}
        {t(i18nKey)}
      </Link>
    );
  }
}

export default injectSheet(styles)(translate()(ActionLink));
