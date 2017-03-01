package fr.pigeo.rimap.rimaprcp.core;

/**
 * used for translation purposes. See https://techblog.ralph-schuster.eu/2012/12/30/eclipse-e4-using-the-translationservice/
 * Used in Plugin class, that refers to proper plugin for translations
 */

import java.text.MessageFormat;
import java.util.Locale;

import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.internal.services.BundleTranslationProvider;
import org.eclipse.e4.core.services.translation.TranslationProviderFactory;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;

@SuppressWarnings("restriction")
@Singleton
@Creatable
public class E4Utils {

	public static final TranslationService TRANSLATIONS = getTranslationService();
	
	/**
	 * Finds the top Eclipse context.
	 * 
	 * @return the eclipse context.
	 */
	@SuppressWarnings("restriction")
	public static IEclipseContext getTopContext() {
		return E4Workbench.getServiceContext();

	}

	@SuppressWarnings("restriction")
	public static TranslationService getTranslationService() {
		IEclipseContext context = getTopContext();
		TranslationService service = context.get(TranslationService.class);
		if (service == null) {

			if (context.get(BundleTranslationProvider.LOCALE) == null) {
				context.set(BundleTranslationProvider.LOCALE, Locale.getDefault());
			}

			service = TranslationProviderFactory.bundleTranslationService(context);
			context.set(TranslationService.class, service);
		}

		return service;
	}

	/**
	 * Translate the given key.
	 * 
	 * @param key
	 *            key to translate
	 * @param contributorUri
	 *            the contributor URI of the translation
	 * @param args
	 *            variable replacements (will replace {0}, {1},... placeholders)
	 */
	public static String translate(String key, String contributorUri, Object... args) {
		if (key == null)
			return "";
		if (key.charAt(0) != '%') key = '%'+key;
		try {
			TranslationService ts = TRANSLATIONS;
			String rc = TRANSLATIONS.translate(key, contributorUri);
			if ((args == null) || (args.length == 0))
				return rc;
			return MessageFormat.format(rc, args);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "!" + key + "!";
		}
	}
}