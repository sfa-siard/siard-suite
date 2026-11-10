import "i18next";
import type en from "./en.json";

// typed translation keys: t("unknown.key") does not compile
declare module "i18next" {
  interface CustomTypeOptions {
    defaultNS: "translation";
    resources: { translation: typeof en };
  }
}
