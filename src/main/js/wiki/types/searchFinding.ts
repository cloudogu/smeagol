export type SearchFinding = {
  contentFragment: string;
  path: string;
  score: number;
  _links: {
    self: {
      href: string;
    };
  };
};

export type SearchFindings = SearchFinding[];
