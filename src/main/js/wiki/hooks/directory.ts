import { apiClient } from "../../apiclient";
import { useQuery, UseQueryResult } from "react-query";

function createDirectoryUrl(repositoryId: string, branch: string, path: string) {
  return `/repositories/${repositoryId}/branches/${branch}/directories/${path}`;
}

export function useDirectory(repository: string, branch: string, path: string): UseQueryResult {
  const url = createDirectoryUrl(repository, branch, path);
  return useQuery(["directory", { repository: repository, branch: branch, path: path }], () =>
    apiClient.get(url).then((response) => response.json())
  );
}
