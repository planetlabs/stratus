/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.geowebcache.filter.parameters.CaseNormalizer;
import org.geowebcache.filter.parameters.ParameterException;
import org.springframework.data.annotation.Transient;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of {@link org.geowebcache.filter.parameters.RegexParameterFilter} that adds @Transient annotations
 * to ensure spring data honors fields marked with the transient keyword.
 *
 * @author joshfix
 * Created on 5/18/18
 */
@ParametersAreNonnullByDefault
@XStreamAlias("regexParameterFilter")
public class RegexParameterFilterRedis extends CaseNormalizingParameterFilterRedis {

    private static final long serialVersionUID = -1496940509350980799L;

    public final static String DEFAULT_EXPRESSION = "";

    private String regex = DEFAULT_EXPRESSION;

    @Transient
    private transient Pattern pat = compile(regex, getNormalize().getCase());

    public RegexParameterFilterRedis() {
        super();
    }

    /**
     * Get a {@link Matcher} for this filter's regexp against the given string.
     * @param value
     * @return
     */
    public synchronized Matcher getMatcher(String value) {
        return pat.matcher(value);
    }

    static Pattern compile(String regex, CaseNormalizer.Case c) {
        int flags = 0;
        if (c!= CaseNormalizer.Case.NONE) {
            flags += Pattern.CASE_INSENSITIVE;
            flags += Pattern.UNICODE_CASE;
        }
        return Pattern.compile(regex, flags);
    }

    protected RegexParameterFilterRedis readResolve() {
        super.readResolve();
        Preconditions.checkNotNull(regex);
        this.pat = Pattern.compile(regex);
        return this;
    }

    @Override
    public String apply(String str) throws ParameterException {
        if (str == null || str.length() == 0) {
            return getDefaultValue();
        }

        if (getMatcher(str).matches()) {
            return getNormalize().apply(str);
        }

        throw new ParameterException(str + " violates filter for parameter " + getKey());
    }

    @Override
    public @Nullable
    List<String> getLegalValues() {
        return null;
    }

    /**
     * Checks whether a given parameter value applies to this filter.
     *
     * @param parameterValue
     *            the value to check if applies to this parameter filter
     * @return {@code true} if {@code parameterValue} is valid according to this filter,
     *         {@code false} otherwise
     */
    @Override
    public boolean applies(@Nullable String parameterValue) {
        return getMatcher(parameterValue).matches();
    }

    /**
     * @return the regex
     */
    public String getRegex() {
        return regex;
    }

    /**
     * @param regex
     *            the regex to set.  {@literal null} will be treated as default value.
     */
    public void setRegex(@Nullable String regex) {
        if(regex==null) regex = DEFAULT_EXPRESSION;
        this.regex = regex;
        this.pat = compile(this.regex, getNormalize().getCase());
    }

    @Override
    public void setNormalize(CaseNormalizer normalize) {
        super.setNormalize(normalize);
        this.pat = compile(this.regex, getNormalize().getCase());
    }

    @Override
    public RegexParameterFilterRedis clone() {
        RegexParameterFilterRedis clone = new RegexParameterFilterRedis();
        clone.setDefaultValue(getDefaultValue());
        clone.setKey(getKey());
        clone.regex = regex;
        clone.setNormalize(getNormalize().clone());
        return clone;
    }

    @Override
    public List<String> getValues() {
        return null;
    }

}