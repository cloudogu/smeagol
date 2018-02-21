//@flow
import React from 'react';
import {connect} from 'react-redux';
import FileBrowser from '../components/FileBrowser';
import Breadcrumb from '../components/Breadcrumb';
import {createDirectoryUrl, fetchDirectoryIfNeeded} from '../modules/directory';
import I18nAlert from '../../I18nAlert';
import Loading from '../../Loading';
import {translate} from 'react-i18next';

type Props = {
    loading: boolean,
    error: Error,
    directory: any,
    path: string,
    url: string,
    t: any,
    fetchDirectoryIfNeeded: (url: string) => void
}

class Directory extends React.Component<Props> {

    componentDidMount() {
        this.props.fetchDirectoryIfNeeded(this.props.url);
    }

    componentDidUpdate() {
        this.props.fetchDirectoryIfNeeded(this.props.url);
    }

    render() {
        const { path, error, loading, directory, t } = this.props;

        if (error) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <I18nAlert i18nKey="directory_failed_to_fetch" />
                </div>
            );
        } else if (loading) {
            return (
                <div>
                    <h1>Smeagol</h1>
                    <Loading/>
                </div>
            );
        } else if (!directory) {
            return (
                <div>
                    <h1>Smeagol</h1>
                </div>
            );
        }

        return (
            <div>
                <h1>{ t('directory_heading') }</h1>
                <Breadcrumb path={ path } />
                <FileBrowser directory={ directory } />
            </div>
        );
    }

}

function findDirectoryPath(props) {
    const { pathname } = props.location;
    const parts = pathname.split('/');
    return parts.slice(4).join('/');
}

const mapStateToProps = (state, ownProps) => {
    const { repository, branch } = ownProps.match.params;
    const path = findDirectoryPath(ownProps);
    const url = createDirectoryUrl(repository, branch, path);

    return {
        ...state.directory[url],
        url,
        path
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchDirectoryIfNeeded: (url: string) => {
            dispatch(fetchDirectoryIfNeeded(url))
        }
    }
};

export default translate()(connect(mapStateToProps, mapDispatchToProps)(Directory));
