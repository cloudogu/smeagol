import { useMutation, UseMutationResult, useQuery, useQueryClient, UseQueryResult } from "react-query";
import { apiClient } from "../../apiclient";
import { WikiDto } from "../types/wikiDto";

export function useWiki(repository: string, branch: string): UseQueryResult<WikiDto> {
  return useQuery<WikiDto>(["wiki", { repository: repository, branch: branch }], () =>
    apiClient.get(`/repositories/${repository}/branches/${branch}`).then((response) => response.json())
  );
}

type editParams = {
  rootDir: string;
  landingPage: string;
};

export function useEditWiki(repository: string, branch: string): UseMutationResult {
  const queryClient = useQueryClient();
  const url = `/repositories/${repository}/branches/${branch}`;
  return useMutation(
    ["editWiki", url],
    (data: editParams) => {
      return apiClient.patch(url, data);
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(["wiki", { repository: repository, branch: branch }]);
      }
    }
  );
}

export function useInitWiki(repository: string, branch: string): UseMutationResult {
  const queryClient = useQueryClient();
  const url = `/repositories/${repository}/branches/${branch}`;
  const data = {
    landingPage: "Home",
    rootDir: "docs"
  };
  return useMutation(
    ["initWiki", url],
    () => {
      return apiClient.post(url, data);
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(["wiki", { repository: repository, branch: branch }]);
      }
    }
  );
}
