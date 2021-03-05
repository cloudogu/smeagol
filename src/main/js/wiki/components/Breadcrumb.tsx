import React from "react";
import injectSheet from "react-jss";
import classNames from "classnames";

const styles = {
  breadcrumb: {
    "background-color": "white"
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
      <div className={classNames("breadcrumb", classes.breadcrumb)}>
        {entries.map((entry) => {
          if (!entry.link) {
            return <li className="active">{entry.name}</li>;
          } else {
            return (
              <li key={entry.name}>
                <a href={entry.link}>{entry.name}</a>
              </li>
            );
          }
        })}
      </div>
    );
  }
}

export default injectSheet(styles)(Breadcrumb);
