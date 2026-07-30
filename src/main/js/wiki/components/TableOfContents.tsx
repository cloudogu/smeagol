import React from "react";
import injectSheet from "react-jss";
import { withTranslation } from "react-i18next";
import tocbot from "tocbot";
import { WIDTH_BOUNDARY } from "./PageViewer";

const cloudoguDarkBlue = "#00426b";
const cloudoguLightGray = "#fff";

const styles = {
  tocHidden: {
    "& ~ .js-toc": { display: "none !important" },
    "& > i.glyphicon-chevron-down": { display: "none !important" }
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
    "display": "flex",
    "align-items": "center",
    "gap": "0.25rem",
    "& ~ ol > li": {
      padding: "0",
      "font-size": "1.4rem"
    },
    "& ~ i": {
      "font-size": "1.4rem",
      "margin-left": "0.5rem"
    }
  },
  main: {
    padding: "1.5rem",
    "@media (max-width: 901px)": { "border-bottom": "1px solid #ddd" },
    color: cloudoguDarkBlue,
    "background-color": cloudoguLightGray
  },
  list: {
    margin: "0",
    "list-style": "none",
    color: "inherit",
    "& .toc-list": { color: "inherit", listStyle: "none", paddingLeft: "1rem" },
    "& .toc-item": { paddingTop: "0.75rem", wordBreak: "break-word", hyphens: "auto" }
  }
};

type Props = {
  page: any;
  classes: any;
  screenWidth: any;
  t: any;
};

class TableOfContents extends React.Component<Props> {
  state = {
    collapsed: true
  };

  componentDidMount() {
    setTimeout(() => {
      this.initToc();
    }, 200);
  }

  componentDidUpdate(prevProps: Props) {
    if (prevProps.page.content !== this.props.page.content) {
      tocbot.refresh();
    }
  }

  componentWillUnmount() {
    tocbot.destroy();
  }

  initToc() {
    tocbot.init({
      tocSelector: ".js-toc",
      contentSelector: ".toastui-editor-contents",
      headingSelector: "h1, h2, h3, h4, h5, h6",
      hasInnerContainers: true,
      activeLinkClass: "is-active-link",
      listClass: "toc-list",
      listItemClass: "toc-item"
    });
  }

  handleToggle = () => {
    this.setState({ collapsed: !this.state.collapsed });
  };

  render() {
    const { classes, screenWidth, t } = this.props;
    const { collapsed } = this.state;

    return (
      <div className={classes.main}>
        {screenWidth < WIDTH_BOUNDARY && (
          <button
            onClick={this.handleToggle}
            className={[classes.tocToggle, collapsed ? classes.tocVisible : classes.tocHidden].join(" ")}
            aria-expanded={!collapsed}
            title={t("table-of-contents")}
          >
            {t("table-of-contents")}
            <i className="glyphicon glyphicon-chevron-down" />
            <i className="glyphicon glyphicon-chevron-right" />
          </button>
        )}
        <div className={["js-toc", classes.list].join(" ")} />
      </div>
    );
  }
}

export default withTranslation()(injectSheet(styles)(TableOfContents));
