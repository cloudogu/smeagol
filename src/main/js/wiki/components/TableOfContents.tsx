import React from "react";
import injectSheet from "react-jss";
import ReactMarkdownHeading from "react-markdown-heading";

const styles = {
  tocHidden: {
    "& + ul": {
      display: "none"
    }
  },
  tocToggle: {
    cursor: "pointer",
    "user-select": "none"
  }
};

type Props = {
  page: any;
  classes: any;
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
    const { page, classes } = this.props;
    return (
      <div>
        <a
          onClick={this.handleToggle}
          className={[classes.tocToggle, this.collapsed ? classes.tocHidden : ""].join(" ")}
        >
          Table of Contents
        </a>
        <ReactMarkdownHeading markdown={page.content} hyperlink={true} />
      </div>
    );
  }
}

export default injectSheet(styles)(TableOfContents);
