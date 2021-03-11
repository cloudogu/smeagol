export type WikiDto = {
  displayName: string;
  repositoryName: string;
  directory: string;
  landingPage: string;
  _links: {
    self: {
      href: string;
    };
    repository: {
      href: string;
    };
    landingPage: {
      href: string;
    };
  };
};
