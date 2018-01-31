//@flow
import React from 'react';
import injectSheet from 'react-jss';
import classNames from 'classnames';

import { Route } from 'react-router';

import Repositories from './repository/containers/Repositories';
import Branches from './repository/containers/Branches';
import WikiRoot from './wiki/containers/WikiRoot';
import Page from './wiki/containers/Page';
import {Switch} from 'react-router-dom';

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
                <Switch>
                <Route exact path="/" component={Repositories} />
                <Route exact path="/:repository" component={Branches} />
                <Route exact path="/:repository/:branch" component={WikiRoot} />
                <Route path="/:repository/:branch" component={Page} />
                </Switch>
            </div>
        );
    }

}

export default injectSheet(styles)(Main);
