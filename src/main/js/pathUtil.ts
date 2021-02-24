export function pathWithTrailingSlash(path: string) {
  const lastChar = path.substr(-1);
  if (lastChar !== "/") {
    return path + "/";
  }
  return path;
}
