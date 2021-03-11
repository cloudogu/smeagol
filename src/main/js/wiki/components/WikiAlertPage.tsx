import React from "react";
import I18nAlert from "../../I18nAlert";

export default class WikiAlertPage extends React.Component<{ i18nKey: string }> {
  render(): JSX.Element {
    return (
      <div>
        <h1>Smeagol</h1>
        <I18nAlert i18nKey={this.props.i18nKey} />
      </div>
    );
  }
}
