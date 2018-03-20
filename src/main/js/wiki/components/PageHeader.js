//@flow
import React from 'react';
import injectSheet from 'react-jss';
import ActionLink from './ActionLink';
import ActionButton from './ActionButton';
import PageNameForm from './PageNameForm';
import { withRouter } from 'react-router-dom';
import classNames from 'classnames';
import SearchBar from "./SearchBar";

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
    pagesLink: string,
    historyLink: string,
    onDeleteClick: () => void,
    onHomeClick: () => void,
    onOkMoveClick: () => void,
    onRestoreClick: () => void,
    search: (string) => void,
    history: any,
    classes: any
}

type State = {
    showCreateForm: boolean,
    showMoveForm: boolean
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

    onOkMoveClick = (name) => {
        const path = this.getPathFromPagename(name);
        this.props.onOkMoveClick(path);
    };

    onOkCreate = (name) => {
        const { repository, branch } = this.props.wiki;
        let wikiPath = `/${repository}/${branch}/`;
        const pagePath = this.getPathFromPagename(name);

        this.props.history.push(wikiPath + pagePath);
    };

    getPathFromPagename = (name) => {
        if (name.startsWith('/')) {
            return name.substr(1);
        } else {
            return `docs/${name}`;
        }
    };

    onMoveClick = () => {
        this.setState({
            showMoveForm: true
        });
    };

    onAbortMoveClick = () => {
        this.setState({
            showMoveForm: false
        });
    };

    onRestoreClick = () => {
        const pagePath = this.props.page.path;
        const commit = this.props.page.commit.commitId;
        this.props.onRestoreClick(pagePath, commit);
    };


    render() {
        const { page, pagesLink, classes, onDeleteClick, onHomeClick, historyLink } = this.props;

        const homeButton = <ActionButton onClick={onHomeClick}  i18nKey="page-header_home" type="primary" />;
        const createButton = <ActionButton onClick={this.onCreateClick} i18nKey="page-header_create" type="primary" />;
        const pagesButton = <ActionLink to={ pagesLink }  i18nKey="page-header_pages" type="primary" />;
        const historyButton = <ActionLink to={ historyLink }  i18nKey="page-header_history" type="primary" />;
        const edit = page._links.edit ? <ActionLink to="?edit=true" i18nKey="page-header_edit" type="primary" /> : '';
        const moveButton = page._links.move ? <ActionButton onClick={this.onMoveClick} i18nKey="page-header_move" type="primary" /> : '';
        const deleteButton = page._links.delete ? <ActionButton onClick={onDeleteClick} i18nKey="page-header_delete" type="primary" /> : '';
        const restoreButton = page._links.restore ? <ActionButton onClick={this.onRestoreClick} i18nKey="page-header_restore" type="primary" /> : '';
        const createForm = <PageNameForm show={ this.state.showCreateForm } onOk={ this.onOkCreate } onAbortClick={ this.onAbortCreateClick } labelPrefix="create" />
        const moveForm = <PageNameForm show={ this.state.showMoveForm } onOk={ this.onOkMoveClick } onAbortClick={ this.onAbortMoveClick } labelPrefix="move" />

        return (
            <div className={classes.header}>
                <h1>{ page.path }</h1>
                <div className={classNames(classes.actions, "row")}>
                    <div className="col-xs-9">
                        {homeButton}
                        {createButton}
                        {moveButton}
                        {pagesButton}
                        {historyButton}
                        {edit}
                        {deleteButton}
                        {restoreButton}
                    </div>
                    <div className="col-xs-3">
                        <SearchBar search={search}/>
                    </div>
                </div>
                {createForm}
                {moveForm}
            </div>
        );
    }

}

export default withRouter(injectSheet(styles)(PageHeader));
