/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.geowebcache.filter.parameters.ParameterException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@XStreamAlias("stringParameterFilter")
public class StringParameterFilterRedis extends CaseNormalizingParameterFilterRedis {

    private static final long serialVersionUID = 7383381085250203901L;

    private List<String> values;

    public StringParameterFilterRedis() {
        values = new ArrayList<String>(0);
    }

    protected StringParameterFilterRedis readResolve() {
        super.readResolve();
        if (values == null) {
            values = new ArrayList<String>(0);
        }
        for(String value: values) {
            Preconditions.checkNotNull(value, "Value list included a null pointer.");
        }
        return this;
    }

    @Override
    public String apply(@Nullable String str) throws ParameterException {
        if (str == null || str.length() == 0) {
            return getDefaultValue();
        }

        str = getNormalize().apply(str);

        if(getLegalValues().contains(str)){
            return str;
        }

        throw new ParameterException(str + " violates filter for parameter " + getKey());
    }

    /**
     * @return the values the parameter can take.
     */
    @Override
    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Set the values the parameter can take
     */
    public void setValues(List<String> values) {
        Preconditions.checkNotNull(values);
        for(String value: values) {
            Preconditions.checkNotNull(value, "Value list included a null pointer.");
        }
        this.values = new ArrayList<String>(values);
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
        return getLegalValues().contains(getNormalize().apply(parameterValue));
    }

    @Override
    public StringParameterFilterRedis clone() {
        StringParameterFilterRedis clone = new StringParameterFilterRedis();
        clone.setDefaultValue(getDefaultValue());
        clone.setKey(getKey());
        if (values != null) {
            clone.values = new ArrayList<>(values);
        }
        clone.setNormalize(getNormalize().clone());
        return clone;
    }
}
