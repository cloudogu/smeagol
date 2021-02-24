// @flow

const isPageNameValid = (initialPageName: string, pageName: string) => {
  if (pageName === initialPageName) {
    return false;
  }

  const decoded = decodeURI(pageName);

  if (decoded.startsWith("/") || decoded.startsWith(" ")) {
    return false;
  }

  if (decoded.includes("..") || decoded.includes("//") || decoded.includes("?") || decoded.includes("!")) {
    return false;
  }

  if (decoded.endsWith("/") || decoded.endsWith(".") || decoded.endsWith(" ")) {
    return false;
  }

  return decoded.match(/^[0-9a-zA-Z.\-_/ ]+$/);
};

export default isPageNameValid;
