//@flow
import React from 'react';
import injectSheet from 'react-jss';
import ActionLink from './ActionLink';

const styles = {
    header: {
        borderBottom: '1px solid #ddd'
    },
    actions: {
        marginBottom: '1em'
    }
};

type Props = {
    page: any,
    classes: any
}

class PageHeader extends React.Component<Props> {

    render() {
        const { page, classes } = this.props;
        return (
            <div className={classes.header}>
                <h1>{ page.path }</h1>
                <div className={classes.actions}>
                    <ActionLink to="?edit=true"  value={ 'Edit' } />
                </div>
            </div>
        );
    }

}

export default injectSheet(styles)(PageHeader);
