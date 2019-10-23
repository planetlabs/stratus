/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import lombok.Data;
import org.geowebcache.filter.parameters.CaseNormalizer;
import org.geowebcache.filter.parameters.CaseNormalizingParameterFilter;

import java.util.Locale;

@Data
public abstract class CaseNormalizingParameterFilterRedis  extends CaseNormalizingParameterFilter {
    private CaseNormalizer.Case kase = null;
    private String locale_language = null;
    private String locale_country = null;
    private String locale_variant = null;

    public Locale getLocale() {
        return new Locale(
                locale_language == null ? "" : locale_language,
                locale_country == null ? "" : locale_country,
                locale_variant == null ? "" : locale_variant);
    }

    public void setLocale(Locale locale) {
        this.locale_language = locale.getLanguage();
        this.locale_country = locale.getCountry();
        this.locale_variant = locale.getVariant();
    }

    @Override
    public CaseNormalizer getNormalize() {
        return new CaseNormalizer(kase, getLocale());
    }

    @Override
    public void setNormalize(CaseNormalizer normalize) {
        super.setNormalize(normalize);
        this.kase = normalize.getCase();
        setLocale(normalize.getLocale());
    }
}
