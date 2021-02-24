import { useQuery } from "react-query";
import { apiClient } from "../../apiclient";

export function useRepository(id: string) {
  return useQuery(["repository", id], () => apiClient.get(`/repositories/${id}`).then((response) => response.json()));
}
