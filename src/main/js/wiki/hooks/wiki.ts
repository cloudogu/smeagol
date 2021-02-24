import { useQuery, UseQueryResult } from "react-query";
import { apiClient } from "../../apiclient";

export function useWiki(repository: string, branch: string): UseQueryResult {
  return useQuery(["wiki", { repository: repository, branch: branch }], () =>
    apiClient.get(`/repositories/${repository}/branches/${branch}.json`).then((response) => response.json())
  );
}
