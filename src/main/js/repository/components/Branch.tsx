import React from "react";
import { Link, withRouter, RouteComponentProps } from "react-router-dom";
import { pathWithTrailingSlash } from "../../pathUtil";

type BranchData = {
  name: string;
};

type Props = RouteComponentProps & {
  branch: BranchData;
};

class Branch extends React.Component<Props> {
  render() {
    const { match, branch } = this.props;
    const link = pathWithTrailingSlash(match.url) + encodeURIComponent(branch.name) + "/";

    return (
      <Link className="list-group-item" to={link}>
        <h4>{branch.name}</h4>
      </Link>
    );
  }
}

export default withRouter(Branch);
