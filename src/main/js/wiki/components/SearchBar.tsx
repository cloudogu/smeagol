import React from "react";
import { translate } from "react-i18next";

type Props = {
  search: (arg0: string) => void;
};

type State = {
  query: string;
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
    const { t } = this.props;
    return (
      <form className="input-group" method="GET" onSubmit={this.search}>
        <input
          type="text"
          className="form-control"
          placeholder={t("search-bar_placeholder")}
          onChange={this.handleChange}
        />
        <span className="input-group-btn">
          <button className="btn btn-default" type="button" onClick={this.search}>
            <i className="glyphicon glyphicon-search" />
          </button>
        </span>
      </form>
    );
  }
}

export default translate()(SearchBar);
