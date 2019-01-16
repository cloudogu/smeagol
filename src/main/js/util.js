
export function pathWithTrailingSlash(path) {
    var lastChar = path.substr(-1);
    if (lastChar !== '/') {
        path = path + '/';
    }
    return path;
}