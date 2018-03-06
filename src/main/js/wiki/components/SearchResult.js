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
            <Link to={pageLink} className="list-group-item">
                <span className="badge">{ result.score.toFixed(2) }</span>
                <h4 className="list-group-item-heading">{ result.path }</h4>
                <p className="list-group-item-text" dangerouslySetInnerHTML={{__html: result.contentFragment}}></p>
            </Link>
        );
    }

}

export default SearchResult;
