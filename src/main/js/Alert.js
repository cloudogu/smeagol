//@flow
import React from 'react';

type Props = {}

class Alert extends React.Component<Props> {

    render() {
        return (
            <div className="alert alert-danger">
                { this.props.children }
            </div>
        );
    }

}

export default Alert;
