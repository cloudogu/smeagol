//@flow
import React from 'react';
import injectSheet from 'react-jss';
import ActionLink from './ActionLink';
import ActionButton from './ActionButton';
import CreateForm from './CreateForm';
import { withRouter } from 'react-router-dom';

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
    wiki: any,
    onDeleteClick: () => void,
    onHomeClick: () => void,
    history: any,
    classes: any
}

type State = {
    showCreateForm: boolean
};

class PageHeader extends React.Component<Props,State> {

    constructor(props) {
        super(props);
        this.state =  {
            showCreateForm: false
        };
    }

    onCreateClick = () =>  {
        this.setState({
            showCreateForm: true
        });
    };

    onAbortCreateClick = () => {
        this.setState({
            showCreateForm: false
        });
    };

    onOkCreate = (name) => {
        const { repository, branch } = this.props.wiki;

        let path = `/${repository}/${branch}`;

        if (name.startsWith('/')) {
            path = `${path}${name}`;
        } else {
            path = `${path}/docs/${name}`;
        }

        this.props.history.push(path);
    };

    render() {
        const { page, classes, onDeleteClick, onHomeClick } = this.props;

        const homeButton = <ActionButton onClick={onHomeClick}  i18nKey="page-header_home" type="primary" />;
        const createButton = <ActionButton onClick={this.onCreateClick}  i18nKey="page-header_create" type="primary" />;
        const edit = page._links.edit ? <ActionLink to="?edit=true" i18nKey="page-header_edit" type="primary" /> : '';
        const deleteButton = page._links.delete ? <ActionButton onClick={onDeleteClick}  i18nKey="page-header_delete" type="primary" /> : '';

        const createForm = <CreateForm show={ this.state.showCreateForm } onOk={ this.onOkCreate } onAbortClick={ this.onAbortCreateClick } />
        return (
            <div className={classes.header}>
                <h1>{ page.path }</h1>
                <div className={classes.actions}>
                    {homeButton}
                    {createButton}
                    {edit}
                    {deleteButton}
                </div>
                {createForm}
            </div>
        );
    }

}

export default withRouter(injectSheet(styles)(PageHeader));
