//@flow
import React from 'react';
import injectSheet from 'react-jss';
import ActionLink from './ActionLink';
import ActionButton from './ActionButton';

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
    deletePage: () => void,
    classes: any
}

class PageHeader extends React.Component<Props> {

    render() {
        const { page, classes, deletePage } = this.props;

        const edit = page._links.edit ? <ActionLink to="?edit=true" i18nKey="page-header_edit" type="primary" /> : '';
        const deleteButton = page._links.delete ? <ActionButton onClick={deletePage}  i18nKey="page-header_delete" type="primary" /> : '';
        return (
            <div className={classes.header}>
                <h1>{ page.path }</h1>
                <div className={classes.actions}>
                    {edit}
                    {deleteButton}
                </div>
            </div>
        );
    }

}

export default injectSheet(styles)(PageHeader);
