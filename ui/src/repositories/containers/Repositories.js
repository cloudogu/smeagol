// @flow
import React from 'react';
import { connect } from 'react-redux';
import GeneralInformation from '../components/GeneralInformation';
import RepositoryList from '../components/RepositoryList';

import { fetchRepositories } from '../module';

type Props = {
    repositories: Repository[]
}

class Repositories extends React.Component<Props> {

    componentDidMount() {
        this.props.fetchRepositories();
    }

    render() {
        let repositories = this.props.repositories;
        if (!repositories) {
            repositories = [];
        }

        return (
            <div>
                <h1>Smeagol</h1>
                <GeneralInformation />
                <h2>Wikis</h2>
                <RepositoryList repositories={ repositories } />
            </div>
        );
    }

}

const mapStateToProps = (state) => ({
    repositories: state.repositories ? state.repositories.items : []
});

const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        fetchRepositories: () => {
            dispatch(fetchRepositories())
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Repositories);
