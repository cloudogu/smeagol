//@flow
import React from 'react';
import {connect} from 'react-redux';
import {fetchWikiIfNeeded, selectByRepositoryAndBranch} from '../modules/wiki';
import {Redirect} from 'react-router-dom';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';

type Props = {
    loading: boolean,
    error: Error,
    wiki: any,
    repository: string,
    branch: string,
    fetchWikiIfNeeded: (repository: string, branch: string) => void
};

class WikiRoot extends React.Component<Props> {

    componentDidMount() {
        const { repository, branch } = this.props;
        this.props.fetchWikiIfNeeded(repository, branch);
    }

    render() {
        const { error, loading, wiki } = this.props;

        let child = <div />;
        if (error) {
            child = <I18nAlert i18nKey="wikiroot_failed_to_fetch" />;
        } else if (loading) {
            child = <Loading />;
        } else if (wiki) {
            child = <Redirect to={wiki.landingPage} />
        }

        return (
            <div>
                <h1>Smeagol</h1>
                { child }
            </div>
        );
    }

}

const mapStateToProps = (state, ownProps) => {
    const { repository, branch } = ownProps.match.params;
    return {
        ...selectByRepositoryAndBranch(state, repository, branch),
        repository,
        branch
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchWikiIfNeeded: (repository, branch) => {
            dispatch(fetchWikiIfNeeded(repository, branch))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(WikiRoot);