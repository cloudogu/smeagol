export type Branch = {
  name: string;
  _links: {
    self: {
      href: string;
    };
  };
};

export type RepositoryDto = {
  description: string;
  id: string;
  name: string;
  _embedded: {
    branches: {
      _embedded: {
        branchResourceList: Branch[]
      }
    };
  };
  _links: {
    self: {
      href: string;
    };
  };
};
