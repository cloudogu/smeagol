import React, { FC } from "react";
import { Redirect } from "react-router-dom";
import Loading from "../../Loading";
import I18nAlert from "../../I18nAlert";
import WikiNotFoundError from "../components/WikiNotFoundError";
import BackToRepositoriesButton from "../../BackToRepositoriesButton";
import { match, withRouter } from "react-router";
import { pathWithTrailingSlash } from "../../pathUtil";
import { useInitWiki, useWiki } from "../hooks/wiki";
import { PAGE_NOT_FOUND_ERROR } from "../../apiclient";
import ActionButton from "../components/ActionButton";
import ToolTip from "../components/ToolTip";

type Params = {
  repository: string;
  branch: string;
};

type Props = {
  match: match<Params>;
};
const WikiRoot: FC<Props> = (props) => {
  const wikiQuery = useWiki(props.match.params.repository, props.match.params.branch);
  const initWikiMutation = useInitWiki(props.match.params.repository, props.match.params.branch);

  let child = <div />;
  if (initWikiMutation.error) {
    child = <I18nAlert i18nKey="init_wiki_failed" />;
  } else if (wikiQuery.isLoading || initWikiMutation.isLoading) {
    child = <Loading />;
  } else if (wikiQuery.error === PAGE_NOT_FOUND_ERROR) {
    child = (
      <>
        <WikiNotFoundError />
        <ToolTip prefix={"init-wiki"} />
        <ActionButton i18nKey="init_wiki" type="primary" onClick={initWikiMutation.mutate} />
      </>
    );
  } else if (wikiQuery.error) {
    child = <I18nAlert i18nKey="wikiroot_failed_to_fetch" />;
  } else if (wikiQuery.data) {
    child = <Redirect to={pathWithTrailingSlash(props.match.url) + wikiQuery.data.landingPage} />;
  }

  return (
    <div>
      <h1>Smeagol</h1>
      {child}
      <BackToRepositoriesButton />
    </div>
  );
};

export default withRouter(WikiRoot);
