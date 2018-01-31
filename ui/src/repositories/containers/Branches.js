//@flow
import React from 'react';
import { fetchRepository } from '../modules/repository';
import BranchOverview from '../components/BranchOverview';
import { connect } from 'react-redux';
import GeneralInformation from '../components/GeneralInformation';

type Props = {}

class Branches extends React.Component<Props> {

    componentDidMount() {
        this.props.fetchRepository(this.props.match.params.repository);
    }
    render() {
        const { repository } = this.props;

        return (
            <div>
                <h1>Smeagol</h1>
                <GeneralInformation />
                <h2>Branches</h2>
                <BranchOverview repository={repository} />
            </div>
        );
    }

}

const mapStateToProps = (state) => ({
    repository: state.repository ? state.repository.repository : null
});

const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        fetchRepository: (repositories) => {
            dispatch(fetchRepository(repositories))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Branches);