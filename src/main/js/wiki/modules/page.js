//@flow
import {apiClient} from '../../apiclient';
import {QueryClient, useMutation, useQuery, useQueryClient} from "react-query";

function createPageUrl(repositoryId: string, branch: string, path: string, commit: string) {
    let url = `/repositories/${repositoryId}/branches/${branch}/pages/${path}`
    if (commit) {
        url += `?commit=${commit}`;
    }
    return url;
}

export function usePage(repository: string, branch: string, path: string, commit: string) {
    let url = createPageUrl(repository, branch, path, commit)
    return useQuery(['page', {repository: repository, branch: branch, path: path, commit: commit}], () =>
        apiClient.get(url).then(response => response.json()))
}

export function useEditPage(repository: string, branch: string, path: string) {
    const queryClient = useQueryClient()
    let url = createPageUrl(repository, branch, path, "")
    return useMutation(["editpage", url], (data) => {
        return apiClient.post(url, {message: data.message, content: data.content})
    }, {
        onSuccess: () => {
            invalidateQueriesForPageContentChange(queryClient, repository, branch, path)
        },
    })
}

export function useDeletePage(repository: string, branch: string, path: string, pushPageState: () => void) {
    const queryClient = useQueryClient()
    let url = createPageUrl(repository, branch, path, "")
    return useMutation(["deletepage", url], (message) => {
        return apiClient.delete(url, {message}).then(() => {
            pushPageState()
        })
    }, {
        onSuccess: () => {
            invalidateQueriesForPageMetaChange(queryClient, repository, branch)
            invalidateQueriesForPageContentChange(queryClient, repository, branch, path)
        },
    })
}

export function useRenamePage(repository: string, branch: string, path: string, pushPageState: (string) => void) {
    const queryClient = useQueryClient()
    let url = createPageUrl(repository, branch, path, "")
    return useMutation(["movePage", url], (data) => {
        // TODO i18n
        const message = 'Move page ' + path + ' to ' + data.target + ' (smeagol)';
        return apiClient.post(url, {message, "moveTo": data.target}).then(() => {
                invalidateQueriesForPageContentChange(queryClient, repository, branch, data.target)
                pushPageState(data.target)
            }
        )
    }, {
        onSuccess: () => {
            invalidateQueriesForPageMetaChange(queryClient, repository, branch)
            invalidateQueriesForPageContentChange(queryClient, repository, branch, path)
        },
    })
}

export function useCreatePage(repository: string, branch: string, path: string) {
    const queryClient = useQueryClient()
    let url = createPageUrl(repository, branch, path, "")
    return useMutation(["createPage", url], (data) => {
        return apiClient.post(url, {message: data.message, content: data.content})
    }, {
        onSuccess: () => {
            invalidateQueriesForPageMetaChange(queryClient, repository, branch)
            invalidateQueriesForPageContentChange(queryClient, repository, branch, path)
        },
    })
}

type deleteParams = {
    message: string,
    commit: string
}

export function useRestorePage(repository: string, branch: string, path: string, pushPageState: (string) => void) {
    const queryClient = useQueryClient()
    let url = createPageUrl(repository, branch, path, "")
    return useMutation(["deletepage", url], (params: deleteParams) => {
        return apiClient.post(url, {message: params.message, restore: params.commit}).then(() => {
            pushPageState(path)
        })
    }, {
        onSuccess: () => {
            invalidateQueriesForPageMetaChange(queryClient, repository, branch)
            invalidateQueriesForPageContentChange(queryClient, repository, branch, path)
        },
    })
}

function invalidateQueriesForPageMetaChange(queryClient: QueryClient, repository: string, branch: string) {
    queryClient.invalidateQueries(['directory', {repository: repository, branch: branch}])
}

function invalidateQueriesForPageContentChange(queryClient: QueryClient, repository: string, branch: string, path: string) {
    queryClient.invalidateQueries(['page', {repository: repository, branch: branch, path: path}])
    queryClient.invalidateQueries(['search', {repository: repository, branch: branch}])
    queryClient.invalidateQueries(['pagehistory', {repository: repository, branch: branch, path: path}])
}