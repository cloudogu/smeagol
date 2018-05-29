//@flow
import React from 'react';
import { fetchRepositoryByIdIfNeeded, selectById } from '../modules/repository';
import BranchOverview from '../components/BranchOverview';
import { connect } from 'react-redux';
import GeneralInformation from '../components/GeneralInformation';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';

type Props = {
    id: string,
    repository: any,
    error: any,
    loading: boolean,
    fetchRepositoryByIdIfNeeded: (id: string) => void
}

class Branches extends React.Component<Props> {
    componentDidMount() {
        this.props.fetchRepositoryByIdIfNeeded(this.props.id);
    }

    render() {
        const { repository, error, loading } = this.props;

        let child = <div />;
        if (error) {
            child = <I18nAlert i18nKey="branches_failed_to_fetch" />;
        } else if (loading) {
            child = <Loading />;
        } else if (repository) {
            child = <BranchOverview repository={repository} />
        }

        return (
            <div>
                <h1>Smeagol</h1>
                <GeneralInformation />
                <h2>Branches</h2>
                { child }
            </div>
        );
    }

}

const mapStateToProps = (state, ownProps) => {
    const id = ownProps.match.params.repository;
    return {
        ...selectById(state, id),
        id
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchRepositoryByIdIfNeeded: (id: string) => {
            dispatch(fetchRepositoryByIdIfNeeded(id))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Branches);