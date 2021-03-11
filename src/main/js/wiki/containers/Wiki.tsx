import React, { FC } from "react";
import { match, Route, Switch, withRouter } from "react-router";
import Directory from "./Directory";
import History from "./History";
import Search from "./Search";
import Page from "./Page";
import Navigation from "../../Navigation";
import WikiRoot from "./WikiRoot";

type Params = {
  repository: string;
  branch: string;
};

type Props = {
  match: match<Params>;
  history: any;
};

const Wiki: FC<Props> = (props) => {
  return (
    <div>
      <Navigation
        repository={props.match.params.repository}
        branch={props.match.params.branch}
        history={props.history}
      />
      <Switch>
        <Route exact path="/:repository/:branch" component={WikiRoot} />
        <Route path="/:repository/:branch/pages" component={Directory} />
        <Route path="/:repository/:branch/history" component={History} />
        <Route path="/:repository/:branch/search" component={Search} />
        <Route path="/:repository/:branch" component={Page} />
      </Switch>
    </div>
  );
};

export default withRouter(Wiki);
