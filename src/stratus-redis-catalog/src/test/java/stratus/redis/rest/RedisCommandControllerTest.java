/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest;

import stratus.redis.config.EmbeddedRedisConfig;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.redis.rest.command.DefaultRedisCommandService;
import stratus.redis.rest.command.RedisCommandController;
import io.lettuce.core.protocol.CommandType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author joshfix
 * Created on 6/13/18
 */

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {EmbeddedRedisConfig.class, RedisRepositoryImpl.class, RedisCommandController.class,
        DefaultRedisCommandService.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisCommandControllerTest {

    private MockMvc mockMvc;

    public static final String TEST_STRING_KEY = "test_string_key";
    public static final String TEST_STRING_VALUE = "test_string_value";
    public static final String TEST_SET_KEY = "test_set_key";
    public static final String TEST_SET_MEMBER_1 = "test_set_member_1";
    public static final String TEST_SET_MEMBER_2 = "test_set_member_2";
    public static final String TEST_HASH_KEY = "test_hash_key";
    public static final String TEST_HASH_FIELD = "test_hash_field";
    public static final String TEST_HASH_VALUE = "test_hash_value";
    public static final String EXEC_URL = RedisCatalogRestConstants.BASE_PATH + "/exec";

    @Autowired
    private RedisCommandController controller;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        addRedisData();
    }

    @Test
    public void testKeys() throws Exception {
        mockMvc.perform(
                post(EXEC_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(CommandType.KEYS + " *"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[*]", hasItems(containsString(TEST_STRING_KEY))))
                .andExpect(jsonPath("$[*]", hasItems(containsString(TEST_SET_KEY))))
                .andExpect(jsonPath("$[*]", hasItems(containsString(TEST_HASH_KEY))));
    }

    @Test
    public void testString() throws Exception {
        mockMvc.perform(
                post(EXEC_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(CommandType.GET + " " + TEST_STRING_KEY))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[*]", hasItems(containsString(TEST_STRING_VALUE))));
    }

    @Test
    public void testSet() throws Exception {
        mockMvc.perform(
                post(EXEC_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(CommandType.SMEMBERS + " " + TEST_SET_KEY))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[*]", hasItems(containsString(TEST_SET_MEMBER_1))))
                .andExpect(jsonPath("$[*]", hasItems(containsString(TEST_SET_MEMBER_2))));
    }

    @Test
    public void testHash() throws Exception {
        mockMvc.perform(
                post(EXEC_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(CommandType.HGET + " " + TEST_HASH_KEY + " " + TEST_HASH_FIELD))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[*]", hasItems(containsString(TEST_HASH_VALUE))));

        mockMvc.perform(
                post(EXEC_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(CommandType.HGETALL + " " + TEST_HASH_KEY))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[*]", hasItems(containsString(TEST_HASH_FIELD))))
                .andExpect(jsonPath("$[*]", hasItems(containsString(TEST_HASH_VALUE))));
    }

    private void addRedisData() throws Exception {
        mockMvc.perform(
                post(EXEC_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(CommandType.SET + " " + TEST_STRING_KEY + " " + TEST_STRING_VALUE))
                .andExpect(status().isOk());

        mockMvc.perform(
                post(EXEC_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(CommandType.SADD + " " + TEST_SET_KEY + " " + TEST_SET_MEMBER_1 + " " + TEST_SET_MEMBER_2))
                .andExpect(status().isOk());

        mockMvc.perform(
                post(EXEC_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(CommandType.HSET + " " + TEST_HASH_KEY + " " + TEST_HASH_FIELD + " " + TEST_HASH_VALUE))
                .andExpect(status().isOk());
    }

}
