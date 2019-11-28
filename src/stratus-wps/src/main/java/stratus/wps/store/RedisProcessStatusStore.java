/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.store;

import net.opengis.wps10.ExecuteType;
import org.apache.commons.beanutils.BeanComparator;
import org.geoserver.wps.CompositeComparator;
import org.geoserver.wps.ProcessStatusStore;
import org.geoserver.wps.executor.ExecutionStatus;
import org.geotools.data.Query;
import org.geotools.util.logging.Logging;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import stratus.wps.model.StratusExecutionStatus;
import stratus.wps.redis.repository.StratusExecutionStatusRepository;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static stratus.wps.model.StratusExecutionStatus.toStratusExecutionStatus;

/**
 * Stratus implementation of ProcessStatusStore to store, retrieve and update the status of a WPS process execution to a Redis store.
 * This allows tracking of a WPS execution status across multiple nodes, in an asynchronous request.
 */
public class RedisProcessStatusStore implements ProcessStatusStore {

    private final StratusExecutionStatusRepository wpsRepo;

    static final Logger LOGGER = Logging.getLogger(RedisProcessStatusStore.class);

    static ExecuteType executeType;

    public RedisProcessStatusStore (StratusExecutionStatusRepository repo){
        this.wpsRepo = repo;
    }

    public void save(ExecutionStatus status) {
        executeType = status.getRequest();
        StratusExecutionStatus es = toStratusExecutionStatus(status);
        wpsRepo.save(es);
    }

    @Override
    public int remove(Filter filter) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Removing statuses matching " + filter);
        }

        int count = 0;
        for (StratusExecutionStatus status : wpsRepo.findAll()) {
            if (filter.evaluate(status)) {
                count++;
                wpsRepo.findById(status.getExecutionId()).ifPresent(wpsRepo::delete);
            }
        }

        return count;
    }

    @Override
    public List<ExecutionStatus> list(Query query) {
        List<StratusExecutionStatus> result = new ArrayList<>();

        // extract and filter
        Filter filter = query.getFilter();
        for (StratusExecutionStatus status : wpsRepo.findAll()) {
            if (filter.evaluate(status)) {
                result.add(status);
            }
        }

        // sort
        SortBy[] sorts = query.getSortBy();
        if (sorts != null) {
            List<Comparator<StratusExecutionStatus>> comparators = new ArrayList<>();
            for (SortBy sort : sorts) {
                if (sort == SortBy.NATURAL_ORDER) {
                    comparators.add(new BeanComparator("creationTime"));
                } else if (sort == SortBy.REVERSE_ORDER) {
                    comparators.add(Collections.reverseOrder(new BeanComparator("creationTime")));
                } else {
                    String property = sort.getPropertyName().getPropertyName();
                    // map property to ExecutionStatus values
                    if ("node".equalsIgnoreCase(property)) {
                        property = "nodeId";
                    } else if ("user".equalsIgnoreCase(property)) {
                        property = "userName";
                    } else if ("task".equalsIgnoreCase(property)) {
                        property = "task";
                    }
                    Comparator<StratusExecutionStatus> comparator = new BeanComparator(property);
                    if (sort.getSortOrder() == SortOrder.DESCENDING) {
                        comparator = Collections.reverseOrder(comparator);
                    }
                    comparators.add(comparator);
                }
            }

            if (comparators.size() > 1) {
                Comparator<StratusExecutionStatus> comparator = new CompositeComparator<>(comparators);
                Collections.sort(result, comparator);
            } else if (comparators.size() == 1) {
                Collections.sort(result, comparators.get(0));
            }
        }

        // paging
        Integer startIndex = query.getStartIndex();
        if (startIndex != null && startIndex > 0) {
            if (startIndex > result.size()) {
                result.clear();
            } else {
                result = result.subList(startIndex, result.size());
            }
        }
        if (result.size() > query.getMaxFeatures()) {
            result = result.subList(0, query.getMaxFeatures());
        }

        List<ExecutionStatus> convertedResult = new ArrayList<>();
        for (StratusExecutionStatus stratusExecutionStatus : result) {
            convertedResult.add(stratusExecutionStatus.toExecutionStatus(executeType));
        }

        return convertedResult;
    }

    @Override
    public ExecutionStatus get(String executionId) {
        Optional<StratusExecutionStatus> stratusExecutionStatus = wpsRepo.findById(executionId);
        if (stratusExecutionStatus.isPresent()) {
            return stratusExecutionStatus.get().toExecutionStatus(executeType);
        }
        return null;
    }

    @Override
    public ExecutionStatus remove(String executionId) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Removing status for execution id: " + executionId);
        }
        ExecutionStatus es = null;
        Optional<StratusExecutionStatus> stratusExecutionStatus = wpsRepo.findById(executionId);
        if (stratusExecutionStatus.isPresent()) {
            es = stratusExecutionStatus.get().toExecutionStatus(executeType);
        }
        wpsRepo.findById(executionId).ifPresent(wpsRepo::delete);

        return es;
    }

    @Override
    public boolean supportsPredicate() {
        return true;
    }

    @Override
    public boolean supportsPaging() {
        return false;
    }
}
