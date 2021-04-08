import { apiClient } from "../../apiclient";
import { QueryClient, useMutation, UseMutationResult, useQuery, useQueryClient, UseQueryResult } from "react-query";
import { PageDto } from "../types/pageDto";

function createPageUrl(repositoryId: string, branch: string, path: string, commit: string) {
  let url = `/repositories/${repositoryId}/branches/${branch}/pages/${path}`;
  if (commit) {
    url += `?commit=${commit}`;
  }
  return url;
}

export function usePage(repository: string, branch: string, path: string, commit: string): UseQueryResult<PageDto> {
  const url = createPageUrl(repository, branch, path, commit);
  return useQuery<PageDto>(["page", { repository: repository, branch: branch, path: path, commit: commit }], () =>
    apiClient.get(url).then((response) => response.json())
  );
}

type editParams = {
  message: string;
  content: string;
};

export function useEditPage(repository: string, branch: string, path: string): UseMutationResult {
  const queryClient = useQueryClient();
  const url = createPageUrl(repository, branch, path, "");
  return useMutation(
    ["editpage", url],
    (data: editParams) => {
      return apiClient.post(url, { message: data.message, content: data.content });
    },
    {
      onSuccess: () => {
        invalidateQueriesForPageContentChange(queryClient, repository, branch, path);
      }
    }
  );
}

export function useDeletePage(
  repository: string,
  branch: string,
  path: string,
  pushPageState: () => void
): UseMutationResult {
  const queryClient = useQueryClient();
  const url = createPageUrl(repository, branch, path, "");
  return useMutation(
    ["deletepage", url],
    (message: string) => {
      return apiClient.delete(url, { message }).then(() => {
        pushPageState();
      });
    },
    {
      onSuccess: () => {
        invalidateQueriesForPageMetaChange(queryClient, repository, branch);
        invalidateQueriesForPageContentChange(queryClient, repository, branch, path);
      }
    }
  );
}

export function useRenamePage(
  repository: string,
  branch: string,
  path: string,
  pushPageState: (arg0: string) => void
): UseMutationResult {
  const queryClient = useQueryClient();
  const url = createPageUrl(repository, branch, path, "");
  return useMutation(
    ["movePage", url],
    (target: string) => {
      // TODO i18n
      const message = "Move page " + path + " to " + target + " (smeagol)";
      return apiClient.post(url, { message, moveTo: target }).then(() => {
        invalidateQueriesForPageContentChange(queryClient, repository, branch, target);
        pushPageState(target);
      });
    },
    {
      onSuccess: () => {
        invalidateQueriesForPageMetaChange(queryClient, repository, branch);
        invalidateQueriesForPageContentChange(queryClient, repository, branch, path);
      }
    }
  );
}

type createParams = {
  message: string;
  content: string;
};

export function useCreatePage(repository: string, branch: string, path: string): UseMutationResult {
  const queryClient = useQueryClient();
  const url = createPageUrl(repository, branch, path, "");
  return useMutation(
    ["createPage", url],
    (data: createParams) => {
      return apiClient.post(url, { message: data.message, content: data.content });
    },
    {
      onSuccess: () => {
        invalidateQueriesForPageMetaChange(queryClient, repository, branch);
        invalidateQueriesForPageContentChange(queryClient, repository, branch, path);
      }
    }
  );
}

type deleteParams = {
  message: string;
  commit: string;
};

export function useRestorePage(
  repository: string,
  branch: string,
  path: string,
  pushPageState: (arg0: string) => void
): UseMutationResult {
  const queryClient = useQueryClient();
  const url = createPageUrl(repository, branch, path, "");
  return useMutation(
    ["deletepage", url],
    (params: deleteParams) => {
      return apiClient.post(url, { message: params.message, restore: params.commit }).then(() => {
        pushPageState(path);
      });
    },
    {
      onSuccess: () => {
        invalidateQueriesForPageMetaChange(queryClient, repository, branch);
        invalidateQueriesForPageContentChange(queryClient, repository, branch, path);
      }
    }
  );
}

function invalidateQueriesForPageMetaChange(queryClient: QueryClient, repository: string, branch: string) {
  queryClient.invalidateQueries(["directory", { repository: repository, branch: branch }]);
}

function invalidateQueriesForPageContentChange(
  queryClient: QueryClient,
  repository: string,
  branch: string,
  path: string
) {
  queryClient.invalidateQueries(["page", { repository: repository, branch: branch, path: path }]);
  queryClient.invalidateQueries(["search", { repository: repository, branch: branch }]);
  queryClient.invalidateQueries(["pagehistory", { repository: repository, branch: branch, path: path }]);
}
