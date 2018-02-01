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

        const edit = page._links.edit ? <ActionLink to="?edit=true"  i18nKey="page-header_edit" type="primary" /> : '';
        return (
            <div className={classes.header}>
                <h1>{ page.path }</h1>
                <div className={classes.actions}>
                    {edit}
                </div>
            </div>
        );
    }

}

export default injectSheet(styles)(PageHeader);
