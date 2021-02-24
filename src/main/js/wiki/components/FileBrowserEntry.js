//@flow
import React from "react";
import injectSheet from "react-jss";
import classNames from "classnames";
import { Link } from "react-router-dom";

const styles = {
  spacing: {
    paddingRight: "5px"
  }
};

type Props = {
  createLink: (file: any) => string,
  file: any,
  classes: any
};

class FileBrowserEntry extends React.Component<Props> {
  isDirectory(file: any) {
    return file.type === "directory";
  }

  createIcon(file: any) {
    if (this.isDirectory(file)) {
      return "folder-open";
    }
    return "file";
  }

  render() {
    const { file, classes } = this.props;
    const icon = this.createIcon(file);
    const link = this.props.createLink(file);

    return (
      <Link to={link}>
        <i className={classNames("glyphicon", "glyphicon-" + icon, classes.spacing)} /> {file.name}
      </Link>
    );
  }
}

export default injectSheet(styles)(FileBrowserEntry);
