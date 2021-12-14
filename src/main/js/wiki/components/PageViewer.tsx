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
      "padding-top": "2rem",
      "padding-bottom": "2rem"
    }
  },
  content: {
    "@media (min-width: 900px)": {
      "padding-left": "2rem",
      "border-left": "1px solid #ddd"
    },
    display: "inline-block",
    "word-break": "break-word",
    "-ms-hyphens": "auto",
    "-webkit-hyphens": "auto",
    hyphens: "auto"
  },
  withoutBorder: {
    "@media (min-width: 900px)": {
      "padding-left": "0rem",
      "border-left": "none"
    }
  },
  stickyBox: {
    "@media (max-width: 900px)": {
      display: "none"
    },
    "max-width": "22rem",
    "min-width": "22rem"
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
          path={page.path}
          pushBranchStateFunction={pushBranchStateFunction}
          branch={branch}
          branches={branches}
          wiki={wiki}
          pagesLink={pagesLink}
          historyLink={historyLink}
          onDelete={onDelete}
          onHomeClick={onHome}
          onOkMoveClick={onMove}
          onRestoreClick={onRestore}
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
