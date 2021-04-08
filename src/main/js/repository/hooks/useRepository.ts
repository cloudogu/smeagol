import { useQuery, UseQueryResult } from "react-query";
import { apiClient } from "../../apiclient";
import { RepositoryDto } from "../types/repositoryDto";

export function useRepository(id: string): UseQueryResult<RepositoryDto> {
  return useQuery<RepositoryDto>(["repository", id], () =>
    apiClient.get(`/repositories/${id}`).then((response) => response.json())
  );
}
