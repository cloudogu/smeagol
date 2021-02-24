// @flow
import { useQuery } from "react-query";
import { apiClient } from "../../apiclient";

function createHistoryUrl(repositoryId: string, branch: string, path: string) {
  return `/repositories/${repositoryId}/branches/${branch}/history/${path}`;
}

export function usePageHistory(repository: string, branch: string, path: string) {
  const url = createHistoryUrl(repository, branch, path);
  return useQuery(["pagehistory", { repository: repository, branch: branch, path: path }], () =>
    apiClient.get(url).then((response) => response.json())
  );
}
