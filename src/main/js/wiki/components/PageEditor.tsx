import React from "react";
import MarkdownEditor from "./MarkdownEditor";

type Props = {
  repository: string;
  branch: string;
  path: string;
  content: string;
  onSave: (...args: Array<any>) => any;
  onAbort: () => void;
};

class PageEditor extends React.Component<Props> {
  render() {
    const { repository, branch, path, content, onSave, onAbort } = this.props;
    return (
      <div>
        <MarkdownEditor
          repository={repository}
          branch={branch}
          path={path}
          content={content}
          onSave={onSave}
          onAbortClick={onAbort}
        />
      </div>
    );
  }
}

export default PageEditor;
