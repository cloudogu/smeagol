import React from "react";
import injectSheet from "react-jss";
import classNames from "classnames";
import { Link } from "react-router-dom";

const styles = {
  breadcrumb: {
    "background-color": "white",
    "font-weight": "bold",
    "font-size": "medium",
    "padding-top": "11px",
    "padding-bottom": "1px",
    "padding-left": "0px"
  }
};

export type Entry = {
  name: string;
  link: string;
};

type Props = {
  entries: Entry[];
  classes: any;
};

class Breadcrumb extends React.Component<Props> {
  render() {
    const { entries, classes } = this.props;

    return (
      <ul className={classNames("breadcrumb", classes.breadcrumb)}>
        {entries.map((entry) => {
          if (!entry.link) {
            return <li className="active">{entry.name}</li>;
          }
          return (
            <li key={entry.name}>
              <Link to={entry.link}>{entry.name}</Link>
            </li>
          );
        })}
      </ul>
    );
  }
}

export default injectSheet(styles)(Breadcrumb);
