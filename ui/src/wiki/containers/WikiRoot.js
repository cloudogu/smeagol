//@flow
import React from 'react';
import {connect} from 'react-redux';
import {fetchWiki} from '../modules/wiki';
import {Redirect} from 'react-router-dom';

type Props = {}

class WikiRoot extends React.Component<Props> {

    componentDidMount() {
        const { repository, branch } = this.props.match.params;

        console.log(this.props.match);

        this.props.fetchWiki( repository, branch );
    }

    render() {
        const { wiki } = this.props;
        if (!wiki) {
            return <div />
        }

        return (
            <Redirect to={wiki.landingPage} />
        );
    }

}

const mapStateToProps = (state) => ({
    wiki: state.wiki ? state.wiki.wiki : null
});

const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        fetchWiki: (repository, branch) => {
            dispatch(fetchWiki(repository, branch))
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(WikiRoot);