//@flow
import React from 'react';
import {connect} from 'react-redux';
import {fetchWiki} from '../modules/wiki';
import {Redirect} from 'react-router-dom';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';

type Props = {
    loading: boolean,
    error: any,
    wiki: any
};

class WikiRoot extends React.Component<Props> {

    componentDidMount() {
        const { repository, branch } = this.props.match.params;
        this.props.fetchWiki( repository, branch );
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

const mapStateToProps = (state) => {
    return state.wiki;
};

const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        fetchWiki: (repository, branch) => {
            dispatch(fetchWiki(repository, branch))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(WikiRoot);