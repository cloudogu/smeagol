import React from "react";
import injectSheet from "react-jss";
import FileBrowserEntry from "./FileBrowserEntry";

const styles = {
  files: {
    listStyleType: "none"
  }
};

type Props = {
  createLink: (directory: any, file: any) => string;
  directory: any;
  classes: any;
};

export const orderFiles = (files) => {
  if (!files) {
    return [];
  }
  return files.sort((a, b) => {
    if (a.type === "directory" && b.type !== "directory") {
      return -10;
    } else if (a.type !== "directory" && b.type === "directory") {
      return 10;
    } else if (a.name < b.name) {
      return -1;
    } else if (a.name > b.name) {
      return 1;
    }
    return 0;
  });
};

class FileBrowser extends React.Component<Props> {
  createFileLink = (file: any) => {
    return this.props.createLink(this.props.directory, file);
  };

  render() {
    const children = orderFiles(this.props.directory.children);

    return (
      <ul className="list-unstyled">
        {children.map((file) => {
          return (
            <li key={file.name}>
              <FileBrowserEntry file={file} createLink={this.createFileLink} />
            </li>
          );
        })}
      </ul>
    );
  }
}

export default injectSheet(styles)(FileBrowser);
