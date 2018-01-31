//@flow
import React from 'react';
import {Link} from 'react-router-dom';

type Props = {
  branch: any
};

class Branch extends React.Component<Props> {

    render() {
        const { branch } = this.props;

        return (
            <Link className="list-group-item" to={ branch.name + '/' }>
                <h4>{ branch.name }</h4>
            </Link>
        );
    }

}

export default Branch;
