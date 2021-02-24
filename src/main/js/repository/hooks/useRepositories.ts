import { useQuery, UseQueryResult } from "react-query";
import { apiClient } from "../../apiclient";

export function useRepositories(): UseQueryResult {
  return useQuery("repositories", () => apiClient.get("/repositories").then((response) => response.json()));
}
