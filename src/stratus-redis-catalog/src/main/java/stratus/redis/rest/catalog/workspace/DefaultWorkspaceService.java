/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.catalog.workspace;

import stratus.redis.catalog.RedisCatalogFacade;
import stratus.redis.catalog.info.WorkspaceInfoRedisImpl;
import stratus.redis.catalog.repository.WorkspaceRepository;
import lombok.AllArgsConstructor;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author joshfix
 * Created on 6/15/18
 */
@Service
@AllArgsConstructor
public class DefaultWorkspaceService implements WorkspaceService {

    private final RedisCatalogFacade facade;
    private final WorkspaceRepository workspaceRepository;

    @Override
    public WorkspaceInfo getWorkspace(String id, String name) {
        if (null != id && !id.isEmpty()) {
            return facade.getWorkspace(id);
        } else if (null != name && !name.isEmpty()) {
            return facade.getWorkspaceByName(name);
        } else {
            return facade.getDefaultWorkspace();
        }
    }

    @Override
    public List<WorkspaceInfo> getWorkspaces() {
        return facade.getWorkspaces();
    }

    @Override
    public Page<WorkspaceInfoRedisImpl> getWorkspaces(Pageable page) {
        return workspaceRepository.findAll(page);
    }

    @Override
    public Page<WorkspaceInfoRedisImpl> getWorkspacesByName(String name, Pageable page) {
        return workspaceRepository.findByName(name, page);
    }

    @Override
    public List<SearchResult> getCatalogInfoByName(String name) {
        List<LayerInfo> layerInfos = facade.getLayers();
        List<WorkspaceInfo> workspaceInfos = facade.getWorkspaces();
        List<LayerGroupInfo> groupInfos = facade.getLayerGroups();

        if (name.equals("*")) {
            List<SearchResult> catalogSearchResults = layerInfos.stream()
                    .map(ri -> new SearchResult(ri))
                    .collect(Collectors.toList());

            List<SearchResult> workspaceSearchResults = workspaceInfos.stream()
                    .map(ri -> new SearchResult(ri))
                    .collect(Collectors.toList());

            List<SearchResult> groupSearchResults = groupInfos.stream()
                    .map(ri -> new SearchResult(ri))
                    .collect(Collectors.toList());

            catalogSearchResults.addAll(workspaceSearchResults);
            catalogSearchResults.addAll(groupSearchResults);
            return catalogSearchResults;
        }

        List<SearchResult> catalogSearchResults = layerInfos.stream()
                .filter(ri -> catalogResultHasName(ri, name))
                .map(ri -> new SearchResult(ri))
                .collect(Collectors.toList());

        List<SearchResult> workspaceSearchResults = workspaceInfos.stream()
                .filter(ri -> catalogResultHasName(ri, name))
                .map(ri -> new SearchResult(ri))
                .collect(Collectors.toList());

        List<SearchResult> groupSearchResults = groupInfos.stream()
                .filter(ri -> catalogResultHasName(ri, name))
                .map(ri -> new SearchResult(ri))
                .collect(Collectors.toList());

        catalogSearchResults.addAll(workspaceSearchResults);
        catalogSearchResults.addAll(groupSearchResults);
        return catalogSearchResults;
    }

    private boolean catalogResultHasName(LayerInfo ri, String name) {
        boolean out = false;
        name = name.toUpperCase();

        if (ri.getName() != null && ri.getName().toUpperCase().contains(name))
            return true;

        if (ri.getAbstract() != null && ri.getAbstract().toUpperCase().contains(name))
            return true;

        if (ri.getName() != null && ri.getName().toUpperCase().contains(name))
            return true;

        return out;
    }

    private boolean catalogResultHasName(WorkspaceInfo wi, String name) {
        boolean out = false;
        name = name.toUpperCase();

        if (wi.getName() != null && wi.getName().toUpperCase().contains(name))
            return true;

        return out;
    }

    private boolean catalogResultHasName(LayerGroupInfo ri, String name) {
        boolean out = false;
        name = name.toUpperCase();

        if (ri.getName() != null && ri.getName().toUpperCase().contains(name))
            return true;

        if (ri.getAbstract() != null && ri.getAbstract().toUpperCase().contains(name))
            return true;

        if (ri.getTitle() != null && ri.getTitle().toUpperCase().contains(name))
            return true;

        return out;
    }
}
