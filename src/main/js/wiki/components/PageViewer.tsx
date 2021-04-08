import React from "react";
import PageContent from "./PageContent";
import PageHeader from "./PageHeader";
import PageFooter from "./PageFooter";
import TableOfContents from "./TableOfContents";
import { Branch } from "../../repository/types/repositoryDto";

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
};

class PageViewer extends React.Component<Props> {
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
      branches
    } = this.props;

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
        <TableOfContents page={page} />
        <PageContent page={page} />
        <PageFooter page={page} />
      </div>
    );
  }
}

export default PageViewer;
