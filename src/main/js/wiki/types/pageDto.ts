export type Author = {
  displayName: string;
  email: string;
}

export type Commit = {
  author: Author
  commitId: string;
  date: any;
  message: string;
}

export type PageDto = {
  commit: Commit;
  content: string;
  path: string;
  landingPage: string;
  _links: {
    self: {
      href: string;
    };
    move: {
      href: string;
    };
    edit: {
      href: string;
    };
    delete: {
      href: string;
    };
  };
};
