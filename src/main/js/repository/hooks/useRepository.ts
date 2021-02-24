import { useQuery, UseQueryResult } from "react-query";
import { apiClient } from "../../apiclient";

export function useRepository(id: string): UseQueryResult {
  return useQuery(["repository", id], () => apiClient.get(`/repositories/${id}`).then((response) => response.json()));
}
