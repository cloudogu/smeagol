//@flow
import React from 'react';
import { fetchRepository } from '../modules/repository';
import BranchOverview from '../components/BranchOverview';
import { connect } from 'react-redux';
import GeneralInformation from '../components/GeneralInformation';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';

type Props = {
    repository: any,
    error: any,
    loading: boolean
}

class Branches extends React.Component<Props> {

    componentDidMount() {
        this.props.fetchRepository(this.props.match.params.repository);
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

const mapStateToProps = (state) => {
    return state.repository
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchRepository: (repositories) => {
            dispatch(fetchRepository(repositories))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Branches);