//@flow
import React from 'react';
import injectSheet from 'react-jss';

const styles = {
    border: {
        borderBottom: '1px solid #ddd'
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
            <div className={classes.border}>
                <h1>{ page.path }</h1>
            </div>
        );
    }

}

export default injectSheet(styles)(PageHeader);
