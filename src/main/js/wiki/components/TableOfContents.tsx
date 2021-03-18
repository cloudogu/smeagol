import React from "react";
import injectSheet from "react-jss";
import { translate } from "react-i18next";
import ReactMarkdownHeading from "react-markdown-heading";

const cloudoguDarkBlue = "#00426b";
const cloudoguLightGray = "#fff";

const styles = {
  tocHidden: {
    "& ~ ul": {
      display: "none !important"
    },
    "& > i.glyphicon-chevron-down": {
      display: "none !important"
    }
  },
  tocVisible: {
    "& > i.glyphicon-chevron-right": {
      display: "none !important"
    }
  },
  tocToggle: {
    cursor: "pointer",
    "user-select": "none",
    color: "inherit",
    "font-size": "1.8rem",
    border: "none",
    "background-color": "transparent",
    "padding-left": "0",
    "& ~ ul > li": {
      padding: "0",
      "font-size": "1.8rem"
    },
    "& ~ i": {
      "font-size": "1.4rem",
      "margin-left": "0.5rem"
    }
  },
  main: {
    padding: "1rem",
    "border-bottom": "1px solid #ddd",
    color: cloudoguDarkBlue,
    "background-color": cloudoguLightGray
  },
  list: {
    margin: "0",
    "list-style": "none",
    color: "inherit",
    "padding-left": "1em"
  },
  item: {
    color: "inherit",
    "font-size": "1.4rem",
    "padding-top": "0.5rem"
  }
};

type Props = {
  page: any;
  classes: any;
  t: any;
};

class TableOfContents extends React.Component<Props> {
  collapsed: boolean;

  constructor(props) {
    super(props);
    this.collapsed = true;
  }

  handleToggle = () => {
    this.collapsed = !this.collapsed;
    this.forceUpdate();
  };

  render() {
    const { page, classes, t } = this.props;
    return (
      <div className={classes.main}>
        <button
          onClick={this.handleToggle}
          className={[classes.tocToggle, this.collapsed ? classes.tocHidden : classes.tocVisible].join(" ")}
          aria-expanded={[this.collapsed ? "false" : "true"]}
          title={t("table-of-contents")}
        >
          {t("table-of-contents")}
          <i className="glyphicon glyphicon-chevron-down" />
          <i className="glyphicon glyphicon-chevron-right" />
        </button>
        <ReactMarkdownHeading
          hyperlink={true}
          markdown={page.content}
          ulClassName={classes.list}
          liClassName={classes.item}
        />
      </div>
    );
  }
}

export default translate()(injectSheet(styles)(TableOfContents));
