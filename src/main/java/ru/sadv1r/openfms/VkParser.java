package ru.sadv1r.openfms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created on 6/24/15.
 *
 * @author sadv1r
 * @version 0.1
 */
public class VkParser extends Parser {
    private static final Logger logger = Logger.getLogger(VkParser.class);

    private static final int VK_MIN_ID;
    private static final int VK_MAX_ID;
    private static final int VK_STRING_MAX_LENGTH;
    private static final int VK_SITE_MAX_LENGTH;
    private static final int VK_STATUS_MAX_LENGTH;
    private static final int MAX_USERS_TO_PARSE_AT_ONCE;

    private static String fieldsToParse = "sex,bdate,city,country,photo_50,photo_100,photo_200_orig,photo_200,photo_400_orig," +
            "photo_max,photo_max_orig,photo_id,online,online_mobile,domain,has_mobile,contacts,connections,site," +
            "education,universities,schools,can_post,can_see_all_posts,can_see_audio,can_write_private_message,status," +
            "last_seen,relation,relatives,counters,screen_name,maiden_name,occupation,activities,interests,music,movies," +
            "tv,books,games,about,quotes,personal,nickname";

    static {
        FileInputStream fileInputStream;
        Properties properties = new Properties();

        try {
            fileInputStream = new FileInputStream("src/main/resources/vk.properties");
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует! Устанавливаем настройки по умолчанию...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        VK_MIN_ID = Integer.parseInt(properties.getProperty("vk.min.id", "1"));
        VK_MAX_ID = Integer.parseInt(properties.getProperty("vk.max.id", "999999999"));
        VK_STRING_MAX_LENGTH = Integer.parseInt(properties.getProperty("vk.string.max.length", "50"));
        VK_SITE_MAX_LENGTH = Integer.parseInt(properties.getProperty("vk.site.max.length", "256"));
        VK_STATUS_MAX_LENGTH = Integer.parseInt(properties.getProperty("vk.status.max.length", "140"));
        MAX_USERS_TO_PARSE_AT_ONCE = Integer.parseInt(properties.getProperty("max.users.to.parse.at.once", "500"));
    }


    /**
     * Получает текущий список полей для запроса из Вконтакте
     *
     * @return Список полей через запятую
     */
    public static String getFieldsToParse() {
        return fieldsToParse;
    }

    /**
     * Устанавливает список полей для запроса из Вконтакте
     *
     * @param fieldsToParse Список полей через запятую
     */
    public static void setFieldsToParse(String fieldsToParse) {
        VkParser.fieldsToParse = fieldsToParse;
    }

    /**
     * Парсит данные пользователя Вконтакте
     *
     * @param vkId Уникальный идентификатор пользователя <b>id</b>
     * @return Объект пользователя
     */
    public static VkUser parse(int vkId) throws IOException {
        logger.trace("Запуск метода parse(int)");
        if (correctVkIdFormat(vkId)) {
            ObjectMapper mapper = new ObjectMapper();
            String vkApiUsersGetURL = "https://api.vk.com/method/users.get?v=5.24&lang=ru&fields=" + fieldsToParse + "&user_ids=";
            String documentToParse = vkApiUsersGetURL + vkId;
            logger.trace("Получаем основные данные пользователя c id: " + vkId);
            JsonNode usersGetResult = getJsonNodeFromApi(documentToParse).get("response").get(0);
            logger.debug("Получены основные данные пользователя: \"" + vkId + "\"");
            return mapper.readValue(usersGetResult.toString(), VkUser.class);
        } else {
            logger.warn("id \"" + vkId + "\" выходит за допустимые пределы, указанные в конфигурационном файле");
            throw new IllegalArgumentException("id пользователя имеет недопустимый формат");
        }
    }

    /**
     * Перегрузка метода parse(int vkId)
     *
     * @param screenName Короткое имя пользователя
     * @return Объект пользователя
     * @throws IOException
     * @see #parse(int)
     */
    public static VkUser parse(String screenName) throws IOException {
        int vkId = getUserId(screenName);
        return parse(vkId);
    }

    /**
     * Парсит данные пользователей Вконтакте
     *
     * @param ids Уникальные идентификаторы пользователей <b>id</b>. Не более 500
     * @return Объекты пользователей
     * @throws IOException
     */
    public static ArrayList<VkUser> parse(int[] ids) throws IOException {
        int idsLength = ids.length;
        if (idsLength <= MAX_USERS_TO_PARSE_AT_ONCE) {
            for (int id : ids) {
                if (!correctVkIdFormat(id)) {
                    throw new IllegalArgumentException("id пользователя имеет недопустимый формат: " + id);
                }
            }
            ArrayList<VkUser> vkUsers = new ArrayList<>(idsLength);
            String stringIds = Arrays.toString(ids).replaceAll("[\\[ \\]]", "");
            ObjectMapper mapper = new ObjectMapper();
            String documentToParse = "https://api.vk.com/method/users.get?v=5.24&lang=ru&user_ids="
                    + stringIds + "&fields=" + fieldsToParse;
            JsonNode usersGetResult = getJsonNodeFromApi(documentToParse).get("response");
            for (int i = 0; i < idsLength; i++) {
                VkUser vkUser = mapper.readValue(usersGetResult.get(i).toString(), VkUser.class);
                vkUsers.add(vkUser);
            }
            return vkUsers;
        } else {
            throw new IllegalArgumentException("Количество идентификаторов не может быть больше " + MAX_USERS_TO_PARSE_AT_ONCE);
        }
    }

    /**
     * Перегрузка метода parse(int[] ids)
     *
     * @param screenNames Короткие имена пользователей
     * @return Объекты пользователей
     * @throws IOException
     * @see #parse(int[])
     */
    public static ArrayList<VkUser> parse(String[] screenNames) throws IOException {
        int screenNamesLength = screenNames.length;
        int[] ids = new int[screenNamesLength];
        for (int i = 0; i < screenNamesLength; i++) {
            ids[i] = getUserId(screenNames[i]);
        }
        return parse(ids);
    }

    /**
     * Проверяет id пользователя Вконтакте на соответсвие формату
     *
     * @param vkId Уникальный идентификатор пользователя <b>id</b>
     * @return true, если формат id правильный. false - если неправильный
     */
    private static boolean correctVkIdFormat(int vkId) {
        return vkId > VK_MIN_ID && vkId < VK_MAX_ID;
    }

    /**
     * Получает id пользователя Вконтакте по короткому имени
     *
     * @param screenName Короткое имя пользователя
     * @return Уникальный идентификатор пользователя <b>id</b>
     * @throws IOException
     */
    private static int getUserId(String screenName) throws IOException {
        logger.trace("Запуск метода getUserId(String)");
        String vkApiResolveScreenNameUrl = "https://api.vk.com/method/utils.resolveScreenName?screen_name=";
        logger.trace("Получаем id пользователя \"" + screenName + "\"");
        JsonNode resolveScreenNameResult = getJsonNodeFromApi(vkApiResolveScreenNameUrl + screenName).get("response");

        if (resolveScreenNameResult.hasNonNull("object_id")) {
            int vkId = resolveScreenNameResult.get("object_id").asInt();
            logger.debug("Получен id пользователя \"" + screenName + "\": " + vkId);
            return vkId;
        } else {
            logger.warn("Пользователь с коротким именем \"" + screenName + "\" не найден");
            throw new IllegalArgumentException("Пользователь с таким коротким именем не найден");
        }
    }

    private static Set<Integer> parseFriends(int vkId) throws IOException {
        String documentToParse = "https://api.vk.com/method/friends.get?v=5.24&user_id=" + vkId;
        JsonNode friendsGetResult = getJsonNodeFromApi(documentToParse);
        if (!friendsGetResult.hasNonNull("response")) {
            return new HashSet<Integer>();
        } else {
            friendsGetResult = friendsGetResult.get("response").get("items");
            Set<Integer> friends = new HashSet<>(friendsGetResult.size());
            for (JsonNode node : friendsGetResult) {
                friends.add(node.asInt());
            }
            return friends;
        }
    }
}
