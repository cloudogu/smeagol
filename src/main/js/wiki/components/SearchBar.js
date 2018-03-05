//@flow
import React from 'react';
import PageContent from './PageContent';
import PageHeader from './PageHeader';
import PageFooter from './PageFooter';

type Props = {
    search: (string) => void
};

type State = {
    query: string
};

class SearchBar extends React.Component<Props, State> {

    constructor(props) {
        super(props);
        this.state = {};
    }

    search = (event: Event) => {
        event.preventDefault();
        this.props.search(this.state.query);
    };

    handleChange = (event: Event) => {
        this.setState({
            query: event.target.value
        });
    };

    render() {
        // TODO i18n
        return (
            <form className="input-group" method="GET" onSubmit={this.search}>
                <input type="text" className="form-control" placeholder="Search for..." onChange={this.handleChange} />
                <span className="input-group-btn">
                    <button className="btn btn-default" type="button" onClick={this.search}>
                        <i className="glyphicon glyphicon-search"></i>
                    </button>
                </span>
            </form>
        );
    }

}

export default SearchBar;
