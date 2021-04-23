export function pathWithTrailingSlash(path: string) {
  const lastChar = path.substr(-1);
  if (lastChar !== "/") {
    return path + "/";
  }
  return path;
}

export function isValidPath(path: string): boolean {
  const allowlistCharactersInPath = /^([\w.\-_/ ]+)$/;

  if (path === null || path === undefined || path.length === 0) {
    return false;
  }

  let result = true;

  result = result && !path.includes("..");
  result = result && !path.includes("//");
  result = result && !path.startsWith("/");
  result = result && !path.endsWith(".");
  result = result && !path.endsWith("/");
  result = result && !path.startsWith(" ");
  result = result && !path.endsWith(" ");
  result = result && path.match(allowlistCharactersInPath).length > 0;

  return result;
}
