import PageContent from "./PageContent";
import PageHeader from "./PageHeader";
import PageFooter from "./PageFooter";
import TableOfContents from "./TableOfContents";
import { Branch } from "../../repository/types/repositoryDto";
import injectSheet from "react-jss";
import StickyBox from "react-sticky-box";
import React from "react";

export const WIDTH_BOUNDARY = 900;

const styles = {
  layout: {
    "@media (min-width: 900px)": {
      display: "flex",
      alignItems: "flex-start",
      "padding-top": "20px",
      "padding-bottom": "20px"
    }
  },
  content: {
    "@media (min-width: 900px)": {
      "padding-left": "20px",
      "border-left": "1px solid #ddd"
    }
  },
  withoutBorder: {
    "@media (min-width: 900px)": {
      "padding-left": "0px",
      "border-left": "none"
    }
  },
  stickyBox: {
    "@media (max-width: 900px)": {
      display: "none"
    },
    "max-width": "200px",
    "min-width": "200px"
  }
};

type Props = {
  page: any;
  wiki: any;
  pagesLink: string;
  historyLink: string;
  onDelete: () => void;
  onHome: () => void;
  onMove: (target: string) => void;
  onRestore: (pagePath: string, commit: string) => void;
  pushBranchStateFunction: (branchName: string, pagePath: string) => void;
  branch: string;
  branches: Branch[];
  classes: any;
};

class PageViewer extends React.Component<Props> {
  constructor(props) {
    super(props);
    this.state = { width: window.innerWidth };
  }

  updateWith = () => {
    this.setState({ width: window.innerWidth });
  };

  componentDidMount() {
    window.addEventListener("resize", this.updateWith);
  }

  componentWillUnmount() {
    window.addEventListener("resize", this.updateWith);
  }

  private static hasMarkdownHeadings(page: any): boolean {
    return /^#\s*(.+?)[ \t]*$/gm.test(page.content);
  }

  render() {
    const {
      page,
      wiki,
      onDelete,
      onHome,
      onMove,
      pagesLink,
      historyLink,
      onRestore,
      pushBranchStateFunction,
      branch,
      branches,
      classes
    } = this.props;
    const { width } = this.state;
    return (
      <div>
        <PageHeader
          page={page}
          wiki={wiki}
          pagesLink={pagesLink}
          historyLink={historyLink}
          onDelete={onDelete}
          onHomeClick={onHome}
          onOkMoveClick={onMove}
          onRestoreClick={onRestore}
          pushBranchStateFunction={pushBranchStateFunction}
          branch={branch}
          branches={branches}
        />
        <div className={classes.layout}>
          {PageViewer.hasMarkdownHeadings(page) && width >= WIDTH_BOUNDARY && (
            <StickyBox offsetTop={55} className={classes.stickyBox}>
              <TableOfContents page={page} screenWidth={width} />
            </StickyBox>
          )}
          {PageViewer.hasMarkdownHeadings(page) && width < WIDTH_BOUNDARY && (
            <TableOfContents page={page} screenWidth={width} />
          )}
          <div
            className={[classes.content, PageViewer.hasMarkdownHeadings(page) ? null : classes.withoutBorder].join(" ")}
          >
            <PageContent page={page} />
          </div>
        </div>
        <PageFooter page={page} />
      </div>
    );
  }
}

export default injectSheet(styles)(PageViewer);
