import React from "react";
import injectSheet from "react-jss";
import { FoldingCube } from "better-react-spinkit";

const styles = {
  wrapper: {
    width: "100%",
    display: "flex",
    height: "10em"
  },
  loading: {
    margin: "auto",
    textAlign: "center"
  }
};

type Props = {
  classes: any;
};

class Loading extends React.Component<Props> {
  render() {
    const { classes } = this.props;
    return (
      <div className={classes.wrapper}>
        <div className={classes.loading}>
          <FoldingCube size={100} color="#00426B" />
          Loading ...
        </div>
      </div>
    );
  }
}

export default injectSheet(styles)(Loading);
