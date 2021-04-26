export function pathWithTrailingSlash(path: string) {
  const lastChar = path.substr(-1);
  if (lastChar !== "/") {
    return path + "/";
  }
  return path;
}

export function isValidRelativePath(path: string): boolean {
  if (path === null || path === undefined || path.length === 0) {
    return false;
  }
  const absolutePathRegex = /^[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?(\/[a-zA-Z0-9]([a-zA-Z0-9-]*[A-Z0-9])?)*$/g;
  return path.match(absolutePathRegex) !== null;
}
