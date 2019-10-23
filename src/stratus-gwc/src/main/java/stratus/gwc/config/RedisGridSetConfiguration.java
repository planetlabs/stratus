/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.gwc.redis.data.GridSetRedisImpl;
import stratus.gwc.redis.repository.GridSetRepository;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.repository.RedisRepositoryImpl;
import org.geowebcache.config.ConfigurationPersistenceException;
import org.geowebcache.config.GridSetConfiguration;
import org.geowebcache.grid.GridSet;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GeoWebCache GridSetConfiguration which stores GridSets in a SpringData repository.
 * 
 * @author smithkm
 *
 */
@Primary
@Service("stratusGWCGridSetConfiguration")
public class RedisGridSetConfiguration extends BaseRedisConfiguration implements GridSetConfiguration {

    private final GridSetRepository gsRepository;

    public RedisGridSetConfiguration(RedisRepositoryImpl repository, RedisConfigProps configProps, GridSetRepository tlRepository) {
        super(repository, configProps, "Stratus GridSet Catalog");
        this.gsRepository = tlRepository;
    }

    @Override
    public Collection<GridSet> getGridSets() {
        List<GridSet> gridSets = new ArrayList<>();
        for (GridSetRedisImpl gridSet : gsRepository.findAll()) {
            gridSets.add(resolve(gridSet));
        }
        return gridSets;
    }

    @Override
    public Optional<GridSet> getGridSet(String gridSetName) {
        return gsRepository.findById(gridSetName).map(GridSetRedisImpl::makeGridSet);
    }

    @Override
    public Set<String> getGridSetNames() {
        return getGridSets().stream().map(GridSet::getName).collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public void removeGridSet(String gridSetName)
            throws NoSuchElementException, IllegalArgumentException {
        if (containsGridSet(gridSetName)) {
            try {
                gsRepository.findById(gridSetName).ifPresent(gsRepository::delete);
            } catch (DataAccessException ex) {
                throw new ConfigurationPersistenceException(ex);
            }
        } else {
            throw new NoSuchElementException("Cannot delete gridSet " + gridSetName + " - gridSet doesn't exist");
        }
    }

    @Transactional
    @Override
    public void modifyGridSet(GridSet tl) throws NoSuchElementException {
        if(tl instanceof GridSet) {
            if (containsGridSet(tl.getName())) {
                save((GridSet) tl);
            } else {
                throw new NoSuchElementException("Cannot modify gridSet " + tl.getName() + " - gridSet doesn't exist");
            }
        } else {
            throw new IllegalArgumentException("Cannot modify gridSet " + tl.getName() + " - gridSet is not a GridSet");
        }
    }

    @Transactional
    @Override
    public void renameGridSet(String oldName, String newName)
            throws NoSuchElementException, IllegalArgumentException {

        GridSetRedisImpl oldGridSet = gsRepository.findById(oldName).orElseThrow(() ->
                new NoSuchElementException("Cannot rename gridSet " + oldName + " - gridSet doesn't exist"));

        if (gsRepository.findById(newName).isPresent()) {
            throw new IllegalArgumentException("Cannot rename gridSet " + oldName + " to " + newName + " - gridSet already exists");
        }
        try {

            gsRepository.findById(oldName).ifPresent(gsRepository::delete);
            oldGridSet.setName(newName);
            gsRepository.save(oldGridSet);
        } catch (DataAccessException ex) {
            throw new ConfigurationPersistenceException(ex);
        }
    }

    @Transactional
    @Override
    public void addGridSet(GridSet tl) throws IllegalArgumentException {
        if(tl instanceof GridSet) {
            if (containsGridSet(tl.getName())) {
                throw new IllegalArgumentException("Cannot add gridSet " + tl.getName() + " - gridSet already exists");
            }
            save((GridSet) tl);
        } else {
            throw new IllegalArgumentException("Cannot add gridSet " + tl.getName() + " - gridSet is not a GridSet");
        }
    }

    /**
     * Does the configuration have a gridset by the given name
     * @param gridSetName
     * @return
     */
    public boolean containsGridSet(String gridSetName) {
        return getGridSet(gridSetName).isPresent();
    }

    @Override
    public boolean canSave(GridSet gridSet) {
        return Objects.nonNull(gridSet.getName());
    }

    /**
     * Converts a {@link GridSet} to a {@link GridSetRedisImpl} and saves it to
     * the {@link #gsRepository}
     *
     * @param gridSet The gridSet to save
     */
    protected void save(GridSet gridSet) {
        GridSetRedisImpl redisGridSet = new GridSetRedisImpl(gridSet);
        try {
            gsRepository.save(redisGridSet);
        } catch (DataAccessException ex) {
            throw new ConfigurationPersistenceException(ex);
        }
    }

    /**
     * Converts a {@link GridSetRedisImpl} retrieved from the {@link #gsRepository} into a
     * {@link GridSet} that can be returned
     *
     * @param gridSetRedis gridSet retrieved from redis
     * @return gridSet to be used by geoserver
     */
    protected GridSet resolve(GridSetRedisImpl gridSetRedis) throws IllegalArgumentException {
        if(Objects.isNull(gridSetRedis)) {
            return null;
        }
        return gridSetRedis.makeGridSet();
    }
}
