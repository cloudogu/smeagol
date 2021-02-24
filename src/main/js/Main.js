//@flow
import React from "react";
import injectSheet from "react-jss";
import classNames from "classnames";

import { Route, withRouter } from "react-router";

import Repositories from "./repository/containers/Repositories";
import Branches from "./repository/containers/Branches";
import WikiRoot from "./wiki/containers/WikiRoot";
import Page from "./wiki/containers/Page";
import { Switch } from "react-router-dom";
import Directory from "./wiki/containers/Directory";
import History from "./wiki/containers/History";
import Search from "./wiki/containers/Search";

const styles = {
  content: {
    paddingTop: "60px"
  }
};

type Props = {
  classes: any
};

class Main extends React.Component<Props> {
  render() {
    const { classes } = this.props;
    return (
      <div className={classNames("container", classes.content)}>
        <Switch>
          <Route exact path="/" component={Repositories} />
          <Route exact path="/:repository" component={Branches} />
          <Route exact path="/:repository/:branch" component={WikiRoot} />
          <Route path="/:repository/:branch/pages" component={Directory} />
          <Route path="/:repository/:branch/history" component={History} />
          <Route path="/:repository/:branch/search" component={Search} />
          <Route path="/:repository/:branch" component={Page} />
        </Switch>
      </div>
    );
  }
}

export default withRouter(injectSheet(styles)(Main));
