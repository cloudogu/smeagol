//@flow
import React from 'react';
import injectSheet from 'react-jss';
import { Link } from 'react-router-dom';

const styles = {};

type Props = {
    repository: any,
    classes: any
}

class Repository extends React.Component<Props> {

    render() {
        const { repository } = this.props;
        let name = repository.name;
        const index = name.lastIndexOf('/');
        if (index > 0) {
            name = name.substring(index + 1);
        }

        return (
            <Link className="list-group-item" to={ repository.id + '/' }>
                <h4>{ name }</h4>
                <p className="list-group-item-text">
                    { repository.description }
                </p>
            </Link>
        );
    }

}

export default injectSheet(styles)(Repository);
