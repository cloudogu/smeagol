//@flow
import React from 'react';
import injectSheet from 'react-jss';

const styles = {
    footer: {
        borderTop: '1px solid #ddd',
        paddingTop: '10px',
        paddingBottom: '10px'
    }
};


type Props = {
    page: any,
    classes: any
}

class PageFooter extends React.Component<Props> {

    render() {
        const { page, classes } = this.props;
        return (
            <div className={classes.footer}>
                Last edited by { page.author.displayName }, { page.lastModified }
            </div>
        );
    }

}

export default injectSheet(styles)(PageFooter);
