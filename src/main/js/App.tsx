import React from "react";
import Navigation from "./Navigation";
import Main from "./Main";
import "ces-theme/dist/css/ces.css";
import { withRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "react-query";
import { ReactQueryDevtools } from "react-query/devtools";

const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <div className="App">
        <Navigation />
        <Main />
      </div>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
}

export default withRouter(App);