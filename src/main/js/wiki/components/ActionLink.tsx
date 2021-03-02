import React from "react";
import { Link } from "react-router-dom";
import { translate } from "react-i18next";
import injectSheet from "react-jss";
import classNames from "classnames";

const styles = {
  button: {
    marginRight: "5px"
  }
};

type Props = {
  to: string;
  i18nKey: string;
  type?: string;
};

class ActionLink extends React.Component<Props> {
  static defaultProps = {
    type: "default"
  };

  render() {
    const { to, i18nKey, type, classes, t } = this.props;
    const typeClass = "btn-" + type;
    return (
      <Link className={classNames("btn", typeClass, classes.button)} to={to}>
        {t(i18nKey)}
      </Link>
    );
  }
}

export default injectSheet(styles)(translate()(ActionLink));
