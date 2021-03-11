import React from "react";
import injectSheet from "react-jss";
import classNames from "classnames";

import { Route, withRouter } from "react-router";

import { Switch } from "react-router-dom";
import Wiki from "./wiki/containers/Wiki";
import RepositoryOverview from "./repository/containers/RepositoryOverview";

const styles = {
  content: {
    paddingTop: "60px"
  }
};

type Props = {
  classes: any;
};

class Main extends React.Component<Props> {
  render() {
    const { classes } = this.props;
    const repositoryOverviewOptions: string[] = ["/", "/:repository"];
    return (
      <div className={classNames("container", classes.content)}>
        <Switch>
          <Route exact path={repositoryOverviewOptions} component={RepositoryOverview} />
          <Route path="/:repository/:branch" component={Wiki} />
        </Switch>
      </div>
    );
  }
}

export default withRouter(injectSheet(styles)(Main));
