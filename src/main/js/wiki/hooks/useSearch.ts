import { apiClient } from "../../apiclient";
import { useQuery, UseQueryResult } from "react-query";
import { SearchFindings } from "../types/searchFinding";

function createSearchUrl(repositoryId: string, branch: string, query: string) {
  return `/repositories/${repositoryId}/branches/${branch}/search?query=${query}`;
}

export function useSearch(repository: string, branch: string, query: string): UseQueryResult<SearchFindings> {
  const url = createSearchUrl(repository, branch, query);
  return useQuery<SearchFindings>(["search", { repository: repository, branch: branch, query: query }], () =>
    apiClient.get(url).then((response) => response.json())
  );
}
