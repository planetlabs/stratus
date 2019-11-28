/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.catalog.workspace;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.WorkspaceInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import stratus.redis.catalog.info.WorkspaceInfoRedisImpl;

import java.util.List;

/**
 * Created by joshfix on 1/27/17.
 */
@Slf4j
@AllArgsConstructor
//@RestController
@RequestMapping("/rest/catalog")
public class StratusWorkspaceController {

    private final WorkspaceService service;

    @GetMapping("/workspace")
    public WorkspaceInfo getWorkspace(@RequestParam(name = "id", required = false) String id,
                                      @RequestParam(name = "name", required = false) String name) {
        return service.getWorkspace(id, name);
    }

    @GetMapping("/workspaces")
    public List<WorkspaceInfo> getWorkspaces() {
        return service.getWorkspaces();
    }

    @GetMapping(value = "/getWorkspaces")
    public Page<WorkspaceInfoRedisImpl> getWorkspaces(Pageable page) {
        return service.getWorkspaces(page);
    }

    @GetMapping(value = "/getWorkspacesByName/{name}")
    public Page<WorkspaceInfoRedisImpl> getWorkspacesByName(@PathVariable("name") String name, Pageable page) {
        return service.getWorkspacesByName(name, page);
    }

    @GetMapping(value = "/getCatalogInfoByName/{name}")
    public List<SearchResult> getCatalogInfoByName(@PathVariable("name") String name) {
        return service.getCatalogInfoByName(name);
    }

}
