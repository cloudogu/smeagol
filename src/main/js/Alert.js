//@flow
import React from "react";
import classNames from "classnames";

type Props = {
  type: string
};

class Alert extends React.Component<Props> {
  static defaultProps = {
    type: "danger"
  };

  render() {
    const { type } = this.props;

    return <div className={classNames("alert", "alert-" + type)}>{this.props.children}</div>;
  }
}

export default Alert;
