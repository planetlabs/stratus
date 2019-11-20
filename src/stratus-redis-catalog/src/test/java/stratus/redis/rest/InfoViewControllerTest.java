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
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.redis.rest.info.DefaultInfoService;
import stratus.redis.rest.info.InfoViewController;

import java.util.Properties;

import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author joshfix
 * Created on 6/13/18
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {EmbeddedRedisConfig.class, RedisRepositoryImpl.class, InfoViewController.class,
        DefaultInfoService.class, ThymeleafAutoConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class InfoViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void testView() throws Exception {
        this.mockMvc
                .perform(get(RedisCatalogRestConstants.BASE_PATH + "/" + InfoViewController.VIEW_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML_VALUE))
                .andExpect(view().name(InfoViewController.VIEW_NAME))
                .andExpect(model().attribute(InfoViewController.INFO_ATTRIBUTE_NAME, isA(Properties.class)));
    }

}
