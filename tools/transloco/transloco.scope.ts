export const LANGUAGES = ['en', 'es'];

export const scopeLoader = (importer: (lang: string, root: string) => Promise<any>, root: any = 'i18n') => {
  return LANGUAGES.reduce((acc, lang) => {
    // @ts-ignore
    acc[lang] = () => importer(lang, root);
    return acc;
  }, {});
};
