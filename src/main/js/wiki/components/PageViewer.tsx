import React from "react";
import PageContent from "./PageContent";
import PageHeader from "./PageHeader";
import PageFooter from "./PageFooter";

type Props = {
  page: any;
  wiki: any;
  pagesLink: string;
  historyLink: string;
  onDelete: () => void;
  onHome: () => void;
  onMove: (target: string) => void;
  onRestore: (pagePath: string, commit: string) => void;
  search: (arg0: string) => void;
};

class PageViewer extends React.Component<Props> {
  render() {
    const { page, wiki, onDelete, onHome, onMove, pagesLink, search, historyLink, onRestore } = this.props;
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
          search={search}
        />
        <PageContent page={page} />
        <PageFooter page={page} />
      </div>
    );
  }
}

export default PageViewer;
