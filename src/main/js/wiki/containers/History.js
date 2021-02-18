//@flow
import React from 'react';
import {connect} from 'react-redux';
import I18nAlert from '../../I18nAlert';
import Loading from '../../Loading';
import {translate} from 'react-i18next';
import {createHistoryUrl, fetchHistoryIfNeeded} from "../modules/pagehistory";
import CommitsTable from '../components/CommitsTable';
import ActionLink from '../components/ActionLink';

type Props = {
    loading: boolean,
    error: Error,
    directory: any,
    repository: string,
    branch: string,
    page: string,
    url: string,
    t: any,
    pagehistory: any,
    fetchHistoryIfNeeded: (url: string) => void
}

class History extends React.Component<Props> {

    componentDidMount() {
        const {url, repository, branch, fetchHistoryIfNeeded} = this.props;

        fetchHistoryIfNeeded(url);
    }

    componentDidUpdate() {
        this.props.fetchHistoryIfNeeded(this.props.url);
    }

    render() {
        const {error, loading, t, page, pagehistory, repository, branch} = this.props;
        const pagePath = `/${repository}/${branch}/${page}`;
        if (error) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <I18nAlert i18nKey="directory_failed_to_fetch"/>
                </div>
            );
        } else if (loading) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <Loading/>
                </div>
            );
        } else if (!pagehistory) {
            return (
                <div>
                    <h1>Smeagol</h1>
                </div>
            );
        }

        return (
            <div>
                <div className="page-header">
                    <h1>{t('history_heading') + page}</h1>
                    <ActionLink to={pagePath} i18nKey="history-header_show_page" type="primary"/>
                </div>
                <CommitsTable commits={pagehistory.commits} pagePath={pagePath}/>
            </div>
        );
    }

}

function findDirectoryPath(props) {
    const {pathname} = props.location;
    const parts = pathname.split('/');
    return parts.slice(4).join('/');
}

const mapStateToProps = (state, ownProps) => {
    const {repository, branch} = ownProps.match.params;
    const page = findDirectoryPath(ownProps);
    const url = createHistoryUrl(repository, branch, page);
    return {
        ...state.pagehistory[url],
        repository,
        branch,
        url,
        page
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchHistoryIfNeeded: (url: string) => {
            dispatch(fetchHistoryIfNeeded(url))
        },
    }
};

export default translate()(connect(mapStateToProps, mapDispatchToProps)(History));
