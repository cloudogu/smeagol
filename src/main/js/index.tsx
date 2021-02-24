import React from "react";
import ReactDOM from "react-dom";
import App from "./App";
import registerServiceWorker from "./registerServiceWorker";

import { I18nextProvider } from "react-i18next";
import i18n from "./i18n";

import createHistory from "history/createBrowserHistory";
import { Router } from "react-router";

// Create a history of your choosing (we're using a browser history in this case)
const history = createHistory({
  basename: process.env.PUBLIC_URL
});

window.appHistory = history;

ReactDOM.render(
  <I18nextProvider i18n={i18n}>
    <Router history={history}>
      <App />
    </Router>
  </I18nextProvider>,
  document.getElementById("root")
);

registerServiceWorker();
