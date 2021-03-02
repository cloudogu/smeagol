import React from "react";
import MarkdownEditor from "./MarkdownEditor";

type Props = {
  path: string;
  content: string;
  onSave: (...args: Array<any>) => any;
  onAbort: () => void;
};

class PageEditor extends React.Component<Props> {
  render() {
    const { path, content, onSave, onAbort } = this.props;
    return (
      <div>
        <h1>{path}</h1>
        <MarkdownEditor path={path} content={content} onSave={onSave} onAbortClick={onAbort} />
      </div>
    );
  }
}

export default PageEditor;
