//@flow
import React from "react";
import injectSheet from "react-jss";
import DateFromNow from "../../DateFromNow";
import ShortCommitHash from "./ShortCommitHash";

const styles = {
  commitTableTr: {
    backgroundColor: "#e7eff3"
  },
  commitTableTd: {
    border: "1px solid #b9d1dc !important",
    fontSize: "1em",
    lineHeight: "1.6em",
    verticalAlign: "middle !important"
  },
  dateColor: {
    color: "#999"
  }
};

type Props = {
  commit: any,
  classes: any,
  key: any,
  pagePath: string
};

class CommitsTableEntry extends React.Component<Props> {
  render() {
    const { commit, key, classes, pagePath } = this.props;
    return (
      <tr className={classes.commitTableTr} key={key}>
        <td className={classes.commitTableTd}>
          <b>{commit.author.displayName}</b>
        </td>
        <td className={classes.commitTableTd}>
          <span className={classes.dateColor}>
            <DateFromNow date={commit.date} />:{" "}
          </span>
          {commit.message} [<ShortCommitHash commit={commit} pagePath={pagePath} />]
        </td>
      </tr>
    );
  }
}

export default injectSheet(styles)(CommitsTableEntry);
