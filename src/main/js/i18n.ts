import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import { initReactI18next } from "react-i18next";

import enTranslations from "./assets/locales/en/translations.json";
import deTranslations from "./assets/locales/de/translations.json";

import "dayjs/locale/de.js";

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    fallbackLng: "en",
    supportedLngs: ["en", "de"],
    ns: ["translations"],
    defaultNS: "translations",
    resources: {
      en: { translations: enTranslations },
      de: { translations: deTranslations }
    },
    debug: process.env.NODE_ENV === "development",
    interpolation: {
      escapeValue: false
    },
    detection: {
      order: ["querystring", "cookie", "localStorage", "navigator", "path", "subdomain"],
      caches: ["localStorage", "cookie"]
    }
  });

export default i18n;
