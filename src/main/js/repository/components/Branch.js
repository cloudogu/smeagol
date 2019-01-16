//@flow
import React from 'react';
import {Link} from 'react-router-dom';
import { withRouter } from "react-router";
import {pathWithTrailingSlash} from "../../util";

type Props = {
  branch: any
};

class Branch extends React.Component<Props> {

    render() {
        const { match, branch } = this.props;

        const link = pathWithTrailingSlash(match.url) + encodeURIComponent(branch.name) + '/';

        return (
            <Link className="list-group-item" to={ link }>
                <h4>{ branch.name }</h4>
            </Link>
        );
    }

}

export default withRouter(Branch);
