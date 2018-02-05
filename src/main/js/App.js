import React, { Component } from 'react';
import Navigation from './Navigation';
import Main from './Main';
import 'ces-theme/dist/css/ces.css';
import {withRouter} from 'react-router-dom';

class App extends Component {
  render() {
    return (
      <div className="App">
        <Navigation />
        <Main />
      </div>
    );
  }
}

export default withRouter(App);
