//@flow
import React from 'react';
import {Redirect} from 'react-router-dom';
import Loading from '../../Loading';
import I18nAlert from '../../I18nAlert';
import WikiNotFoundError from "../components/WikiNotFoundError";
import BackToRepositoriesButton from "../../BackToRepositoriesButton";
import {withRouter} from "react-router";
import {pathWithTrailingSlash} from "../../pathUtil";
import {useWiki} from '../modules/wiki';
import {PAGE_NOT_FOUND_ERROR} from "../../apiclient";

type Props = {
    match: any
};

function WikiRoot(props: Props) {

    const {isLoading, isError, data, error} = useWiki(props.match.params.repository, props.match.params.branch)

    let child = <div/>;
    if (error === PAGE_NOT_FOUND_ERROR) {
        child = <WikiNotFoundError/>
    } else if (isError) {
        child = <I18nAlert i18nKey="wikiroot_failed_to_fetch"/>;
    } else if (isLoading) {
        child = <Loading/>;
    } else if (data) {
        child = <Redirect to={pathWithTrailingSlash(props.match.url) + data.landingPage}/>
    }

    return (
        <div>
            <h1>Smeagol</h1>
            {child}
            <BackToRepositoriesButton/>
        </div>
    );
}

export default withRouter(WikiRoot)