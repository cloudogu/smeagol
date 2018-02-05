// @flow
import React from 'react';
import { connect } from 'react-redux';
import GeneralInformation from '../components/GeneralInformation';
import RepositoryList from '../components/RepositoryList';

import { fetchRepositoriesIfNeeded } from '../modules/repositories';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';

type Props = {
    loading: boolean,
    error: any,
    repositories: any,
    fetchRepositoriesIfNeeded: () => void
}

class Repositories extends React.Component<Props> {

    componentDidMount() {
        this.props.fetchRepositoriesIfNeeded();
    }

    render() {
        const { loading, error, repositories } = this.props;

        let child = <div />;
        if (error) {
            child = <I18nAlert i18nKey="repositories_failed_to_fetch" />;
        } else if (loading) {
            child = <Loading />;
        } else if (repositories) {
            child = <RepositoryList repositories={ repositories } />;
        }

        return (
            <div>
                <h1>Smeagol</h1>
                <GeneralInformation />
                <h2>Wikis</h2>
                { child }
            </div>
        );
    }

}

const mapStateToProps = (state) => {
    return state.repositories;
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchRepositoriesIfNeeded: () => {
            dispatch(fetchRepositoriesIfNeeded())
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Repositories);
