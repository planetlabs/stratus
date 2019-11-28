/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import stratus.redis.config.EmbeddedRedisConfig;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.redis.rest.connection.*;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author joshfix
 * Created on 6/13/18
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {EmbeddedRedisConfig.class, RedisConfigProps.class, ThymeleafAutoConfiguration.class,
        RedisRepositoryImpl.class, DefaultConnectionService.class, ConnectionViewController.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void testView() throws Exception {
        this.mockMvc
                .perform(get(RedisCatalogRestConstants.BASE_PATH + "/" + ConnectionViewController.VIEW_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML_VALUE))
                .andExpect(view().name(ConnectionViewController.VIEW_NAME))
                .andExpect(model().attribute(
                        ConnectionViewController.POOL_STATUS_ATTRIBUTE_NAME, isA(ConnectionPoolStatusModel.class)))
                .andExpect(model().attribute(
                        ConnectionViewController.POOL_CONFIG_ATTRIBUTE_NAME, isA(ConnectionPoolConfigModel.class)))
                .andExpect(model().attribute(
                        ConnectionViewController.CLIENTS_ATTRIBUTE_NAME, isA(List.class)))
                .andExpect(model().attribute(
                        ConnectionViewController.CLIENTS_ATTRIBUTE_NAME, hasItems(isA(ClientModel.class))));
    }

}
