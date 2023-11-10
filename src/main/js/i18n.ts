import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import { reactI18nextModule } from "react-i18next";

import "dayjs/locale/de.js";

// The import is essential for webpack to detect the translations and to pack them together with the bundle.js
import resBundle from "i18next-resource-store-loader!./assets/locales/DoNotDelete";

i18n
  .use(LanguageDetector)
  .use(reactI18nextModule)
  .init({
    fallbackLng: "en",

    // have a common namespace used around the full app
    ns: ["translations"],
    defaultNS: "translations",
    resources: resBundle,

    debug: true,

    react: {
      wait: true
    }
  });

export default i18n;
