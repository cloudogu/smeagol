import { useQuery, UseQueryResult } from "react-query";
import { apiClient } from "../../apiclient";
import { WikiDto } from "../types/wikiDto";

export function useWiki(repository: string, branch: string): UseQueryResult<WikiDto> {
  return useQuery<WikiDto>(["wiki", { repository: repository, branch: branch }], () =>
    apiClient.get(`/repositories/${repository}/branches/${branch}`).then((response) => response.json())
  );
}
