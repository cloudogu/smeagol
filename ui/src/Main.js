//@flow
import React from 'react';
import injectSheet from 'react-jss';
import classNames from 'classnames';

import { Route } from 'react-router';

import Repositories from './repository/containers/Repositories';
import Branches from './repository/containers/Branches';

const styles = {
    content: {
        paddingTop: '60px'
    },
};

type Props = {}

class Main extends React.Component<Props> {

    render() {
        const { classes } = this.props;
        return (
            <div className={classNames('container', classes.content)}>
                <Route exact path="/" component={Repositories} />
                <Route exact path="/:repository" component={Branches} />
            </div>
        );
    }

}

export default injectSheet(styles)(Main);
