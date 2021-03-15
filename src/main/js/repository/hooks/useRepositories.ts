import { useQuery, UseQueryResult } from "react-query";
import { apiClient } from "../../apiclient";

export function useRepositories(onlyWithWiki: boolean): UseQueryResult {
  let queryParam = "";
  if (onlyWithWiki) {
    queryParam = "?wikiEnabled=true";
  }

  return useQuery("repositories" + queryParam, () =>
    apiClient
      .get("/repositories" + queryParam)
      .then((response) => response.json())
      .then((resource) => {
        if (resource._embedded && resource._embedded.repositories) {
          return resource._embedded.repositories;
        } else {
          return [];
        }
      })
  );
}
