//@flow
import React from 'react';
import {Link} from 'react-router-dom';

type Props = {
    to: string,
    value: string
};

class ActionLink extends React.Component<Props> {

    render() {
        const { to, value } = this.props;
        return (
            <Link className="btn btn-primary" to={ to }>
                { value }
            </Link>
        );
    }

}

export default ActionLink;
