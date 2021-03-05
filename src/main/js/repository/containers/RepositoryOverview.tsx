import React, { FC } from "react";
import { Route, Switch, withRouter } from "react-router";
import Navigation from "../../Navigation";
import Repositories from "./Repositories";
import Branches from "./Branches";

const RepositoryOverview: FC = () => {
  return (
    <div>
      <Navigation />
      <Switch>
        <Route exact path="/" component={Repositories} />
        <Route exact path="/:repository" component={Branches} />
      </Switch>
    </div>
  );
};

export default withRouter(RepositoryOverview);
