import React from "react";
import Loading from "../../Loading";

export default class WikiLoadingPage extends React.Component {
  render(): JSX.Element {
    return (
      <div>
        <h1>Smeagol</h1>
        <Loading />
      </div>
    );
  }
}
