import React from "react";
import { translate } from "react-i18next";
import injectSheet from "react-jss";
import classNames from "classnames";

type Props = {
  search: (arg0: string) => void;
};

type State = {
  query: string;
};

const styles = {
  searchButton: {
    "border-color": "#1b7daa",
    height: "34px"
  }
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
    const { t, classes } = this.props;
    return (
      <form className="input-group" method="GET" onSubmit={this.search}>
        <input
          type="text"
          className="form-control"
          placeholder={t("search-bar_placeholder")}
          onChange={this.handleChange}
        />
        <span className="input-group-btn">
          <button
            className={classNames(classes.searchButton, "btn", "btn-default")}
            type="button"
            onClick={this.search}
          >
            <i className="glyphicon glyphicon-search" />
          </button>
        </span>
      </form>
    );
  }
}

export default translate()(injectSheet(styles)(SearchBar));
