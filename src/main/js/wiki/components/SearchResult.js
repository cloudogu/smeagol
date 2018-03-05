//@flow
import React from 'react';
import {Link} from "react-router-dom";

type Props = {
    result: any,
    createPageLink: (path: string) => string
};

class SearchResult extends React.Component<Props> {

    render() {
        const { result } = this.props;
        const pageLink = this.props.createPageLink(result.path);
        return (
            <li>
                <Link to={pageLink}>{ result.path }</Link>
            </li>
        );
    }

}

export default SearchResult;
