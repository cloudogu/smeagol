const isPageNameValid = (initialPageName: string, pageName: string) => {
  if (pageName === initialPageName) {
    return false;
  }
  if (pageName.includes("%")) {
    return false;
  }
  const decoded = decodeURI(pageName);
  if (decoded.includes(" ")) {
    return false;
  }
  if (decoded.startsWith("/") || decoded.includes("//") || decoded.endsWith("/")) {
    return false;
  }
  if (decoded.includes("./") || decoded.includes("..") || decoded.endsWith(".")) {
    return false;
  }
  // Valid symbols are: numbers, letters, and - _ . (for file extensions) and / (for subdirectories)
  return decoded.match(/^[0-9a-zA-Z.\-_/ ]+$/) != null;
};

export default isPageNameValid;
