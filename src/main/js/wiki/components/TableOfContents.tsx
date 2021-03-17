import React from "react";
import injectSheet from "react-jss";
import { translate } from "react-i18next";
import ReactMarkdownHeading from "react-markdown-heading";

const cloudoguDarkBlue = "#00426b";
const cloudoguLightGray = "#f5f5f5";

const styles = {
  tocHidden: {
    "& + ul": {
      display: "none"
    }
  },
  tocToggle: {
    cursor: "pointer",
    "user-select": "none",
    color: "inherit",
    "font-size": "1.8em",
    "& + ul > li": {
      padding: "0",
      "font-size": "1.8em"
    }
  },
  main: {
    padding: "1rem",
    "border-bottom": "1px solid #ddd",
    color: cloudoguDarkBlue
  },
  list: {
    margin: "0",
    "list-style": "none",
    color: "inherit",
    "padding-left": "1em",
    "background-color": cloudoguLightGray
  },
  item: {
    color: "inherit",
    "font-size": "1.4rem"
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
    console.log(this.props);
    return (
      <div className={classes.main}>
        <a
          onClick={this.handleToggle}
          className={[classes.tocToggle, this.collapsed ? classes.tocHidden : ""].join(" ")}
        >
          {t("table-of-contents")}
        </a>
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
