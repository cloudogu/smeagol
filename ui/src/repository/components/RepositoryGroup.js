//@flow
import React from 'react';
import injectSheet from 'react-jss';
import Repository from './Repository';
import classNames from 'classnames';

const styles = {
    border: {
        borderBottom: '1px solid #00426B',
        cursor: 'pointer',
        marginBottom: '20px',
        marginTop: '30px'
    },
    icon: {
        fontSize: '14px'
    },
    panel: {
        marginLeft: '15px'
    }
};

type Props = {
    classes: any;
    group: any
}

type State = {
    open: boolean
};

// @VisibleForTesting
export function orderRepositoriesByName(repositories) {
    repositories.sort(function(a, b){
        if (a.name < b.name) {
            return -1;
        } else if (a.name > b.name) {
            return 1;
        }
        return 0;
    });
}

class RepositoryGroup extends React.Component<Props, State> {

    constructor(props) {
        super(props);
        this.state = {
            open: true
        };
    }

    toggleOpenState = () => {
        this.setState({
            open: !this.state.open
        });
    };

    render() {
        const { group, classes } = this.props;
        let repositories = group.repositories;
        orderRepositoriesByName(repositories);

        const icon = this.state.open ? 'glyphicon-chevron-down' : 'glyphicon-chevron-right';

        let children;
        if (this.state.open) {
            children = (
                <div className={ classes.panel }>
                    { repositories.map((repository) => {
                        return (
                            <Repository key={repository.id} repository={repository} />
                        );
                    }) }
                </div>
            );
        }
        return (
            <div>
                <h3 className={classNames(classes.border, classes.open, classes.accordion)} onClick={ this.toggleOpenState }>
                    <i className={classNames(classes.icon, "glyphicon", icon)}></i> { group.name }
                </h3>
                {children}
            </div>
        );
    }

}

export default injectSheet(styles)(RepositoryGroup);
